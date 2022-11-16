package de.muspellheim.activitysampling.application;

import javafx.scene.control.*;

class ActivityListCell extends ListCell<ActivityItem> {
  protected void updateItem(ActivityItem item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      setText(null);
      setGraphic(null);
      setStyle(null);
    } else {
      setText(item.text());
      if (item.header()) {
        setStyle("-fx-font-weight: bold");
      } else {
        setStyle(null);
      }
    }
  }
}
