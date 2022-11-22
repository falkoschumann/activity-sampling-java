package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.application.activitysampling.*;
import javafx.application.*;
import javafx.stage.*;

public class App extends Application {
  private static final String ARG_LOG_FILE = "log-file";

  @Override
  public void init() {
    if (getParameters().getNamed().containsKey(ARG_LOG_FILE)) {
      Configuration.INSTANCE.setLogFile(getParameters().getNamed().get(ARG_LOG_FILE));
    }
  }

  @Override
  public void start(Stage primaryStage) {
    var view = ActivitySamplingView.newInstance(primaryStage);
    view.run();
  }
}
