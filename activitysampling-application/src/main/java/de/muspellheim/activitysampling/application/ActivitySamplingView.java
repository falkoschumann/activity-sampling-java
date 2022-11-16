package de.muspellheim.activitysampling.application;

import java.time.*;
import java.util.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class ActivitySamplingView {
  @FXML private Stage stage;
  @FXML private MenuBar menuBar;
  @FXML private TextField activity;
  @FXML private Button logButton;
  @FXML private Label countdownLabel;
  @FXML private ProgressBar countdown;
  @FXML private ListView<ActivityItem> recentActivities;

  private final ActivitySamplingViewModel viewModel = new ActivitySamplingViewModel();

  private final Notifier notifier = new Notifier();
  private final Timer timer = new Timer("System clock", true);
  private CountdownTask countdownTask;

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
    viewModel.onCountdownElapsed = notifier::showNotification;
    stage.setOnCloseRequest(e -> notifier.dispose());
    menuBar.setUseSystemMenuBar(true);
    activity.textProperty().bindBidirectional(viewModel.activityTextProperty());
    logButton.disableProperty().bind(viewModel.logButtonDisableProperty());
    countdownLabel.textProperty().bind(viewModel.countdownLabelTextProperty());
    countdown.progressProperty().bind(viewModel.countdownProgressProperty());
    recentActivities.setCellFactory(view -> new ActivityListCell(viewModel::setActivity));
    recentActivities.setItems(viewModel.getRecentActivities());
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
  private void handleStart5min() {
    startCountdown(Duration.ofMinutes(5));
  }

  @FXML
  private void handleStart10min() {
    startCountdown(Duration.ofMinutes(10));
  }

  @FXML
  private void handleStart15min() {
    startCountdown(Duration.ofMinutes(15));
  }

  @FXML
  private void handleStart20min() {
    startCountdown(Duration.ofMinutes(20));
  }

  @FXML
  private void handleStart30min() {
    startCountdown(Duration.ofMinutes(30));
  }

  @FXML
  private void handleStart60min() {
    startCountdown(Duration.ofMinutes(60));
  }

  @FXML
  private void handleStart1min() {
    startCountdown(Duration.ofMinutes(1));
  }

  private void startCountdown(Duration interval) {
    stopCountdown();
    viewModel.startCountdown(interval);
    countdownTask = new CountdownTask();
    timer.scheduleAtFixedRate(countdownTask, 0, 1000);
  }

  @FXML
  private void handleStop() {
    stopCountdown();
  }

  private void stopCountdown() {
    Optional.ofNullable(countdownTask).ifPresent(TimerTask::cancel);
  }

  @FXML
  private void handleRefresh() {
    viewModel.load();
  }

  @FXML
  private void handleLog() {
    viewModel.logActivity();
  }

  private class CountdownTask extends TimerTask {
    @Override
    public void run() {
      Platform.runLater(viewModel::progressCountdown);
    }
  }
}
