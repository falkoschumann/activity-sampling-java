package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.application.activitysampling.*;
import de.muspellheim.activitysampling.application.timesheet.*;
import de.muspellheim.activitysampling.domain.*;
import de.muspellheim.activitysampling.infrastructure.*;
import java.nio.file.*;
import java.util.function.*;

public class ViewModels {
  private ViewModels() {
    // do not instantiate static class
  }

  public static ActivitySamplingViewModel newActivitySampling(
      Runnable onCountdownElapsed, Consumer<String> onError) {
    var activitiesService = getActivitiesService();
    return new ActivitySamplingViewModel(activitiesService, onCountdownElapsed, onError);
  }

  public static TimesheetViewModel newTimesheet(Consumer<String> onError) {
    var activitiesService = getActivitiesService();
    return new TimesheetViewModel(activitiesService, onError);
  }

  private static ActivitiesServiceImpl getActivitiesService() {
    var file = Paths.get(Configuration.INSTANCE.getLogFile());
    var eventStore = new CsvEventStore(file);
    return new ActivitiesServiceImpl(eventStore);
  }
}
