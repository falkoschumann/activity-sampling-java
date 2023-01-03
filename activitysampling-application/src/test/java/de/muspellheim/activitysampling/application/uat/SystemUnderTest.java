/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.uat;

import de.muspellheim.activitysampling.application.activitysampling.ActivitySamplingViewModel;
import de.muspellheim.activitysampling.application.shared.Configuration;
import de.muspellheim.activitysampling.application.shared.Registry;
import de.muspellheim.activitysampling.domain.ActivitiesService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class SystemUnderTest {
  static final SystemUnderTest INSTANCE = new SystemUnderTest();

  private TickingClock clock;
  private ActivitySamplingViewModel activitySamplingViewModel;

  SystemUnderTest() {
    reset();
  }

  final void reset() {
    var logFile = Paths.get("build/activity-log.csv");
    try {
      Files.deleteIfExists(logFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    clock = new TickingClock();

    Configuration configuration = Configuration.INSTANCE;
    configuration.setClock(clock);
    configuration.setLogFile(logFile);

    ActivitiesService activitiesService = Registry.getActivitiesService();
    activitySamplingViewModel = new ActivitySamplingViewModel(activitiesService);
    activitySamplingViewModel.run();
  }

  TickingClock getClock() {
    return clock;
  }

  ActivitySamplingViewModel getActivitySamplingViewModel() {
    return activitySamplingViewModel;
  }
}
