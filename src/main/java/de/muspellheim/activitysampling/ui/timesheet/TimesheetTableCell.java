/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.timesheet;

import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Callback;

class TimesheetTableCell<T> extends TableCell<TimesheetItem, T> {
  private final Pos alignment;

  private TimesheetTableCell(Pos alignment) {
    this.alignment = alignment;
  }

  static <T> Callback<TableColumn<TimesheetItem, T>, TableCell<TimesheetItem, T>> newCellFactory(
      Pos alignment) {
    return column -> new TimesheetTableCell<>(alignment);
  }

  static <T>
      Callback<TableColumn.CellDataFeatures<TimesheetItem, T>, ObservableValue<T>>
          newCellValueFactory(Function<TimesheetItem, T> valueProvider) {
    return param -> new SimpleObjectProperty<>(valueProvider.apply(param.getValue()));
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
