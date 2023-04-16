/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import de.muspellheim.activitysampling.domain.ActivitiesService;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.utilities.EventEmitter;
import de.muspellheim.utilities.Exceptions;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class ActivitySamplingViewModel {
  private final ActivitiesService activitiesService;
  private Locale locale;
  private final Clock clock;

  // State
  private Duration interval = Duration.ofMinutes(20);
  private final ObjectProperty<Duration> countdown = new SimpleObjectProperty<>(interval);
  private final BooleanProperty countdownActive = new SimpleBooleanProperty(false);
  private final BooleanProperty intervalLogged = new SimpleBooleanProperty(false);

  /* *************************************************************************
   *                                                                         *
   * Constructors                                                            *
   *                                                                         *
   **************************************************************************/

  ActivitySamplingViewModel(ActivitiesService activitiesService) {
    this(activitiesService, Locale.getDefault(), Clock.systemDefaultZone());
  }

  ActivitySamplingViewModel(ActivitiesService activitiesService, Locale locale, Clock clock) {
    this.activitiesService = activitiesService;
    this.locale = locale;
    this.clock = clock;
    // TODO Make default interval configurable; use when countdown off
  }

  /* *************************************************************************
   *                                                                         *
   * Events                                                                  *
   *                                                                         *
   **************************************************************************/

  private final EventEmitter<Void> onCountdownElapsed = new EventEmitter<>();

  public void addOnCountdownElapsedListener(Consumer<Void> listener) {
    onCountdownElapsed.addListener(listener);
  }

  public void removeOnCountdownElapsedListener(Consumer<Void> listener) {
    onCountdownElapsed.removeListener(listener);
  }

  private final EventEmitter<List<String>> onError = new EventEmitter<>();

  public void addOnErrorListener(Consumer<List<String>> listener) {
    onError.addListener(listener);
  }

  public void removeOnErrorListener(Consumer<List<String>> listener) {
    onError.removeListener(listener);
  }

  /* *************************************************************************
   *                                                                         *
   * Properties                                                              *
   *                                                                         *
   **************************************************************************/

  // --- stopMenuItemDisable

  private final ObservableBooleanValue stopMenuItemDisable =
      Bindings.createBooleanBinding(() -> countdownActive.not().get(), countdownActive);

  public ObservableBooleanValue stopMenuItemDisableProperty() {
    return stopMenuItemDisable;
  }

  // --- formDisable

  private final ObservableBooleanValue formDisable = countdownActive.and(intervalLogged);

  public ObservableBooleanValue formDisableProperty() {
    return formDisable;
  }

  // --- activityText

  private final StringProperty activityText = new SimpleStringProperty("");

  public StringProperty activityTextProperty() {
    return activityText;
  }

  // --- logButtonDisable

  private final ObservableBooleanValue logButtonDisable =
      Bindings.createBooleanBinding(() -> activityText.get().isBlank(), activityText)
          .or(countdownActive.and(intervalLogged));

  public ObservableBooleanValue logButtonDisableProperty() {
    return logButtonDisable;
  }

  // --- countdownLabelText

  private final ObservableStringValue countdownLabelText =
      Bindings.createStringBinding(
          () -> {
            var time = LocalTime.ofSecondOfDay(countdown.get().toSeconds());
            return DateTimeFormatter.ofPattern("HH:mm:ss").withLocale(locale).format(time);
          },
          countdown);

  public ObservableStringValue countdownLabelTextProperty() {
    return countdownLabelText;
  }

  // --- countdownProgress

  private final ObservableDoubleValue countdownProgress =
      Bindings.createDoubleBinding(
          () -> {
            if (interval.isZero()) {
              return 0.0;
            }

            var elapsedSeconds = (double) interval.minus(countdown.get()).getSeconds();
            return elapsedSeconds / interval.toSeconds();
          },
          countdown);

  public ObservableDoubleValue countdownProgressProperty() {
    return countdownProgress;
  }

  // --- recentActivityItems

  private final ObservableList<ActivityItem> recentActivityItems =
      FXCollections.observableArrayList();

  public ObservableList<ActivityItem> getRecentActivities() {
    return recentActivityItems;
  }

  // --- hoursTodayLabelText

  private final ReadOnlyStringWrapper hoursTodayLabelText = new ReadOnlyStringWrapper("00:00");

  public ObservableStringValue hoursTodayLabelTextProperty() {
    return hoursTodayLabelText.getReadOnlyProperty();
  }

  // --- hoursYesterdayLabelText

  private final ReadOnlyStringWrapper hoursYesterdayLabelText = new ReadOnlyStringWrapper("00:00");

  public ObservableStringValue hoursYesterdayLabelTextProperty() {
    return hoursYesterdayLabelText.getReadOnlyProperty();
  }

  // --- hoursThisWeekLabelText

  private final ReadOnlyStringWrapper hoursThisWeekLabelText = new ReadOnlyStringWrapper("00:00");

  public ObservableStringValue hoursThisWeekLabelTextProperty() {
    return hoursThisWeekLabelText.getReadOnlyProperty();
  }

  // --- hoursThisMonthLabelText

  private final ReadOnlyStringWrapper hoursThisMonthLabelText = new ReadOnlyStringWrapper("00:00");

  public ObservableStringValue hoursThisMonthLabelTextProperty() {
    return hoursThisMonthLabelText.getReadOnlyProperty();
  }

  /* *************************************************************************
   *                                                                         *
   * Public API                                                              *
   *                                                                         *
   **************************************************************************/

  public void run() {
    load();
  }

  public void load() {
    RecentActivities recentActivities;
    try {
      recentActivities = activitiesService.getRecentActivities();
    } catch (Exception e) {
      var messages = Exceptions.collectExceptionMessages("Failed to load activities.", e);
      onError.emit(messages);
      return;
    }

    var items = new ArrayList<ActivityItem>();
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale);
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale);
    for (var day : recentActivities.workingDays()) {
      items.add(new ActivityItem(day.date().format(dateFormatter)));
      for (var activity : day.activities()) {
        items.add(
            new ActivityItem(
                activity.timestamp().format(timeFormatter) + " - " + activity.description(),
                activity.description()));
      }
    }

    var timeFormat = "%1$02d:%2$02d";
    var timeSummary = recentActivities.timeSummary();
    hoursTodayLabelText.set(
        timeFormat.formatted(
            timeSummary.hoursToday().toHours(), timeSummary.hoursToday().toMinutesPart()));
    hoursYesterdayLabelText.set(
        timeFormat.formatted(
            timeSummary.hoursYesterday().toHours(), timeSummary.hoursYesterday().toMinutesPart()));
    hoursThisWeekLabelText.set(
        timeFormat.formatted(
            timeSummary.hoursThisWeek().toHours(), timeSummary.hoursThisWeek().toMinutesPart()));
    hoursThisMonthLabelText.set(
        timeFormat.formatted(
            timeSummary.hoursThisMonth().toHours(), timeSummary.hoursThisMonth().toMinutesPart()));
    this.recentActivityItems.setAll(items);
  }

  public void logActivity() {
    try {
      activitiesService.logActivity(LocalDateTime.now(clock), interval, activityText.get());
      intervalLogged.set(true);
    } catch (Exception e) {
      var messages = Exceptions.collectExceptionMessages("Failed to log activity.", e);
      onError.emit(messages);
      return;
    }

    load();
  }

  public void startCountdown(Duration interval) {
    this.interval = interval;
    countdown.set(interval);
    countdownActive.set(true);
    intervalLogged.set(true);
  }

  public void progressCountdown(Duration duration) {
    if (!countdownActive.get()) {
      return;
    }

    countdown.set(countdown.get().minus(duration));
    if (countdown.get().isZero()) {
      intervalLogged.set(false);
      onCountdownElapsed.emit(null);
      countdown.set(interval);
    }
  }

  public void stopCountdown() {
    countdownActive.set(false);
  }
}
