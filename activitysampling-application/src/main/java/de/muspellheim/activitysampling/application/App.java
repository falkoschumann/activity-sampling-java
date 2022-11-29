package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.application.activitysampling.*;
import de.muspellheim.activitysampling.application.shared.*;
import java.nio.file.*;
import javafx.application.*;
import javafx.stage.*;

public class App extends Application {
  private static final String ARG_LOG_FILE = "log-file";

  @Override
  public void init() {
    if (getParameters().getNamed().containsKey(ARG_LOG_FILE)) {
      var logFile = Paths.get(getParameters().getNamed().get(ARG_LOG_FILE));
      Configuration.INSTANCE.setLogFile(logFile);
    }
  }

  @Override
  public void start(Stage primaryStage) {
    var view = ActivitySamplingView.newInstance(primaryStage);
    view.run();
  }
}
