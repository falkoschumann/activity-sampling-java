/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import de.muspellheim.activitysampling.domain.*;
import java.util.function.*;
import javafx.scene.control.*;

class ActivityListCell extends ListCell<ActivityItem> {
  private final Consumer<Activity> onSelect;

  ActivityListCell(Consumer<Activity> onSelect) {
    this.onSelect = onSelect;
  }

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
              if (e.getClickCount() != 2) {
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
