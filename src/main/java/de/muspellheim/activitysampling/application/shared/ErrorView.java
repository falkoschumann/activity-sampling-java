/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.shared;

import de.muspellheim.common.util.Exceptions;
import javafx.scene.control.Alert;

public class ErrorView {
  private ErrorView() {
    // Do not instantiate static class.
  }

  public static void show(Throwable exception) {
    show(exception.getMessage(), exception.getCause());
  }

  public static void show(String errorMessage, Throwable exception) {
    var alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(errorMessage);
    var messages =
        Exceptions.collect(exception).stream().map(Throwable::getLocalizedMessage).toList();
    alert.setContentText(String.join("\n", messages));
    alert.show();
  }
}
