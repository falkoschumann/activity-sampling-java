package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;

class ActivitySamplingViewModel {
  Runnable onCountdownElapsed;

  private final StringProperty activityText = new SimpleStringProperty("");
  private final ReadOnlyBooleanWrapper logButtonDisable = new ReadOnlyBooleanWrapper(true);
  private final ReadOnlyObjectWrapper<Duration> interval =
      new ReadOnlyObjectWrapper<>(Duration.ZERO);
  private final ReadOnlyObjectWrapper<Duration> countdown =
      new ReadOnlyObjectWrapper<>(Duration.ZERO);
  private final ReadOnlyStringWrapper countdownLabelText = new ReadOnlyStringWrapper("00:00:00");
  private final ReadOnlyDoubleWrapper countdownProgress = new ReadOnlyDoubleWrapper(0.0);
  private final ObservableList<ActivityItem> recentActivities = FXCollections.observableArrayList();
  private final ReadOnlyStringWrapper hoursTodayLabelText = new ReadOnlyStringWrapper("00:00");
  private final ReadOnlyStringWrapper hoursYesterdayLabelText = new ReadOnlyStringWrapper("00:00");
  private final ReadOnlyStringWrapper hoursThisWeekLabelText = new ReadOnlyStringWrapper("00:00");
  private final ReadOnlyStringWrapper hoursThisMonthLabelText = new ReadOnlyStringWrapper("00:00");

  private final ActivitiesService activitiesService;

  ActivitySamplingViewModel(ActivitiesService activitiesService) {
    this.activitiesService = activitiesService;

    logButtonDisable.bind(
        Bindings.createBooleanBinding(() -> activityText.get().isBlank(), activityText));
    countdownLabelText.bind(
        Bindings.createStringBinding(
            () -> {
              var time = LocalTime.ofSecondOfDay(countdown.get().toSeconds());
              return DateTimeFormatter.ofPattern("HH:mm:ss").format(time);
            },
            interval,
            countdown));
    countdownProgress.bind(
        Bindings.createDoubleBinding(
            () -> {
              var elapsedSeconds = (double) interval.get().minus(countdown.get()).getSeconds();
              return elapsedSeconds / interval.get().toSeconds();
            },
            interval,
            countdown));
  }

  StringProperty activityTextProperty() {
    return activityText;
  }

  ReadOnlyBooleanProperty logButtonDisableProperty() {
    return logButtonDisable.getReadOnlyProperty();
  }

  ReadOnlyStringProperty countdownLabelTextProperty() {
    return countdownLabelText.getReadOnlyProperty();
  }

  ReadOnlyDoubleProperty countdownProgressProperty() {
    return countdownProgress.getReadOnlyProperty();
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
    var recentActivities = activitiesService.selectRecentActivities();

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
    activitiesService.logActivity(activityText.get());
    load();
  }

  void setActivity(Activity activity) {
    activityText.set(activity.description());
  }

  void startCountdown(Duration interval) {
    this.interval.set(interval);
    countdown.set(interval);
  }

  void progressCountdown() {
    countdown.set(countdown.get().minusSeconds(1));
    if (countdown.get().isZero()) {
      Optional.ofNullable(onCountdownElapsed).ifPresent(Runnable::run);
      countdown.set(interval.get());
    }
  }
}
