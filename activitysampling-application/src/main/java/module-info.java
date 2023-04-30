module de.muspellheim.activitysampling.application {
  requires de.muspellheim.activitysampling.domain;
  requires de.muspellheim.activitysampling.infrastructure;
  requires de.muspellheim.common;
  requires java.desktop;
  requires javafx.controls;
  requires javafx.fxml;
  requires jdk.localedata;

  opens de.muspellheim.activitysampling.application to
      javafx.graphics;
  opens de.muspellheim.activitysampling.application.activitysampling to
      javafx.fxml;
  opens de.muspellheim.activitysampling.application.timesheet to
      javafx.fxml;
}
