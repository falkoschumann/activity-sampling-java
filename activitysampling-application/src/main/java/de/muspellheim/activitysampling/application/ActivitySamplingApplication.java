/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.application.activitysampling.ActivitySamplingView;
import de.muspellheim.activitysampling.application.shared.Configuration;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.stage.Stage;

public class ActivitySamplingApplication extends Application {
  // TODO configure default duration, if log without timer
  // TODO copy text from recent activities and timesheet
  private static final String ARG_LOG_FILE = "log-file";

  @Override
  public void init() {
    if (getParameters().getNamed().containsKey(ARG_LOG_FILE)) {
      var logFile = Paths.get(getParameters().getNamed().get(ARG_LOG_FILE));
      Configuration.getInstance().setLogFile(logFile);
    }
  }

  @Override
  public void start(Stage primaryStage) {
    var view = ActivitySamplingView.newInstance(primaryStage);
    view.run();
  }
}
