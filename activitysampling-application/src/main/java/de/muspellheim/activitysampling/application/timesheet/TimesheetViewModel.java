package de.muspellheim.activitysampling.application.timesheet;

import de.muspellheim.activitysampling.application.shared.*;
import de.muspellheim.activitysampling.domain.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;
import java.util.function.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;

public class TimesheetViewModel {
  private final ActivitiesService activitiesService;

  // Events
  private Consumer<String> onError;

  // State
  private final ObjectProperty<LocalDate> from;
  private final ObjectProperty<LocalDate> to;

  // Properties
  private final StringExpression title1;
  private final StringExpression title2;
  private final ObjectProperty<ChronoUnit> period;
  private final ObservableList<TimesheetItem> timesheetItems;
  private final ReadOnlyStringWrapper total;

  public TimesheetViewModel(ActivitiesService activitiesService) {
    this(activitiesService, Clock.systemDefaultZone());
  }

  public TimesheetViewModel(ActivitiesService activitiesService, Clock clock) {
    this.activitiesService = activitiesService;

    var today = LocalDate.now(clock);
    var weekday = today.getDayOfWeek().getValue();
    from = new SimpleObjectProperty<>(today.minusDays(weekday - 1));
    to = new SimpleObjectProperty<>(today.plusDays(7 - weekday));
    period =
        new SimpleObjectProperty<>(ChronoUnit.WEEKS) {
          @Override
          protected void invalidated() {
            switch (get()) {
              case DAYS -> to.set(from.get());
              case WEEKS -> {
                var first = from.get().with(ChronoField.DAY_OF_WEEK, 1);
                from.set(first);
                to.set(from.get().plusWeeks(1).minusDays(1));

                /*
                var weekday = from.get().getDayOfWeek().getValue();
                from.set(from.get().minusDays(weekday - 1));
                to.set(from.get().plusDays(7 - weekday + 1));
                */
              }
              case MONTHS -> {
                var first = from.get().withDayOfMonth(1);
                from.set(first);
                var last = first.plusMonths(1).minusDays(1);
                to.set(last);
              }
              default -> throw new IllegalStateException("Unreachable code");
            }
            load();
          }
        };
    timesheetItems = FXCollections.observableArrayList();
    total = new ReadOnlyStringWrapper("00:00");
    var periodConverter = new ChronoUnitStringConverter();
    title1 =
        Bindings.createStringBinding(
            () -> "This " + periodConverter.toString(period.get()) + ": ", period);
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    title2 =
        Bindings.createStringBinding(
            () ->
                dateFormatter.format(from.get())
                    + (period.get() == ChronoUnit.DAYS
                        ? ""
                        : " - " + dateFormatter.format(to.get())),
            from,
            to,
            period);
  }

  public Consumer<String> getOnError() {
    return onError;
  }

  public void setOnError(Consumer<String> onError) {
    this.onError = onError;
  }

  public StringExpression title1Property() {
    return title1;
  }

  public StringExpression title2Property() {
    return title2;
  }

  public ObjectProperty<ChronoUnit> periodProperty() {
    return period;
  }

  public ObservableList<TimesheetItem> getTimesheetItems() {
    return timesheetItems;
  }

  public ReadOnlyStringProperty totalProperty() {
    return total.getReadOnlyProperty();
  }

  public void run() {
    load();
  }

  public void back() {
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
      default -> throw new IllegalStateException("Unreachable code");
    }
    load();
  }

  public void forward() {
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
      default -> throw new IllegalStateException("Unreachable code");
    }
    load();
  }

  private void load() {
    Timesheet timesheet;
    try {
      timesheet = activitiesService.createTimesheet(from.get(), to.get());
    } catch (Exception e) {
      var message = Exceptions.joinExceptionMessages("Failed to load timesheet.", e);
      onError.accept(message);
      return;
    }

    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    var timeFormat = "%1$02d:%2$02d";
    var items = new ArrayList<TimesheetItem>();
    for (var entry : timesheet.getEntries()) {
      items.add(
          new TimesheetItem(
              dateFormatter.format(entry.date()),
              entry.notes(),
              timeFormat.formatted(entry.hours().toHours(), entry.hours().toMinutesPart())));
    }
    timesheetItems.setAll(items);
    total.set(
        timeFormat.formatted(timesheet.getTotal().toHours(), timesheet.getTotal().toMinutesPart()));
  }
}