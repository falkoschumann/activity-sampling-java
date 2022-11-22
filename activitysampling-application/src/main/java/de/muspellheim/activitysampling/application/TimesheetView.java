package de.muspellheim.activitysampling.application;

import java.time.*;
import javafx.beans.property.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class TimesheetView {
  @FXML private Stage stage;
  @FXML private TableView<TimesheetItem> timesheetTable;
  @FXML private TableColumn<TimesheetItem, String> dateColumn;
  @FXML private TableColumn<TimesheetItem, String> activityColumn;
  @FXML private TableColumn<TimesheetItem, String> durationColumn;

  private final TimesheetViewModel viewModel = ViewModels.newTimesheet();

  static TimesheetView newInstance(Stage owner) {
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
    timesheetTable.setItems(viewModel.getTimesheetItems());
    dateColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().date()));
    activityColumn.setCellValueFactory(
        param -> new SimpleObjectProperty<>(param.getValue().activity()));
    durationColumn.setCellValueFactory(
        param -> new SimpleObjectProperty<>(param.getValue().duration()));
  }

  public void run() {
    stage.show();
    viewModel.createTimesheet(LocalDate.of(2022, 11, 1), LocalDate.now());
  }
}
