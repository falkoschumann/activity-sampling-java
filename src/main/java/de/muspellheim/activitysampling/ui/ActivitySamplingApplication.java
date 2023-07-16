/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui;

import de.muspellheim.activitysampling.ui.activitysampling.ActivitySamplingView;
import de.muspellheim.activitysampling.ui.shared.Configuration;
import de.muspellheim.activitysampling.ui.timesheet.TimesheetView;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.stage.Stage;

public class ActivitySamplingApplication extends Application {
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
    var activitySamplingView = ActivitySamplingView.newInstance(primaryStage);

    activitySamplingView.addOpenTimesheetListener(
        e -> openTimesheetView(activitySamplingView.getStage()));

    activitySamplingView.run();
  }

  private static void openTimesheetView(Stage owner) {
    var timesheetView = TimesheetView.newInstance(owner);

    timesheetView.run();
  }
}
