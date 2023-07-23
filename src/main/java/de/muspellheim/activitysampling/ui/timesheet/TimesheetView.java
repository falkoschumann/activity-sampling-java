/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.timesheet;

import de.muspellheim.activitysampling.ui.shared.ErrorView;
import de.muspellheim.activitysampling.ui.shared.PeriodView;
import de.muspellheim.activitysampling.ui.shared.Registry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class TimesheetView {

  @FXML private Stage stage;
  @FXML private PeriodView periodViewController;
  @FXML private TableView<TimesheetItem> timesheetTable;
  @FXML private TableColumn<TimesheetItem, String> dateColumn;
  @FXML private TableColumn<TimesheetItem, String> clientColumn;
  @FXML private TableColumn<TimesheetItem, String> projectColumn;
  @FXML private TableColumn<TimesheetItem, String> taskColumn;
  @FXML private TableColumn<TimesheetItem, String> hoursColumn;
  @FXML private Label totalLabel;

  private final TimesheetViewModel viewModel =
      new TimesheetViewModel(Registry.getActivitiesService());

  public static TimesheetView newInstance(Stage owner) {
    String file = "/TimesheetView.fxml";
    try {
      var url = TimesheetView.class.getResource(file);
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
    dateColumn.setCellFactory(TimesheetTableCell.newCellFactory(Pos.BASELINE_CENTER));
    dateColumn.setCellValueFactory(TimesheetTableCell.newCellValueFactory(TimesheetItem::date));
    clientColumn.setCellFactory(TimesheetTableCell.newCellFactory(Pos.BASELINE_LEFT));
    clientColumn.setCellValueFactory(TimesheetTableCell.newCellValueFactory(TimesheetItem::client));
    projectColumn.setCellFactory(TimesheetTableCell.newCellFactory(Pos.BASELINE_LEFT));
    projectColumn.setCellValueFactory(
        TimesheetTableCell.newCellValueFactory(TimesheetItem::project));
    taskColumn.setCellFactory(TimesheetTableCell.newCellFactory(Pos.BASELINE_LEFT));
    taskColumn.setCellValueFactory(TimesheetTableCell.newCellValueFactory(TimesheetItem::task));
    hoursColumn.setCellFactory(TimesheetTableCell.newCellFactory(Pos.BASELINE_CENTER));
    hoursColumn.setCellValueFactory(TimesheetTableCell.newCellValueFactory(TimesheetItem::hours));

    periodViewController.addPeriodChangedListener(
        e -> viewModel.load(periodViewController.getFrom(), periodViewController.getTo()));
    viewModel.addErrorOccurredListener(ErrorView::show);
    timesheetTable.setItems(viewModel.getTimesheetItems());
    totalLabel.textProperty().bind(viewModel.totalLabelTextProperty());
  }

  public void run() {
    stage.show();
    viewModel.load(periodViewController.getFrom(), periodViewController.getTo());
  }
}
