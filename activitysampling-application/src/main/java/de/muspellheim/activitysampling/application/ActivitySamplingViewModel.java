package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import de.muspellheim.activitysampling.infrastructure.*;
import java.time.format.*;
import java.util.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;

class ActivitySamplingViewModel {
  private final StringProperty activityText = new SimpleStringProperty("");
  private final ReadOnlyBooleanWrapper logButtonDisable = new ReadOnlyBooleanWrapper(true);
  private final ObservableList<ActivityItem> recentActivities = FXCollections.observableArrayList();

  private final ActivitiesService activitiesService;

  ActivitySamplingViewModel() {
    this(new ActivitiesServiceImpl(new CsvEventStore()));
  }

  ActivitySamplingViewModel(ActivitiesService activitiesService) {
    this.activitiesService = activitiesService;

    logButtonDisable.bind(
        Bindings.createBooleanBinding(() -> activityText.get().isBlank(), activityText));
  }

  StringProperty activityTextProperty() {
    return activityText;
  }

  ReadOnlyBooleanProperty logButtonDisableProperty() {
    return logButtonDisable.getReadOnlyProperty();
  }

  ObservableList<ActivityItem> getRecentActivities() {
    return recentActivities;
  }

  void run() {
    load();
  }

  void logActivity() {
    activitiesService.logActivity(activityText.get());
    load();
  }

  void setActivity(Activity activity) {
    activityText.set(activity.description());
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
}
