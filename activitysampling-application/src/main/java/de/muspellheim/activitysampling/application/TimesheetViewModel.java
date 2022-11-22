package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import java.time.*;
import javafx.collections.*;

class TimesheetViewModel {
  private final ObservableList<TimesheetItem> timesheetItems = FXCollections.observableArrayList();
  private final ActivitiesService activitiesService;

  TimesheetViewModel(ActivitiesService activitiesService) {
    this.activitiesService = activitiesService;
  }

  ObservableList<TimesheetItem> getTimesheetItems() {
    return timesheetItems;
  }

  void createTimesheet(LocalDate from, LocalDate to) {
    /*
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    var timeFormat = "%1$02d:%2$02d";
    List<TimesheetItem> items = List.of();
    Timesheet timesheet = activitiesService.createTimesheet(from, to);
    for (var day : timesheet.workingDays()) {
      items.add(new TimesheetItem(day.date().format(dateFormatter)));
      for (var activity : day.activities()) {
        items.add(
            new TimesheetItem()Item(
                activity.timestamp().format(timeFormatter) + " - " + activity.description(),
                activity));
      }
    }
    total.set(timesheet.total());
    timesheetItems.setAll(items);
    */
  }
}
