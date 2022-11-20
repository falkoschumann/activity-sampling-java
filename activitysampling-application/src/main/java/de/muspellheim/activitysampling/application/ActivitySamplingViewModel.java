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
  Runnable onCountdownElapsed;
  Consumer<String> onError;

  private final StringProperty activityText = new SimpleStringProperty("");
  private final ReadOnlyObjectWrapper<Duration> interval =
      new ReadOnlyObjectWrapper<>(Duration.ZERO);
  private final ReadOnlyBooleanWrapper intervalLogged = new ReadOnlyBooleanWrapper(false);
  private final ReadOnlyBooleanWrapper countdownActive = new ReadOnlyBooleanWrapper(false);
  private final ReadOnlyObjectWrapper<Duration> countdown =
      new ReadOnlyObjectWrapper<>(Duration.ZERO);
  private final ObservableList<ActivityItem> recentActivities = FXCollections.observableArrayList();
  private final ReadOnlyStringWrapper hoursTodayLabelText = new ReadOnlyStringWrapper("00:00");
  private final ReadOnlyStringWrapper hoursYesterdayLabelText = new ReadOnlyStringWrapper("00:00");
  private final ReadOnlyStringWrapper hoursThisWeekLabelText = new ReadOnlyStringWrapper("00:00");
  private final ReadOnlyStringWrapper hoursThisMonthLabelText = new ReadOnlyStringWrapper("00:00");

  private final BooleanExpression activityDisable = countdownActive.and(intervalLogged);
  private final BooleanExpression logButtonDisable =
      Bindings.createBooleanBinding(() -> activityText.get().isBlank(), activityText)
          .or(countdownActive.and(intervalLogged));
  private final StringExpression countdownLabelText =
      Bindings.createStringBinding(
          () -> {
            var time = LocalTime.ofSecondOfDay(countdown.get().toSeconds());
            return DateTimeFormatter.ofPattern("HH:mm:ss").format(time);
          },
          interval,
          countdown);
  private final DoubleExpression countdownProgress =
      Bindings.createDoubleBinding(
          () -> {
            var elapsedSeconds = (double) interval.get().minus(countdown.get()).getSeconds();
            return elapsedSeconds / interval.get().toSeconds();
          },
          interval,
          countdown);

  private final ActivitiesService activitiesService;

  ActivitySamplingViewModel(ActivitiesService activitiesService) {
    this.activitiesService = activitiesService;
  }

  StringProperty activityTextProperty() {
    return activityText;
  }

  BooleanExpression activityDisableProperty() {
    return activityDisable;
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
                activity.timestamp().format(timeFormatter) + " - " + activity.description(),
                activity));
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

  void progressCountdown() {
    countdown.set(countdown.get().minusSeconds(1));
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
