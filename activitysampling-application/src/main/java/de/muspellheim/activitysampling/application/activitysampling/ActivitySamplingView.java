/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import de.muspellheim.activitysampling.application.shared.ErrorView;
import de.muspellheim.activitysampling.application.shared.Registry;
import de.muspellheim.activitysampling.application.timesheet.TimesheetView;
import java.time.Duration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    var file = "/ActivitySamplingView.fxml";
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
  void initialize() {
    menuBar.setUseSystemMenuBar(true);
    recentActivities.setCellFactory(ActivityListCell.newCellFactory(viewModel::setActivityText));

    systemClock.addOnTickListener(viewModel::progressCountdown);
    viewModel.addOnErrorListener(ErrorView::show);
    stage.setOnCloseRequest(e -> notifier.dispose());
    stopMenuItem.disableProperty().bind(viewModel.stopMenuItemDisableProperty());
    activityLabel.disableProperty().bind(viewModel.formDisableProperty());
    activity.textProperty().bindBidirectional(viewModel.activityTextProperty());
    activity.disableProperty().bind(viewModel.formDisableProperty());
    logButton.disableProperty().bind(viewModel.logButtonDisableProperty());
    countdownLabel.textProperty().bind(viewModel.countdownLabelTextProperty());
    countdown.progressProperty().bind(viewModel.countdownProgressProperty());
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
  void handleQuit() {
    stage.close();
  }

  @FXML
  void handleStart5min() {
    startCountdown(Duration.ofMinutes(5));
  }

  @FXML
  void handleStart10min() {
    startCountdown(Duration.ofMinutes(10));
  }

  @FXML
  void handleStart15min() {
    startCountdown(Duration.ofMinutes(15));
  }

  @FXML
  void handleStart20min() {
    startCountdown(Duration.ofMinutes(20));
  }

  @FXML
  void handleStart30min() {
    startCountdown(Duration.ofMinutes(30));
  }

  @FXML
  void handleStart60min() {
    startCountdown(Duration.ofMinutes(60));
  }

  @FXML
  void handleStart1min() {
    startCountdown(Duration.ofMinutes(1));
  }

  private void startCountdown(Duration interval) {
    viewModel.startCountdown(interval);
  }

  @FXML
  void handleStop() {
    viewModel.stopCountdown();
  }

  @FXML
  void handleTimesheet() {
    var timesheetView = TimesheetView.newInstance(stage);
    timesheetView.run();
  }

  @FXML
  void handleRefresh() {
    viewModel.load();
  }

  @FXML
  void handleLog() {
    viewModel.logActivity();
  }
}
