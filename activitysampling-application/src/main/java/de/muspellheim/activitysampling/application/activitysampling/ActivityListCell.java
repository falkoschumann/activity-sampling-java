/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import de.muspellheim.activitysampling.domain.Activity;
import java.util.function.Consumer;
import javafx.scene.control.ListCell;

class ActivityListCell extends ListCell<ActivityItem> {
  private static final int DOUBLE_CLICK_COUNT = 2;

  private final Consumer<Activity> onSelect;

  ActivityListCell(Consumer<Activity> onSelect) {
    this.onSelect = onSelect;
  }

  @Override
  protected void updateItem(ActivityItem item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      setText(null);
      setGraphic(null);
      getStyleClass().removeAll("base");
      setOnMouseClicked(null);
    } else {
      setText(item.text());
      getStyleClass().removeAll("base");
      if (item.activity() != null) {
        // Activity
        setOnMouseClicked(
            e -> {
              if (e.getClickCount() != DOUBLE_CLICK_COUNT) {
                return;
              }

              onSelect.accept(item.activity());
            });
      } else {
        // Group header
        getStyleClass().add("base");
        setOnMouseClicked(null);
      }
    }
  }
}
