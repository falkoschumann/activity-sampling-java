package de.muspellheim.activitysampling.application;

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
      setStyle(null);
      setOnMouseClicked(null);
    } else {
      setText(item.text());
      if (item.activity() != null) {
        // Activity
        setStyle(null);
        setOnMouseClicked(
            e -> {
              if (e.getClickCount() != 2) {
                return;
              }

              onSelect.accept(item.activity());
            });
      } else {
        // Group header
        setStyle("-fx-font-weight: bold");
        setOnMouseClicked(null);
      }
    }
  }
}
