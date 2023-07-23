/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.time;

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

public class TimeView {

  @FXML private Stage stage;
  @FXML private PeriodView periodViewController;
  @FXML private TableView<TimeItem> timeTable;
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
    clientColumn.setCellFactory(TimeTableCell.newCellFactory(Pos.BASELINE_LEFT));
    clientColumn.setCellValueFactory(TimeTableCell.newCellValueFactory(TimeItem::client));
    hoursColumn.setCellFactory(TimeTableCell.newCellFactory(Pos.BASELINE_CENTER));
    hoursColumn.setCellValueFactory(TimeTableCell.newCellValueFactory(TimeItem::hours));

    periodViewController.addPeriodChangedListener(
        e -> viewModel.load(periodViewController.getFrom(), periodViewController.getTo()));
    viewModel.addErrorOccurredListener(ErrorView::show);
    timeTable.setItems(viewModel.getTimeItems());
    totalLabel.textProperty().bind(viewModel.totalLabelTextProperty());
  }

  public void run() {
    stage.show();
    viewModel.load(periodViewController.getFrom(), periodViewController.getTo());
  }
}
