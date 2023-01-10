/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.shared;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;

public class Configuration {
  private static final String PROP_LOG_FILE = "activitySampling.logFile";
  private static final String DEFAULT_LOG_FILE =
      System.getProperty("user.home") + System.getProperty("file.separator") + "activity-log.csv";

  public static final Configuration INSTANCE = new Configuration();

  private Clock clock = Clock.systemDefaultZone();
  private Path logFile = Paths.get(System.getProperty(PROP_LOG_FILE, DEFAULT_LOG_FILE));

  private Configuration() {}

  public Clock getClock() {
    return clock;
  }

  public void setClock(Clock clock) {
    this.clock = clock;
  }

  public Path getLogFile() {
    return logFile;
  }

  public void setLogFile(Path logFile) {
    this.logFile = logFile;
  }
}
