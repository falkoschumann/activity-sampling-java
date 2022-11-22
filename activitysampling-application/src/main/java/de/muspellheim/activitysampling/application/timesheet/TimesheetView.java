package de.muspellheim.activitysampling.application.timesheet;

import de.muspellheim.activitysampling.application.*;
import de.muspellheim.activitysampling.application.shared.*;
import javafx.beans.property.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class TimesheetView {
  @FXML private Stage stage;
  @FXML private DatePicker from;
  @FXML private DatePicker to;
  @FXML private TableView<TimesheetItem> timesheetTable;
  @FXML private TableColumn<TimesheetItem, String> dateColumn;
  @FXML private TableColumn<TimesheetItem, String> activityColumn;
  @FXML private TableColumn<TimesheetItem, String> durationColumn;
  @FXML private TextField total;

  private final TimesheetViewModel viewModel = ViewModels.newTimesheet(ErrorView::handleError);

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
    from.valueProperty().bindBidirectional(viewModel.fromProperty());
    to.valueProperty().bindBidirectional(viewModel.toProperty());
    timesheetTable.setItems(viewModel.getTimesheetItems());
    dateColumn.setCellFactory(column -> new TimesheetTableCell<>(Pos.BASELINE_CENTER));
    dateColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().date()));
    activityColumn.setCellFactory(column -> new TimesheetTableCell<>());
    activityColumn.setCellValueFactory(
        param -> new SimpleObjectProperty<>(param.getValue().activity()));
    durationColumn.setCellFactory(column -> new TimesheetTableCell<>(Pos.BASELINE_CENTER));
    durationColumn.setCellValueFactory(
        param -> new SimpleObjectProperty<>(param.getValue().duration()));
    total.textProperty().bind(viewModel.totalProperty());
  }

  public void run() {
    stage.show();
    viewModel.update();
  }

  @FXML
  private void handleUpdate() {
    viewModel.update();
  }
}
