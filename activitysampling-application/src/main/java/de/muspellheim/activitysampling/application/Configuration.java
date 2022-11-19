package de.muspellheim.activitysampling.application;

class Configuration {
  static final Configuration INSTANCE = new Configuration();

  private static final String PROP_LOG_FILE = "activitySampling.logFile";
  private static final String DEFAULT_LOG_FILE =
      System.getProperty("user.home") + System.lineSeparator() + "activity-log.csv";

  private String logFile;

  private Configuration() {
    logFile = System.getProperty(PROP_LOG_FILE, DEFAULT_LOG_FILE);
  }

  String getLogFile() {
    return logFile;
  }

  public void setLogFile(String logFile) {
    this.logFile = logFile;
  }
}
