package de.muspellheim.activitysampling.application;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class ActivitySamplingView {
  @FXML private Stage stage;
  @FXML private MenuBar menuBar;
  @FXML private Label activityLabel;
  @FXML private TextField activity;
  @FXML private Button logButton;
  @FXML private Label countdownLabel;
  @FXML private ProgressBar countdown;
  @FXML private ListView<ActivityItem> recentActivities;
  @FXML private Label hoursTodayLabel;
  @FXML private Label hoursYesterdayLabel;
  @FXML private Label hoursThisWeekLabel;
  @FXML private Label hoursThisMonthLabel;

  private final Notifier notifier = new Notifier();

  private final ActivitySamplingViewModel viewModel =
      ViewModels.newActivitySampling(notifier::showNotification, this::handleError);

  private final Timer timer = new Timer("System clock", true);
  private CountdownTask countdownTask;

  public static ActivitySamplingView newInstance(Stage stage) {
    String file = "/ActivitySamplingView.fxml";
    try {
      var url = ActivitySamplingView.class.getResource(file);
      var loader = new FXMLLoader(url);
      loader.setRoot(stage);
      loader.load();
      return loader.getController();
    } catch (Exception e) {
      throw new IllegalStateException("Could not load view: " + file, e);
    }
  }

  @FXML
  private void initialize() {
    stage.setOnCloseRequest(e -> notifier.dispose());
    menuBar.setUseSystemMenuBar(true);
    activityLabel.disableProperty().bind(viewModel.formDisableProperty());
    activity.textProperty().bindBidirectional(viewModel.activityTextProperty());
    activity.disableProperty().bind(viewModel.formDisableProperty());
    logButton.disableProperty().bind(viewModel.logButtonDisableProperty());
    countdownLabel.textProperty().bind(viewModel.countdownLabelTextProperty());
    countdown.progressProperty().bind(viewModel.countdownProgressProperty());
    recentActivities.setCellFactory(view -> new ActivityListCell(viewModel::setActivity));
    recentActivities.setItems(viewModel.getRecentActivities());
    hoursTodayLabel.textProperty().bind(viewModel.hoursTodayLabelTextProperty());
    hoursYesterdayLabel.textProperty().bind(viewModel.hoursYesterdayLabelTextProperty());
    hoursThisWeekLabel.textProperty().bind(viewModel.hoursThisWeekLabelTextProperty());
    hoursThisMonthLabel.textProperty().bind(viewModel.hoursThisMonthLabelTextProperty());
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
    timer.scheduleAtFixedRate(countdownTask, 0, TimeUnit.SECONDS.toMillis(1));
  }

  @FXML
  private void handleStop() {
    stopCountdown();
    viewModel.stopCountdown();
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

  private void handleError(String message) {
    var alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("An unexpected error occurred.");
    alert.setContentText(message);
    alert.show();
  }

  private class CountdownTask extends TimerTask {
    @Override
    public void run() {
      Platform.runLater(() -> viewModel.progressCountdown(Duration.ofSeconds(1)));
    }
  }
}
