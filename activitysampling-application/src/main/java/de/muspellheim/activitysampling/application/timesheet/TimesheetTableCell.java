package de.muspellheim.activitysampling.application.timesheet;

import javafx.geometry.*;
import javafx.scene.control.*;

class TimesheetTableCell<T> extends TableCell<TimesheetItem, T> {
  private final Pos alignment;

  TimesheetTableCell() {
    this(Pos.BASELINE_LEFT);
  }

  TimesheetTableCell(Pos alignment) {
    this.alignment = alignment;
  }

  @Override
  protected void updateItem(T item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      setText(null);
      setGraphic(null);
      getStyleClass().removeAll("base");
      setTextAlignment(null);
      setOnMouseClicked(null);
    } else {
      setText(item.toString());
      setAlignment(alignment);
      getStyleClass().add("base");
      if (getTableRow().getItem().date().isBlank()) {
        getStyleClass().removeAll("base");
      }
    }
  }
}
