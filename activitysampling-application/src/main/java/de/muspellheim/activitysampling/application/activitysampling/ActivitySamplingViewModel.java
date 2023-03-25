/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import de.muspellheim.activitysampling.application.shared.Exceptions;
import de.muspellheim.activitysampling.domain.ActivitiesService;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.util.EventEmitter;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ActivitySamplingViewModel {
  private final ActivitiesService activitiesService;
  private final Locale locale;
  private final Clock clock;

  // Events
  private final EventEmitter<Void> onCountdownElapsed = new EventEmitter<>();
  private final EventEmitter<List<String>> onError = new EventEmitter<>();

  // State
  private final ReadOnlyObjectWrapper<Duration> interval;
  private final ReadOnlyBooleanWrapper intervalLogged;
  private final ReadOnlyBooleanWrapper countdownActive;
  private final ReadOnlyObjectWrapper<Duration> countdown;

  // Properties
  private final BooleanExpression stopMenuItemDisable;
  private final StringProperty activityText;
  private final BooleanExpression formDisable;
  private final BooleanExpression logButtonDisable;
  private final StringExpression countdownLabelText;
  private final DoubleExpression countdownProgress;
  private final ObservableList<ActivityItem> recentActivities;
  private final ReadOnlyStringWrapper hoursTodayLabelText;
  private final ReadOnlyStringWrapper hoursYesterdayLabelText;
  private final ReadOnlyStringWrapper hoursThisWeekLabelText;
  private final ReadOnlyStringWrapper hoursThisMonthLabelText;

  public ActivitySamplingViewModel(ActivitiesService activitiesService) {
    this(activitiesService, Locale.getDefault(), Clock.systemDefaultZone());
  }

  public ActivitySamplingViewModel(
      ActivitiesService activitiesService, Locale locale, Clock clock) {
    this.activitiesService = activitiesService;
    this.locale = locale;
    this.clock = clock;
    // TODO Make default interval configurable; use when countdown off
    interval = new ReadOnlyObjectWrapper<>(Duration.ofMinutes(20));
    intervalLogged = new ReadOnlyBooleanWrapper(false);
    countdownActive = new ReadOnlyBooleanWrapper(false);
    countdown = new ReadOnlyObjectWrapper<>(interval.get());

    stopMenuItemDisable =
        Bindings.createBooleanBinding(() -> countdownActive.not().get(), countdownActive);
    activityText = new SimpleStringProperty("");
    formDisable = countdownActive.and(intervalLogged);
    logButtonDisable =
        Bindings.createBooleanBinding(() -> activityText.get().isBlank(), activityText)
            .or(countdownActive.and(intervalLogged));
    countdownLabelText =
        Bindings.createStringBinding(
            () -> {
              var time = LocalTime.ofSecondOfDay(countdown.get().toSeconds());
              return DateTimeFormatter.ofPattern("HH:mm:ss").withLocale(locale).format(time);
            },
            interval,
            countdown);
    countdownProgress =
        Bindings.createDoubleBinding(
            () -> {
              if (interval.get().isZero()) {
                return 0.0;
              }

              var elapsedSeconds = (double) interval.get().minus(countdown.get()).getSeconds();
              return elapsedSeconds / interval.get().toSeconds();
            },
            interval,
            countdown);
    recentActivities = FXCollections.observableArrayList();
    hoursTodayLabelText = new ReadOnlyStringWrapper("00:00");
    hoursYesterdayLabelText = new ReadOnlyStringWrapper("00:00");
    hoursThisWeekLabelText = new ReadOnlyStringWrapper("00:00");
    hoursThisMonthLabelText = new ReadOnlyStringWrapper("00:00");
  }

  public void addOnCountdownElapsedListener(Consumer<Void> listener) {
    onCountdownElapsed.addListener(listener);
  }

  public void removeOnCountdownElapsedListener(Consumer<Void> listener) {
    onCountdownElapsed.removeListener(listener);
  }

  public void addOnErrorListener(Consumer<List<String>> listener) {
    onError.addListener(listener);
  }

  public void removeOnErrorListener(Consumer<List<String>> listener) {
    onError.removeListener(listener);
  }

  public BooleanExpression stopMenuItemDisableProperty() {
    return stopMenuItemDisable;
  }

  public StringProperty activityTextProperty() {
    return activityText;
  }

  public BooleanExpression formDisableProperty() {
    return formDisable;
  }

  public BooleanExpression logButtonDisableProperty() {
    return logButtonDisable;
  }

  public StringExpression countdownLabelTextProperty() {
    return countdownLabelText;
  }

  public DoubleExpression countdownProgressProperty() {
    return countdownProgress;
  }

  public ObservableList<ActivityItem> getRecentActivities() {
    return recentActivities;
  }

  public ReadOnlyStringProperty hoursTodayLabelTextProperty() {
    return hoursTodayLabelText.getReadOnlyProperty();
  }

  public ReadOnlyStringProperty hoursYesterdayLabelTextProperty() {
    return hoursYesterdayLabelText.getReadOnlyProperty();
  }

  public ReadOnlyStringProperty hoursThisWeekLabelTextProperty() {
    return hoursThisWeekLabelText.getReadOnlyProperty();
  }

  public ReadOnlyStringProperty hoursThisMonthLabelTextProperty() {
    return hoursThisMonthLabelText.getReadOnlyProperty();
  }

  public void run() {
    load();
  }

  public void load() {
    RecentActivities recentActivities;
    try {
      recentActivities =
          activitiesService.getRecentActivities(LocalDate.now(clock), Period.ofDays(10));
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
    this.recentActivities.setAll(items);
  }

  public void logActivity() {
    try {
      activitiesService.logActivity(LocalDateTime.now(clock), interval.get(), activityText.get());
      intervalLogged.set(true);
    } catch (Exception e) {
      var messages = Exceptions.collectExceptionMessages("Failed to log activity.", e);
      onError.emit(messages);
      return;
    }

    load();
  }

  public void setActivity(String description) {
    activityText.set(description);
  }

  public void startCountdown(Duration interval) {
    this.interval.set(interval);
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
      countdown.set(interval.get());
    }
  }

  public void stopCountdown() {
    countdownActive.set(false);
  }
}
