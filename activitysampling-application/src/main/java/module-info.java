module de.muspellheim.activitysampling.application {
  requires de.muspellheim.activitysampling.domain;
  requires de.muspellheim.activitysampling.infrastructure;
  requires javafx.controls;
  requires javafx.fxml;
  requires jdk.localedata;

  opens de.muspellheim.activitysampling.application to
      javafx.fxml,
      javafx.graphics;
}
