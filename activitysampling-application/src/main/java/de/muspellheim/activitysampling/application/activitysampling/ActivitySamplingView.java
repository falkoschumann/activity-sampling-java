/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import de.muspellheim.activitysampling.application.shared.*;
import de.muspellheim.activitysampling.application.timesheet.*;
import java.time.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class ActivitySamplingView {
  @FXML private Stage stage;
  @FXML private MenuBar menuBar;
  @FXML private MenuItem stopMenuItem;
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

  private final ActivitySamplingViewModel viewModel =
      new ActivitySamplingViewModel(Registry.getActivitiesService());

  private final SystemClock systemClock = new SystemClock();
  private final Notifier notifier = new Notifier(viewModel);

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
    systemClock.addOnTickListener(viewModel::progressCountdown);
    viewModel.addOnErrorListener(ErrorView::handleError);
    stage.setOnCloseRequest(e -> notifier.dispose());
    menuBar.setUseSystemMenuBar(true);
    stopMenuItem.disableProperty().bind(viewModel.stopMenuItemDisableProperty());
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
    viewModel.startCountdown(interval);
  }

  @FXML
  private void handleStop() {
    viewModel.stopCountdown();
  }

  @FXML
  private void handleTimesheet() {
    var timesheetView = TimesheetView.newInstance(stage);
    timesheetView.run();
  }

  @FXML
  private void handleRefresh() {
    viewModel.load();
  }

  @FXML
  private void handleLog() {
    viewModel.logActivity();
  }
}
