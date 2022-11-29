package de.muspellheim.activitysampling.application.uat;

import de.muspellheim.activitysampling.application.activitysampling.*;
import de.muspellheim.activitysampling.application.shared.*;
import de.muspellheim.activitysampling.domain.*;
import java.io.*;
import java.nio.file.*;

class SystemUnderTest {
  static final SystemUnderTest INSTANCE = new SystemUnderTest();

  private TickingClock clock;
  private ActivitySamplingViewModel activitySamplingViewModel;

  SystemUnderTest() {
    reset();
  }

  void reset() {
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
