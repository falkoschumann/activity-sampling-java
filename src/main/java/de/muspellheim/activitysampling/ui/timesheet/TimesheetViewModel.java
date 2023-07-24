/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.timesheet;

import de.muspellheim.activitysampling.application.ActivitiesService;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.activitysampling.util.Durations;
import de.muspellheim.activitysampling.util.EventEmitter;
import de.muspellheim.activitysampling.util.OutputTracker;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TimesheetViewModel {

  private final ActivitiesService activitiesService;
  private final DateTimeFormatter dateFormatter;

  /* *************************************************************************
   *                                                                         *
   * Constructors                                                            *
   *                                                                         *
   **************************************************************************/

  TimesheetViewModel(ActivitiesService activitiesService) {
    this(activitiesService, Locale.getDefault());
  }

  public TimesheetViewModel(ActivitiesService activitiesService, Locale locale) {
    this.activitiesService = activitiesService;
    dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
  }

  /* *************************************************************************
   *                                                                         *
   * Events                                                                  *
   *                                                                         *
   **************************************************************************/

  private final EventEmitter<Throwable> errorOccurred = new EventEmitter<>();

  void addErrorOccurredListener(Consumer<Throwable> listener) {
    errorOccurred.addListener(listener);
  }

  void removeErrorOccurredListener(Consumer<Throwable> listener) {
    errorOccurred.removeListener(listener);
  }

  public OutputTracker<Throwable> trackErrorOccurred() {
    return new OutputTracker<>(errorOccurred);
  }

  /* *************************************************************************
   *                                                                         *
   * Properties                                                              *
   *                                                                         *
   **************************************************************************/

  // --- timesheetItems

  private final ObservableList<TimesheetItem> timesheetItems = FXCollections.observableArrayList();

  public ObservableList<TimesheetItem> getTimesheetItems() {
    return timesheetItems;
  }

  // --- totalLabelText

  private final ReadOnlyStringWrapper totalLabelText = new ReadOnlyStringWrapper("00:00");

  ObservableStringValue totalLabelTextProperty() {
    return totalLabelText.getReadOnlyProperty();
  }

  public String getTotalLabelText() {
    return totalLabelText.get();
  }

  /* *************************************************************************
   *                                                                         *
   * Public API                                                              *
   *                                                                         *
   **************************************************************************/

  public void load(LocalDate from, LocalDate to) {
    try {
      var timesheet = activitiesService.getTimesheet(from, to);
      updateTimesheetItems(timesheet.entries());
      updateTotal(timesheet.total());
    } catch (Exception e) {
      e.printStackTrace();
      errorOccurred.emit(new Exception("Failed to load timesheet.", e));
    }
  }

  private void updateTimesheetItems(List<Timesheet.Entry> entries) {
    var items = new ArrayList<TimesheetItem>();
    for (var entry : entries) {
      items.add(
          TimesheetItem.builder()
              .date(dateFormatter.format(entry.date()))
              .client(entry.client())
              .project(entry.project())
              .task(entry.task())
              .hours(Durations.format(entry.hours(), FormatStyle.SHORT))
              .build());
    }
    timesheetItems.setAll(items);
  }

  private void updateTotal(Duration total) {
    totalLabelText.set(Durations.format(total, FormatStyle.SHORT));
  }
}
