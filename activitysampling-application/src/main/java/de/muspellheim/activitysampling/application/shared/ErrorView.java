package de.muspellheim.activitysampling.application.shared;

import javafx.scene.control.*;

public class ErrorView {
  private ErrorView() {
    // Do not instantiate static class.
  }

  public static void handleError(String message) {
    var alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("An unexpected error occurred.");
    alert.setContentText(message);
    alert.show();
  }
}
