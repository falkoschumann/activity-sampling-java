package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import de.muspellheim.activitysampling.infrastructure.*;
import java.nio.file.*;
import java.util.function.*;

class ViewModels {
  private ViewModels() {
    // do not instantiate static class
  }

  static ActivitySamplingViewModel newActivitySampling(
      Runnable onCountdownElapsed, Consumer<String> onError) {
    var activitiesService = getActivitiesService();
    return new ActivitySamplingViewModel(activitiesService, onCountdownElapsed, onError);
  }

  static TimesheetViewModel newTimesheet() {
    var activitiesService = getActivitiesService();
    return new TimesheetViewModel(activitiesService);
  }

  private static ActivitiesServiceImpl getActivitiesService() {
    var file = Paths.get(Configuration.INSTANCE.getLogFile());
    var eventStore = new CsvEventStore(file);
    return new ActivitiesServiceImpl(eventStore);
  }
}
