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
import lombok.Getter;

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

  @Getter private final ObservableList<TimeItem> timeItems = FXCollections.observableArrayList();

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
      } else {
        report = report.groupByTask();
      }
      updateReportItems(report.entries(), scope);
      updateTotal(report.total());
    } catch (Exception e) {
      errorOccurred.emit(new Exception("Failed to load report.", e));
    }
  }

  private void updateReportItems(List<TimeReport.Entry> entries, Scope scope) {
    var items = new ArrayList<TimeItem>();
    for (var entry : entries) {
      // WORKAROUND: for https://github.com/checkstyle/checkstyle/issues/12817
      // CHECKSTYLE.OFF: Indentation
      var name =
          switch (scope) {
            case CLIENTS -> entry.client();
            case PROJECTS -> entry.project();
            case TASKS -> entry.task();
          };
      // CHECKSTYLE.ON: Indentation
      items.add(
          TimeItem.builder()
              .name(name)
              .client(entry.client())
              .hours(Durations.format(entry.hours(), FormatStyle.SHORT))
              .build());
    }
    timeItems.setAll(items);
  }

  private void updateTotal(Duration total) {
    totalLabelText.set(Durations.format(total, FormatStyle.SHORT));
  }
}
