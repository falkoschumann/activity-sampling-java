/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.shared;

import java.util.*;
import javafx.scene.control.*;

public class ErrorView {
  private ErrorView() {
    // Do not instantiate static class.
  }

  public static void handleError(List<String> messages) {
    var alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    var header = messages.get(0);
    alert.setHeaderText(header);
    var content = messages.stream().skip(1).toList();
    alert.setContentText(String.join("\n\n", content));
    alert.show();
  }
}
