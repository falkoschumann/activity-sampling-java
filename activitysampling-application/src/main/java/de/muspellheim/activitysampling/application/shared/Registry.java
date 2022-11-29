package de.muspellheim.activitysampling.application.shared;

import de.muspellheim.activitysampling.domain.*;
import de.muspellheim.activitysampling.infrastructure.*;

public class Registry {

  private Registry() {
    // do not instantiate static class
  }

  public static ActivitiesService getActivitiesService() {
    Configuration configuration = Configuration.INSTANCE;
    var file = configuration.getLogFile();
    var clock = configuration.getClock();
    var activities = new CsvActivities(file);
    return new ActivitiesServiceImpl(activities, clock);
  }
}
