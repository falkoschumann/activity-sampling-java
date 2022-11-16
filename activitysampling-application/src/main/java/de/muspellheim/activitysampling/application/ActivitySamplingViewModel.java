package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import de.muspellheim.activitysampling.infrastructure.*;
import java.time.format.*;
import java.util.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;

public class ActivitySamplingViewModel {
  private final StringProperty activityText = new SimpleStringProperty("");
  private final ReadOnlyBooleanWrapper logButtonDisable = new ReadOnlyBooleanWrapper(true);
  private final ObservableList<ActivityItem> recentActivities = FXCollections.observableArrayList();

  private final ActivitiesService activitiesService;

  public ActivitySamplingViewModel() {
    this(new ActivitiesServiceImpl(new CsvEventStore()));
  }

  public ActivitySamplingViewModel(ActivitiesService activitiesService) {
    this.activitiesService = activitiesService;

    logButtonDisable.bind(
        Bindings.createBooleanBinding(() -> activityText.get().isBlank(), activityText));
  }

  public StringProperty activityTextProperty() {
    return activityText;
  }

  public ReadOnlyBooleanProperty logButtonDisableProperty() {
    return logButtonDisable.getReadOnlyProperty();
  }

  public ObservableList<ActivityItem> getRecentActivities() {
    return recentActivities;
  }

  public void run() {
    load();
  }

  public void logActivity() {
    activitiesService.logActivity(activityText.get());
    load();
  }

  private void load() {
    var items = new ArrayList<ActivityItem>();
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    for (var day : activitiesService.selectRecentActivities().workingDays()) {
      items.add(new ActivityItem(day.date().format(dateFormatter), true));
      for (var activity : day.activities()) {
        items.add(
            new ActivityItem(
                activity.timestamp().format(timeFormatter) + " - " + activity.description(),
                false));
      }
    }
    recentActivities.setAll(items);
  }
}
