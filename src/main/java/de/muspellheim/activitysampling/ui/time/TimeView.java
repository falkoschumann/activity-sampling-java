/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.time;

import de.muspellheim.activitysampling.ui.shared.ErrorView;
import de.muspellheim.activitysampling.ui.shared.PeriodView;
import de.muspellheim.activitysampling.ui.shared.Registry;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class TimeView {

  @FXML private Stage stage;
  @FXML private PeriodView periodViewController;
  @FXML private ToggleButton clientsToggle;
  @FXML private ToggleButton projectsToggle;
  @FXML private ToggleButton tasksToggle;
  @FXML private TableView<TimeItem> timeTable;
  @FXML private TableColumn<TimeItem, String> nameColumn;
  @FXML private TableColumn<TimeItem, String> clientColumn;
  @FXML private TableColumn<TimeItem, String> hoursColumn;
  @FXML private Label totalLabel;

  private final TimeViewModel viewModel = new TimeViewModel(Registry.getActivitiesService());

  public static TimeView newInstance(Stage owner) {
    String file = "/TimeView.fxml";
    try {
      var url = TimeView.class.getResource(file);
      var loader = new FXMLLoader(url);
      var stage = new Stage();
      stage.initOwner(owner);
      loader.setRoot(stage);
      loader.load();
      return loader.getController();
    } catch (Exception e) {
      throw new IllegalStateException("Could not load view: " + file, e);
    }
  }

  @FXML
  private void initialize() {
    periodViewController.setPeriods(
        List.of(ChronoUnit.DAYS, ChronoUnit.WEEKS, ChronoUnit.MONTHS, ChronoUnit.YEARS));
    periodViewController.setPeriod(ChronoUnit.MONTHS);
    nameColumn.setCellFactory(TimeTableCell.newCellFactory(Pos.BASELINE_LEFT));
    nameColumn.setCellValueFactory(TimeTableCell.newCellValueFactory(TimeItem::name));
    clientColumn.setCellFactory(TimeTableCell.newCellFactory(Pos.BASELINE_LEFT));
    clientColumn.setCellValueFactory(TimeTableCell.newCellValueFactory(TimeItem::client));
    hoursColumn.setCellFactory(TimeTableCell.newCellFactory(Pos.BASELINE_CENTER));
    hoursColumn.setCellValueFactory(TimeTableCell.newCellValueFactory(TimeItem::hours));

    periodViewController.addPeriodChangedListener(e -> update());
    viewModel.addErrorOccurredListener(ErrorView::show);
    timeTable.setItems(viewModel.getTimeItems());
    totalLabel.textProperty().bind(viewModel.totalLabelTextProperty());
  }

  public void run() {
    stage.show();
    update();
  }

  @FXML
  private void changeScope() {
    update();
  }

  private void update() {
    if (clientsToggle.isSelected()) {
      nameColumn.setPrefWidth(300);
      clientColumn.setVisible(false);
      viewModel.load(
          periodViewController.getFrom(),
          periodViewController.getTo(),
          TimeViewModel.Scope.CLIENTS);
    } else if (projectsToggle.isSelected()) {
      nameColumn.setPrefWidth(300);
      clientColumn.setVisible(true);
      viewModel.load(
          periodViewController.getFrom(),
          periodViewController.getTo(),
          TimeViewModel.Scope.PROJECTS);
    } else if (tasksToggle.isSelected()) {
      nameColumn.setPrefWidth(600);
      clientColumn.setVisible(false);
      viewModel.load(
          periodViewController.getFrom(), periodViewController.getTo(), TimeViewModel.Scope.TASKS);
    }
  }
}
