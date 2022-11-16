package de.muspellheim.activitysampling.application;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class ActivitySamplingView {
  @FXML private Stage stage;
  @FXML private MenuBar menuBar;
  @FXML private TextField activity;
  @FXML private Button logButton;
  @FXML private ListView<ActivityItem> recentActivities;

  private final ActivitySamplingViewModel viewModel = new ActivitySamplingViewModel();

  public static ActivitySamplingView newInstance(Stage stage) {
    String file = "/ActivitySamplingView.fxml";
    try {
      var url = ActivitySamplingView.class.getResource(file);
      var loader = new FXMLLoader(url);
      loader.setRoot(stage);
      loader.setControllerFactory(type -> new ActivitySamplingView());
      loader.load();
      return loader.getController();
    } catch (Exception e) {
      throw new RuntimeException("Could not load view: " + file, e);
    }
  }

  @FXML
  private void initialize() {
    menuBar.setUseSystemMenuBar(true);
    activity.textProperty().bindBidirectional(viewModel.activityTextProperty());
    logButton.disableProperty().bind(viewModel.logButtonDisableProperty());
    recentActivities.setItems(viewModel.getRecentActivities());
    recentActivities.setCellFactory(view -> new ActivityListCell(viewModel::setActivity));
  }

  public void run() {
    stage.show();
    viewModel.run();
    activity.requestFocus();
  }

  @FXML
  private void handleQuit() {
    stage.close();
  }

  @FXML
  private void handleLog() {
    viewModel.logActivity();
  }
}
