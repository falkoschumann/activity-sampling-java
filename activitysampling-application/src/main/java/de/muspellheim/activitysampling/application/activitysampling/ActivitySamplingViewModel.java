/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import de.muspellheim.activitysampling.domain.ActivitiesService;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.common.util.Durations;
import de.muspellheim.common.util.EventEmitter;
import de.muspellheim.common.util.OutputTracker;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
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
  private final Locale locale;
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

  private final EventEmitter<LocalDateTime> countdownElapsed = new EventEmitter<>();

  void addCountdownElapsedListener(Consumer<LocalDateTime> listener) {
    countdownElapsed.addListener(listener);
  }

  void removeCountdownElapsedListener(Consumer<LocalDateTime> listener) {
    countdownElapsed.removeListener(listener);
  }

  OutputTracker<LocalDateTime> getCountdownElapsedTracker() {
    return new OutputTracker<>(countdownElapsed);
  }

  private final EventEmitter<Throwable> errorOccurred = new EventEmitter<>();

  void addErrorOccurredListener(Consumer<Throwable> listener) {
    errorOccurred.addListener(listener);
  }

  void removeErrorOccurredListener(Consumer<Throwable> listener) {
    errorOccurred.removeListener(listener);
  }

  OutputTracker<Throwable> getErrorOccurredTracker() {
    return new OutputTracker<>(errorOccurred);
  }

  /* *************************************************************************
   *                                                                         *
   * Properties                                                              *
   *                                                                         *
   **************************************************************************/

  // --- stopMenuItemDisable

  private final ObservableBooleanValue stopMenuItemDisable =
      Bindings.createBooleanBinding(() -> countdownActive.not().get(), countdownActive);

  ObservableBooleanValue stopMenuItemDisableProperty() {
    return stopMenuItemDisable;
  }

  // --- formDisable

  private final ObservableBooleanValue formDisable = countdownActive.and(intervalLogged);

  ObservableBooleanValue formDisableProperty() {
    return formDisable;
  }

  // --- activityText

  private final StringProperty activityText = new SimpleStringProperty("");

  StringProperty activityTextProperty() {
    return activityText;
  }

  void setActivityText(String value) {
    activityTextProperty().set(value);
  }

  // --- logButtonDisable

  private final ObservableBooleanValue logButtonDisable =
      Bindings.createBooleanBinding(() -> activityText.get().isBlank(), activityText);

  ObservableBooleanValue logButtonDisableProperty() {
    return logButtonDisable;
  }

  // --- countdownLabelText

  private final ObservableStringValue countdownLabelText =
      Bindings.createStringBinding(
          () -> Durations.format(countdown.get(), FormatStyle.MEDIUM), countdown);

  ObservableStringValue countdownLabelTextProperty() {
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

  ObservableDoubleValue countdownProgressProperty() {
    return countdownProgress;
  }

  // --- recentActivityItems

  private final ObservableList<ActivityItem> recentActivityItems =
      FXCollections.observableArrayList();

  ObservableList<ActivityItem> getRecentActivities() {
    return recentActivityItems;
  }

  // --- hoursTodayLabelText

  private final ReadOnlyStringWrapper hoursTodayLabelText = new ReadOnlyStringWrapper("00:00");

  ObservableStringValue hoursTodayTextProperty() {
    return hoursTodayLabelText.getReadOnlyProperty();
  }

  // --- hoursYesterdayLabelText

  private final ReadOnlyStringWrapper hoursYesterdayLabelText = new ReadOnlyStringWrapper("00:00");

  ObservableStringValue hoursYesterdayTextProperty() {
    return hoursYesterdayLabelText.getReadOnlyProperty();
  }

  // --- hoursThisWeekLabelText

  private final ReadOnlyStringWrapper hoursThisWeekLabelText = new ReadOnlyStringWrapper("00:00");

  ObservableStringValue hoursThisWeekTextProperty() {
    return hoursThisWeekLabelText.getReadOnlyProperty();
  }

  // --- hoursThisMonthLabelText

  private final ReadOnlyStringWrapper hoursThisMonthLabelText = new ReadOnlyStringWrapper("00:00");

  ObservableStringValue hoursThisMonthTextProperty() {
    return hoursThisMonthLabelText.getReadOnlyProperty();
  }

  /* *************************************************************************
   *                                                                         *
   * Public API                                                              *
   *                                                                         *
   **************************************************************************/

  void load() {
    try {
      var recentActivities = activitiesService.getRecentActivities();
      updateActivityItems(recentActivities);
      updateTimeSummary(recentActivities);
    } catch (Exception e) {
      errorOccurred.emit(new Exception("Failed to load activities.", e));
    }
  }

  private void updateActivityItems(RecentActivities recentActivities) {
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
    recentActivityItems.setAll(items);
  }

  private void updateTimeSummary(RecentActivities recentActivities) {
    var timeSummary = recentActivities.timeSummary();
    hoursTodayLabelText.set(Durations.format(timeSummary.hoursToday(), FormatStyle.SHORT));
    hoursYesterdayLabelText.set(Durations.format(timeSummary.hoursYesterday(), FormatStyle.SHORT));
    hoursThisWeekLabelText.set(Durations.format(timeSummary.hoursThisWeek(), FormatStyle.SHORT));
    hoursThisMonthLabelText.set(Durations.format(timeSummary.hoursThisMonth(), FormatStyle.SHORT));
  }

  void logActivity() {
    try {
      activitiesService.logActivity(
          new Activity(LocalDateTime.now(clock), interval, activityText.get()));
      intervalLogged.set(true);
      load();
    } catch (Exception e) {
      errorOccurred.emit(new Exception("Failed to log activity.", e));
    }
  }

  void startCountdown(Duration interval) {
    this.interval = interval;
    countdown.set(interval);
    countdownActive.set(true);
    intervalLogged.set(true);
  }

  void progressCountdown(Duration duration) {
    if (!countdownActive.get()) {
      return;
    }

    countdown.set(countdown.get().minus(duration));
    if (countdown.get().isZero()) {
      intervalLogged.set(false);
      countdownElapsed.emit(LocalDateTime.now(clock));
      countdown.set(interval);
    }
  }

  void stopCountdown() {
    countdownActive.set(false);
  }
}
