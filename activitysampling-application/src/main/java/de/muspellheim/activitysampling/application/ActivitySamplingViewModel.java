package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.function.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;

class ActivitySamplingViewModel {
  private final ActivitiesService activitiesService;
  private final Runnable onCountdownElapsed;
  private final Consumer<String> onError;

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

  ActivitySamplingViewModel(
      ActivitiesService activitiesService, Runnable onCountdownElapsed, Consumer<String> onError) {
    this.activitiesService = activitiesService;
    this.onCountdownElapsed = onCountdownElapsed;
    this.onError = onError;

    interval = new ReadOnlyObjectWrapper<>(Duration.ZERO);
    intervalLogged = new ReadOnlyBooleanWrapper(false);
    countdownActive = new ReadOnlyBooleanWrapper(false);
    countdown = new ReadOnlyObjectWrapper<>(Duration.ZERO);

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
              return DateTimeFormatter.ofPattern("HH:mm:ss").format(time);
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

  BooleanExpression stopMenuItemDisableProperty() {
    return stopMenuItemDisable;
  }

  StringProperty activityTextProperty() {
    return activityText;
  }

  BooleanExpression formDisableProperty() {
    return formDisable;
  }

  BooleanExpression logButtonDisableProperty() {
    return logButtonDisable;
  }

  StringExpression countdownLabelTextProperty() {
    return countdownLabelText;
  }

  DoubleExpression countdownProgressProperty() {
    return countdownProgress;
  }

  ObservableList<ActivityItem> getRecentActivities() {
    return recentActivities;
  }

  ReadOnlyStringProperty hoursTodayLabelTextProperty() {
    return hoursTodayLabelText.getReadOnlyProperty();
  }

  ReadOnlyStringProperty hoursYesterdayLabelTextProperty() {
    return hoursYesterdayLabelText.getReadOnlyProperty();
  }

  ReadOnlyStringProperty hoursThisWeekLabelTextProperty() {
    return hoursThisWeekLabelText.getReadOnlyProperty();
  }

  ReadOnlyStringProperty hoursThisMonthLabelTextProperty() {
    return hoursThisMonthLabelText.getReadOnlyProperty();
  }

  void run() {
    load();
  }

  void load() {
    RecentActivities recentActivities;
    try {
      recentActivities = activitiesService.selectRecentActivities();
    } catch (Exception e) {
      var message = joinExceptionMessages("Failed to load activities.", e);
      onError.accept(message);
      return;
    }

    var items = new ArrayList<ActivityItem>();
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    for (var day : recentActivities.workingDays()) {
      items.add(new ActivityItem(day.date().format(dateFormatter)));
      for (var activity : day.activities()) {
        items.add(
            new ActivityItem(
                activity.time().format(timeFormatter) + " - " + activity.description(), activity));
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

  void logActivity() {
    try {
      activitiesService.logActivity(activityText.get());
      intervalLogged.set(true);
    } catch (Exception e) {
      var message = joinExceptionMessages("Failed to log activity.", e);
      onError.accept(message);
      return;
    }

    load();
  }

  void setActivity(Activity activity) {
    activityText.set(activity.description());
  }

  void startCountdown(Duration interval) {
    this.interval.set(interval);
    countdown.set(interval);
    countdownActive.set(true);
    intervalLogged.set(true);
  }

  void progressCountdown(Duration duration) {
    countdown.set(countdown.get().minus(duration));
    if (countdown.get().isZero()) {
      intervalLogged.set(false);
      onCountdownElapsed.run();
      countdown.set(interval.get());
    }
  }

  void stopCountdown() {
    countdownActive.set(false);
  }

  private static String joinExceptionMessages(String errorMessage, Throwable cause) {
    if (cause == null) {
      return errorMessage;
    }

    return joinExceptionMessages(errorMessage + " " + cause.getMessage(), cause.getCause());
  }
}
