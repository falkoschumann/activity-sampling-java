/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.timesheet;

import de.muspellheim.activitysampling.application.shared.ErrorView;
import de.muspellheim.activitysampling.application.shared.Registry;
import java.time.temporal.ChronoUnit;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class TimesheetView {
  @FXML private Stage stage;
  @FXML private Label title1;
  @FXML private Label title2;
  @FXML private ChoiceBox<ChronoUnit> period;
  @FXML private TableView<TimesheetItem> timesheetTable;
  @FXML private TableColumn<TimesheetItem, String> dateColumn;
  @FXML private TableColumn<TimesheetItem, String> notesColumn;
  @FXML private TableColumn<TimesheetItem, String> hoursColumn;
  @FXML private Label total;

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
  void initialize() {
    viewModel.setOnError(ErrorView::handleError);
    period.setConverter(new ChronoUnitStringConverter());
    period.getItems().addAll(ChronoUnit.DAYS, ChronoUnit.WEEKS, ChronoUnit.MONTHS);
    period.valueProperty().bindBidirectional(viewModel.periodProperty());
    title1.textProperty().bind(viewModel.title1Property());
    title2.textProperty().bind(viewModel.title2Property());
    timesheetTable.setItems(viewModel.getTimesheetItems());
    dateColumn.setCellFactory(column -> new TimesheetTableCell<>(Pos.BASELINE_CENTER));
    dateColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().date()));
    notesColumn.setCellFactory(column -> new TimesheetTableCell<>());
    notesColumn.setCellValueFactory(
        param -> new SimpleObjectProperty<>(param.getValue().activity()));
    hoursColumn.setCellFactory(column -> new TimesheetTableCell<>(Pos.BASELINE_CENTER));
    hoursColumn.setCellValueFactory(
        param -> new SimpleObjectProperty<>(param.getValue().duration()));
    total.textProperty().bind(viewModel.totalProperty());
  }

  public void run() {
    stage.show();
    viewModel.run();
  }

  @FXML
  void handleBack() {
    viewModel.back();
  }

  @FXML
  void handleForward() {
    viewModel.forward();
  }
}
