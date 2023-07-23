module de.muspellheim.activitysampling {
  requires org.apache.commons.csv;
  requires static lombok;
  requires java.desktop;
  requires static java.sql;
  requires javafx.controls;
  requires javafx.fxml;
  requires jdk.localedata;

  opens de.muspellheim.activitysampling.ui to
      javafx.graphics;
  opens de.muspellheim.activitysampling.ui.activitysampling to
      javafx.fxml;
  opens de.muspellheim.activitysampling.ui.timesheet to
      javafx.fxml;
  opens de.muspellheim.activitysampling.ui.shared to
      javafx.fxml;
}
