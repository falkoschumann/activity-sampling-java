module de.muspellheim.activitysampling {
  requires org.apache.commons.csv;
  requires static lombok;
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
