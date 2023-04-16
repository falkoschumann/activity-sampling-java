/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.timesheet;

import de.muspellheim.activitysampling.domain.ActivitiesService;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.utilities.EventEmitter;
import de.muspellheim.utilities.Exceptions;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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

    var today = LocalDate.now(clock);
    var weekday = today.getDayOfWeek().getValue();
    from.set(today.minusDays(weekday - 1));
    to.set(today.plusDays(7 - weekday));
  }

  /* *************************************************************************
   *                                                                         *
   * Events                                                                  *
   *                                                                         *
   **************************************************************************/

  private final EventEmitter<List<String>> onError = new EventEmitter<>();

  void addOnErrorListener(Consumer<List<String>> listener) {
    onError.addListener(listener);
  }

  void removeOnErrorListener(Consumer<List<String>> listener) {
    onError.removeListener(listener);
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

  // --- title1

  private final ObservableStringValue title1 =
      Bindings.createStringBinding(
          () -> "This " + new ChronoUnitStringConverter().toString(period.get()) + ": ", period);

  ObservableStringValue title1Property() {
    return title1;
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

  /* *************************************************************************
   *                                                                         *
   * Public API                                                              *
   *                                                                         *
   **************************************************************************/

  void run() {
    load();
  }

  void load() {
    Timesheet timesheet;
    try {
      timesheet = activitiesService.getTimesheet(from.get(), to.get());
    } catch (Exception e) {
      var messages = Exceptions.collectExceptionMessages("Failed to load timesheet.", e);
      onError.emit(messages);
      return;
    }

    var timeFormat = "%1$02d:%2$02d";
    var items = new ArrayList<TimesheetItem>();
    for (var entry : timesheet.entries()) {
      items.add(
          new TimesheetItem(
              dateFormatter.format(entry.date()),
              entry.notes(),
              timeFormat.formatted(entry.hours().toHours(), entry.hours().toMinutesPart())));
    }
    timesheetItems.setAll(items);
    total.set(timeFormat.formatted(timesheet.total().toHours(), timesheet.total().toMinutesPart()));
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
      default -> throw new IllegalArgumentException(
          "Unsupported period: %s.".formatted(period.get()));
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
      default -> throw new IllegalArgumentException(
          "Unsupported period: %s.".formatted(period.get()));
    }
    load();
  }
}
