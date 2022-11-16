package de.muspellheim.activitysampling.application;

import javafx.application.*;
import javafx.stage.*;

public class App extends Application {
  @Override
  public void start(Stage primaryStage) {
    var view = ActivitySamplingView.newInstance(primaryStage);
    view.run();
  }
}
