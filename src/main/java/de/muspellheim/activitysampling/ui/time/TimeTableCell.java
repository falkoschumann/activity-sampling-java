/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.time;

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

class TimeTableCell<T> extends TableCell<TimeItem, T> {
  private final Pos alignment;

  private TimeTableCell(Pos alignment) {
    this.alignment = alignment;
  }

  static <T> Callback<TableColumn<TimeItem, T>, TableCell<TimeItem, T>> newCellFactory(
      Pos alignment) {
    return column -> new TimeTableCell<>(alignment);
  }

  static <T>
      Callback<TableColumn.CellDataFeatures<TimeItem, T>, ObservableValue<T>> newCellValueFactory(
          Function<TimeItem, T> valueProvider) {
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
