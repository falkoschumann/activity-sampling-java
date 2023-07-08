/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.activitysampling;

import java.util.function.Consumer;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Callback;

class ActivityListCell extends ListCell<ActivityItem> {
  private static final int DOUBLE_CLICK_COUNT = 2;

  private final Consumer<String> onSelect;

  private ActivityListCell(Consumer<String> onSelect) {
    this.onSelect = onSelect;
  }

  static Callback<ListView<ActivityItem>, ListCell<ActivityItem>> newCellFactory(
      Consumer<String> onSelect) {
    return view -> new ActivityListCell(onSelect);
  }

  @Override
  protected void updateItem(ActivityItem item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      setText(null);
      setGraphic(null);
      getStyleClass().removeAll("base");
      setContextMenu(null);
      setOnMouseClicked(null);
    } else {
      setText(item.text());
      getStyleClass().removeAll("base");

      if (item.isActivity()) {
        // Activity
        var copyMenuItem = new MenuItem("Copy");
        copyMenuItem.setOnAction(
            event -> {
              var content = new ClipboardContent();
              content.putString(item.description());
              Clipboard.getSystemClipboard().setContent(content);
            });
        var menu = new ContextMenu(copyMenuItem);
        setContextMenu(menu);

        setOnMouseClicked(
            e -> {
              if (e.getClickCount() != DOUBLE_CLICK_COUNT) {
                return;
              }

              onSelect.accept(item.description());
            });
      } else {
        // Group header
        getStyleClass().add("base");
        setOnMouseClicked(null);
      }
    }
  }
}
