package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import de.muspellheim.activitysampling.infrastructure.*;
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

  private final ActivitiesService activitiesService;

  ActivitySamplingViewModel() {
    this(new ActivitiesServiceImpl(new CsvEventStore()));
  }

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

  void run() {
    load();
  }

  void load() {
    var items = new ArrayList<ActivityItem>();
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    for (var day : activitiesService.selectRecentActivities().workingDays()) {
      items.add(new ActivityItem(day.date().format(dateFormatter)));
      for (var activity : day.activities()) {
        items.add(
            new ActivityItem(
                activity.timestamp().format(timeFormatter) + " - " + activity.description(),
                activity));
      }
    }
    recentActivities.setAll(items);
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
