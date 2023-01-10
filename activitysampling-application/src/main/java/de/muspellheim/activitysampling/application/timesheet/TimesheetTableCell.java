/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.timesheet;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

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
      setTextAlignment(null);
      setOnMouseClicked(null);
    } else {
      setText(item.toString());
      setAlignment(alignment);
    }
  }
}
