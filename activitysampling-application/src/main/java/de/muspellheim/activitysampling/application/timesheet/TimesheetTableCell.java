/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.timesheet;

import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

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
      setContextMenu(null);
    } else {
      setText(item.toString());
      setAlignment(alignment);

      var copyMenuItem = new MenuItem("Copy");
      copyMenuItem.setOnAction(
          event -> {
            var content = new ClipboardContent();
            content.putString(item.toString());
            Clipboard.getSystemClipboard().setContent(content);
          });
      var menu = new ContextMenu(copyMenuItem);
      setContextMenu(menu);
    }
  }
}
