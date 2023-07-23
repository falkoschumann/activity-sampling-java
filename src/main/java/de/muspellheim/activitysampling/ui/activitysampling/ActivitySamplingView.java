/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.activitysampling;

import de.muspellheim.activitysampling.ui.shared.ErrorView;
import de.muspellheim.activitysampling.ui.shared.Registry;
import de.muspellheim.activitysampling.util.EventEmitter;
import java.time.Duration;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ActivitySamplingView {

  private final EventEmitter<Void> openTime = new EventEmitter<>();
  private final EventEmitter<Void> openTimesheet = new EventEmitter<>();

  @FXML private Stage stage;
  @FXML private MenuBar menuBar;
  @FXML private MenuItem stopMenuItem;
  @FXML private Pane form;
  @FXML private TextField client;
  @FXML private TextField project;
  @FXML private TextField task;
  @FXML private TextField notes;
  @FXML private Button logButton;
  @FXML private Label countdownLabel;
  @FXML private ProgressBar countdown;
  @FXML private ListView<ActivityItem> recentActivities;
  @FXML private Label hoursToday;
  @FXML private Label hoursYesterday;
  @FXML private Label hoursThisWeek;
  @FXML private Label hoursThisMonth;

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
  private void initialize() {
    menuBar.setUseSystemMenuBar(true);
    recentActivities.setCellFactory(ActivityListCell.newCellFactory(viewModel::setActivity));

    systemClock.addOnTickListener(viewModel::progressCountdown);
    viewModel.addErrorOccurredListener(ErrorView::show);
    stage.setOnCloseRequest(e -> notifier.dispose());
    stopMenuItem.disableProperty().bind(viewModel.stopMenuItemDisableProperty());
    form.disableProperty().bind(viewModel.formDisableProperty());
    client.textProperty().bindBidirectional(viewModel.clientTextProperty());
    project.textProperty().bindBidirectional(viewModel.projectTextProperty());
    task.textProperty().bindBidirectional(viewModel.taskTextProperty());
    notes.textProperty().bindBidirectional(viewModel.notesTextProperty());
    logButton.disableProperty().bind(viewModel.logButtonDisableProperty());
    countdownLabel.textProperty().bind(viewModel.countdownLabelTextProperty());
    countdown.progressProperty().bind(viewModel.countdownProgressProperty());
    recentActivities.setItems(viewModel.getRecentActivities());
    hoursToday.textProperty().bind(viewModel.hoursTodayTextProperty());
    hoursYesterday.textProperty().bind(viewModel.hoursYesterdayTextProperty());
    hoursThisWeek.textProperty().bind(viewModel.hoursThisWeekTextProperty());
    hoursThisMonth.textProperty().bind(viewModel.hoursThisMonthTextProperty());
  }

  public Stage getStage() {
    return stage;
  }

  public final void addOpenTimeListener(Consumer<Void> listener) {
    openTime.addListener(listener);
  }

  public final void removeOpenTimeListener(Consumer<Void> listener) {
    openTime.removeListener(listener);
  }

  public final void addOpenTimesheetListener(Consumer<Void> listener) {
    openTimesheet.addListener(listener);
  }

  public final void removeOpenTimesheetListener(Consumer<Void> listener) {
    openTimesheet.removeListener(listener);
  }

  public void run() {
    stage.show();
    viewModel.load();
    client.requestFocus();
  }

  @FXML
  private void quit() {
    stage.close();
  }

  @FXML
  private void startCountdown5min() {
    startCountdown(Duration.ofMinutes(5));
  }

  @FXML
  private void startCountdown10min() {
    startCountdown(Duration.ofMinutes(10));
  }

  @FXML
  private void startCountdown15min() {
    startCountdown(Duration.ofMinutes(15));
  }

  @FXML
  private void startCountdown20min() {
    startCountdown(Duration.ofMinutes(20));
  }

  @FXML
  private void startCountdown30min() {
    startCountdown(Duration.ofMinutes(30));
  }

  @FXML
  private void startCountdown60min() {
    startCountdown(Duration.ofMinutes(60));
  }

  @FXML
  private void startCountdown1min() {
    startCountdown(Duration.ofMinutes(1));
  }

  private void startCountdown(Duration interval) {
    viewModel.startCountdown(interval);
  }

  @FXML
  private void stopCountdown() {
    viewModel.stopCountdown();
  }

  @FXML
  private void refresh() {
    viewModel.load();
  }

  @FXML
  private void openTimesheet() {
    openTimesheet.emit(null);
  }

  @FXML
  private void openTime() {
    openTime.emit(null);
  }

  @FXML
  private void logActivity() {
    viewModel.logActivity();
  }
}
