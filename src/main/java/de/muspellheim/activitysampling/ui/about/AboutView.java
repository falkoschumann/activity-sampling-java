/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.about;

import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AboutView {
  @FXML private Stage stage;
  @FXML private Label versionLabel;
  @FXML private Label initialYearLabel;
  @FXML private Label currentYearLabel;

  public static AboutView newInstance(Stage owner) {
    String file = "/AboutView.fxml";
    try {
      var url = AboutView.class.getResource(file);
      var loader = new FXMLLoader(url);
      var stage = new Stage();
      stage.initOwner(owner);
      loader.setRoot(stage);
      loader.load();
      return loader.getController();
    } catch (Exception e) {
      throw new IllegalStateException("Could not load view: " + file, e);
    }
  }

  @FXML
  private void initialize() {
    versionLabel.setText(System.getProperty("jpackage.app-version", "1.0.0"));
    initializeCurrentYear();
  }

  private void initializeCurrentYear() {
    var initialYear = Integer.parseInt(initialYearLabel.getText());
    var currentYear = LocalDate.now().getYear();
    if (initialYear == currentYear) {
      currentYearLabel.setText("");
    } else {
      currentYearLabel.setText("-" + currentYear);
    }
  }

  public void run() {
    stage.show();
  }
}
