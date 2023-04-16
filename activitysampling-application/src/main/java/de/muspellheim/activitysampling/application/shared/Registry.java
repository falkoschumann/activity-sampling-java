/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.shared;

import de.muspellheim.activitysampling.domain.ActivitiesService;
import de.muspellheim.activitysampling.domain.ActivitiesServiceImpl;
import de.muspellheim.activitysampling.infrastructure.CsvActivitiesRepository;

public class Registry {

  private Registry() {
    // do not instantiate static class
  }

  public static ActivitiesService getActivitiesService() {
    Configuration configuration = Configuration.INSTANCE;
    var file = configuration.getLogFile();
    var activities = new CsvActivitiesRepository(file);
    return new ActivitiesServiceImpl(activities);
  }
}