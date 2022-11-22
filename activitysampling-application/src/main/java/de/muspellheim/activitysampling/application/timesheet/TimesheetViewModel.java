package de.muspellheim.activitysampling.application.timesheet;

import de.muspellheim.activitysampling.application.shared.*;
import de.muspellheim.activitysampling.domain.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.function.*;
import javafx.beans.property.*;
import javafx.collections.*;

public class TimesheetViewModel {
  private final ActivitiesService activitiesService;
  private final Consumer<String> onError;

  // Properties
  private final ObjectProperty<LocalDate> from;
  private final ObjectProperty<LocalDate> to;
  private final ObservableList<TimesheetItem> timesheetItems;
  private final ReadOnlyStringWrapper total;

  public TimesheetViewModel(ActivitiesService activitiesService, Consumer<String> onError) {
    this.activitiesService = activitiesService;
    this.onError = onError;

    to = new SimpleObjectProperty<>(LocalDate.now());
    from = new SimpleObjectProperty<>(to.get().withDayOfMonth(1));
    timesheetItems = FXCollections.observableArrayList();
    total = new ReadOnlyStringWrapper("00:00");
  }

  public ObjectProperty<LocalDate> fromProperty() {
    return from;
  }

  public ObjectProperty<LocalDate> toProperty() {
    return to;
  }

  public ObservableList<TimesheetItem> getTimesheetItems() {
    return timesheetItems;
  }

  public ReadOnlyStringProperty totalProperty() {
    return total.getReadOnlyProperty();
  }

  public void update() {
    Timesheet timesheet;
    try {
      timesheet = activitiesService.createTimesheet(from.get(), to.get());
    } catch (Exception e) {
      var message = Exceptions.joinExceptionMessages("Failed to update timesheet.", e);
      onError.accept(message);
      return;
    }

    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    var timeFormat = "%1$02d:%2$02d";
    var items = new ArrayList<TimesheetItem>();
    for (var day : timesheet.workingDays()) {
      items.add(
          new TimesheetItem(
              day.date().format(dateFormatter),
              "Total",
              timeFormat.formatted(day.total().toHours(), day.total().toMinutesPart())));
      for (var activity : day.activities()) {
        items.add(
            new TimesheetItem(
                "",
                activity.activity(),
                timeFormat.formatted(
                    activity.duration().toHours(), activity.duration().toMinutesPart())));
      }
    }
    timesheetItems.setAll(items);
    total.set(timeFormat.formatted(timesheet.total().toHours(), timesheet.total().toMinutesPart()));
  }
}
