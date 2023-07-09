/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.shared;

import de.muspellheim.activitysampling.application.ActivitiesService;
import de.muspellheim.activitysampling.application.ActivitiesServiceImpl;
import de.muspellheim.activitysampling.infrastructure.CsvActivities;

public class Registry {

  private Registry() {
    // do not instantiate static class
  }

  public static ActivitiesService getActivitiesService() {
    Configuration configuration = Configuration.getInstance();
    var file = configuration.getLogFile();
    var activities = new CsvActivities(file);
    return new ActivitiesServiceImpl(activities);
  }
}
