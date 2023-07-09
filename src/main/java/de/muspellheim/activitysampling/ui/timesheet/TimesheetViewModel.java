/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.timesheet;

import de.muspellheim.activitysampling.domain.ActivitiesService;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.activitysampling.util.Durations;
import de.muspellheim.activitysampling.util.EventEmitter;
import de.muspellheim.activitysampling.util.OutputTracker;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class TimesheetViewModel {
  private final ActivitiesService activitiesService;
  private DateTimeFormatter dateFormatter;

  // State
  private final ObjectProperty<LocalDate> from = new SimpleObjectProperty<>();
  private final ObjectProperty<LocalDate> to = new SimpleObjectProperty<>();

  /* *************************************************************************
   *                                                                         *
   * Constructors                                                            *
   *                                                                         *
   **************************************************************************/

  TimesheetViewModel(ActivitiesService activitiesService) {
    this(activitiesService, Locale.getDefault(), Clock.systemDefaultZone());
  }

  TimesheetViewModel(ActivitiesService activitiesService, Locale locale, Clock clock) {
    this.activitiesService = activitiesService;
    dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
    initPeriodWithCurrentWeek(clock);
  }

  private void initPeriodWithCurrentWeek(Clock clock) {
    var today = LocalDate.now(clock);
    var weekday = today.getDayOfWeek().getValue();
    var monday = today.minusDays(weekday - 1);
    var sunday = today.plusDays(7 - weekday);
    from.set(monday);
    to.set(sunday);
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

  OutputTracker<Throwable> trackErrorOccurred() {
    return new OutputTracker<>(errorOccurred);
  }

  /* *************************************************************************
   *                                                                         *
   * Properties                                                              *
   *                                                                         *
   **************************************************************************/

  // --- period

  private final ObjectProperty<ChronoUnit> period =
      new SimpleObjectProperty<>(ChronoUnit.WEEKS) {
        @Override
        protected void invalidated() {
          switch (get()) {
            case DAYS -> to.set(from.get());
            case WEEKS -> {
              var first = from.get().with(ChronoField.DAY_OF_WEEK, 1);
              from.set(first);
              to.set(from.get().plusWeeks(1).minusDays(1));
            }
            case MONTHS -> {
              var first = from.get().withDayOfMonth(1);
              from.set(first);
              var last = first.plusMonths(1).minusDays(1);
              to.set(last);
            }
            default -> throw new IllegalArgumentException(
                "Unsupported period: %s.".formatted(get()));
          }
          load();
        }
      };

  ObjectProperty<ChronoUnit> periodProperty() {
    return period;
  }

  ChronoUnit getPeriod() {
    return period.get();
  }

  void setPeriod(ChronoUnit value) {
    periodProperty().set(value);
  }

  // --- title1

  private final ObservableStringValue title1 =
      Bindings.createStringBinding(
          () -> "This " + new ChronoUnitStringConverter().toString(period.get()) + ": ", period);

  ObservableStringValue title1Property() {
    return title1;
  }

  String getTitle1() {
    return title1.get();
  }

  // --- title2

  private final ObservableStringValue title2 =
      Bindings.createStringBinding(
          () ->
              dateFormatter.format(from.get())
                  + (period.get() == ChronoUnit.DAYS ? "" : " - " + dateFormatter.format(to.get())),
          from,
          to,
          period);

  ObservableStringValue title2Property() {
    return title2;
  }

  String getTitle2() {
    return title2.get();
  }

  // --- timesheetItems

  private final ObservableList<TimesheetItem> timesheetItems = FXCollections.observableArrayList();

  ObservableList<TimesheetItem> getTimesheetItems() {
    return timesheetItems;
  }

  // --- total

  private final ReadOnlyStringWrapper total = new ReadOnlyStringWrapper("00:00");

  ObservableStringValue totalProperty() {
    return total.getReadOnlyProperty();
  }

  String getTotal() {
    return total.get();
  }

  /* *************************************************************************
   *                                                                         *
   * Public API                                                              *
   *                                                                         *
   **************************************************************************/

  void load() {
    try {
      var timesheet = activitiesService.getTimesheet(from.get(), to.get());
      updateTimesheetItems(timesheet);
      updateTotal(timesheet);
    } catch (Exception e) {
      errorOccurred.emit(new Exception("Failed to load timesheet.", e));
    }
  }

  private void updateTimesheetItems(Timesheet timesheet) {
    var items = new ArrayList<TimesheetItem>();
    for (var entry : timesheet.entries()) {
      items.add(
          new TimesheetItem(
              dateFormatter.format(entry.date()),
              entry.notes(),
              Durations.format(entry.hours(), FormatStyle.SHORT)));
    }
    timesheetItems.setAll(items);
  }

  private void updateTotal(Timesheet timesheet) {
    total.set(Durations.format(timesheet.total(), FormatStyle.SHORT));
  }

  void back() {
    switch (period.get()) {
      case DAYS -> {
        from.set(from.get().minusDays(1));
        to.set(to.get().minusDays(1));
      }
      case WEEKS -> {
        from.set(from.get().minusWeeks(1));
        to.set(to.get().minusWeeks(1));
      }
      case MONTHS -> {
        var first = from.get().minusMonths(1);
        from.set(first);
        var last = first.plusMonths(1).minusDays(1);
        to.set(last);
      }
      default -> {
        // do nothing
      }
    }
    load();
  }

  void forward() {
    switch (period.get()) {
      case DAYS -> {
        from.set(from.get().plusDays(1));
        to.set(to.get().plusDays(1));
      }
      case WEEKS -> {
        from.set(from.get().plusWeeks(1));
        to.set(to.get().plusWeeks(1));
      }
      case MONTHS -> {
        var first = from.get().plusMonths(1);
        from.set(first);
        var last = first.plusMonths(1).minusDays(1);
        to.set(last);
      }
      default -> {
        // do nothing
      }
    }
    load();
  }
}
