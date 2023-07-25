/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.time;

import de.muspellheim.activitysampling.application.ActivitiesService;
import de.muspellheim.activitysampling.domain.TimeReport;
import de.muspellheim.activitysampling.util.Durations;
import de.muspellheim.activitysampling.util.EventEmitter;
import de.muspellheim.activitysampling.util.OutputTracker;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TimeViewModel {

  public enum Scope {
    CLIENTS,
    PROJECTS,
    TASKS,
  }

  private final ActivitiesService activitiesService;

  /* *************************************************************************
   *                                                                         *
   * Constructors                                                            *
   *                                                                         *
   **************************************************************************/

  public TimeViewModel(ActivitiesService activitiesService) {
    this.activitiesService = activitiesService;
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

  private final ObservableList<TimeItem> timeItems = FXCollections.observableArrayList();

  public ObservableList<TimeItem> getTimeItems() {
    return timeItems;
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

  public void load(LocalDate from, LocalDate to, Scope scope) {
    try {
      var report = activitiesService.getTimeReport(from, to);
      if (scope == Scope.CLIENTS) {
        report = report.groupByClient();
      } else if (scope == Scope.PROJECTS) {
        report = report.groupByProject();
      }
      updateReportItems(report.entries());
      updateTotal(report.total());
    } catch (Exception e) {
      e.printStackTrace();
      errorOccurred.emit(new Exception("Failed to load report.", e));
    }
  }

  private void updateReportItems(List<TimeReport.Entry> entries) {
    var items = new ArrayList<TimeItem>();
    for (var entry : entries) {
      items.add(
          TimeItem.builder()
              .client(entry.client())
              .project(entry.project())
              .task(entry.task())
              .hours(Durations.format(entry.hours(), FormatStyle.SHORT))
              .build());
    }
    timeItems.setAll(items);
  }

  private void updateTotal(Duration total) {
    totalLabelText.set(Durations.format(total, FormatStyle.SHORT));
  }
}
