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
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

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
      throw new UncheckedIOException(e);
    }

    Configuration configuration = Configuration.INSTANCE;
    configuration.setLogFile(logFile);

    ActivitiesService activitiesService = Registry.getActivitiesService();
    clock = new TickingClock(Instant.now(), ZoneId.of("Europe/Berlin"));
    activitySamplingViewModel =
        new ActivitySamplingViewModel(activitiesService, Locale.GERMANY, clock);
    activitySamplingViewModel.run();
  }

  TickingClock getClock() {
    return clock;
  }

  ActivitySamplingViewModel getActivitySamplingViewModel() {
    return activitySamplingViewModel;
  }
}
