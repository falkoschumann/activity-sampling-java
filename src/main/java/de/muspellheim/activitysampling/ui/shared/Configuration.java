/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.shared;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration {
  private static final String LOG_FILE_PROPERTY = "activitySampling.logFile";
  private static final Path DEFAULT_LOG_FILE =
      Paths.get(System.getProperty("user.home"), "activity-log.csv");

  private static final Configuration INSTANCE = new Configuration();

  private Path logFile;

  private Configuration() {
    logFile = Paths.get(System.getProperty(LOG_FILE_PROPERTY, DEFAULT_LOG_FILE.toString()));
  }

  public static Configuration getInstance() {
    return INSTANCE;
  }

  public Path getLogFile() {
    return logFile;
  }

  public void setLogFile(Path logFile) {
    this.logFile = logFile;
  }
}
