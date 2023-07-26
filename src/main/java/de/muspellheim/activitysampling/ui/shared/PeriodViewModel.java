/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.shared;

import de.muspellheim.activitysampling.util.EventEmitter;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;

public class PeriodViewModel {

  /* *************************************************************************
   *                                                                         *
   * Constructors                                                            *
   *                                                                         *
   **************************************************************************/

  public PeriodViewModel() {
    this(Locale.getDefault(), Clock.systemDefaultZone());
  }

  public PeriodViewModel(Locale locale, Clock clock) {
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
    initPeriodWithCurrentWeek(clock);

    periodOfLabelText.bind(
        Bindings.createStringBinding(
            () ->
                "This "
                    + new ChronoUnitStringConverter().toString(periodOfChoiceValue.get())
                    + ": ",
            periodOfChoiceValue));
    periodFromToLabelText.bind(
        Bindings.createStringBinding(
            () ->
                dateFormatter.format(from.get())
                    + (periodOfChoiceValue.get() == ChronoUnit.DAYS
                        ? ""
                        : " - " + dateFormatter.format(to.get())),
            from,
            to,
            periodOfChoiceValue));
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

  private final EventEmitter<Void> periodChanged = new EventEmitter<>();

  public final void addPeriodChangedListener(Consumer<Void> listener) {
    periodChanged.addListener(listener);
  }

  public final void removePeriodChangedListener(Consumer<Void> listener) {
    periodChanged.removeListener(listener);
  }

  /* *************************************************************************
   *                                                                         *
   * Properties                                                              *
   *                                                                         *
   **************************************************************************/

  // --- from

  private final ReadOnlyObjectWrapper<LocalDate> from = new ReadOnlyObjectWrapper<>();

  public final ReadOnlyObjectProperty<LocalDate> fromProperty() {
    return from.getReadOnlyProperty();
  }

  public final LocalDate getFrom() {
    return from.get();
  }

  // --- to

  private final ReadOnlyObjectWrapper<LocalDate> to = new ReadOnlyObjectWrapper<>();

  public final LocalDate getTo() {
    return to.get();
  }

  public final ReadOnlyObjectProperty<LocalDate> toProperty() {
    return to.getReadOnlyProperty();
  }

  // --- periodOfChoiceValue

  private final ObjectProperty<ChronoUnit> periodOfChoiceValue =
      new SimpleObjectProperty<>(ChronoUnit.WEEKS) {
        @Override
        protected void invalidated() {
          if (get() == null) {
            return;
          }

          switch (get()) {
            case DAYS -> to.set(from.get());
            case WEEKS -> {
              var first = from.get().with(ChronoField.DAY_OF_WEEK, 1);
              from.set(first);
              var last = from.get().plusWeeks(1).minusDays(1);
              to.set(last);
            }
            case MONTHS -> {
              var first = from.get().withDayOfMonth(1);
              from.set(first);
              var last = first.plusMonths(1).minusDays(1);
              to.set(last);
            }
            case YEARS -> {
              var first = from.get().withDayOfYear(1);
              from.set(first);
              var last = first.plusYears(1).minusDays(1);
              to.set(last);
            }
            default -> throw new IllegalArgumentException(
                "Unsupported period: %s.".formatted(get()));
          }
          periodChanged.emit(null);
        }
      };

  public final ObjectProperty<ChronoUnit> periodOfChoiceValueProperty() {
    return periodOfChoiceValue;
  }

  public final ChronoUnit getPeriodOfChoiceValue() {
    return periodOfChoiceValue.get();
  }

  public final void setPeriodOfChoiceValue(ChronoUnit value) {
    periodOfChoiceValueProperty().set(value);
  }

  // --- periodOfLabelText

  private final ReadOnlyStringWrapper periodOfLabelText = new ReadOnlyStringWrapper();

  public final ReadOnlyStringProperty periodOfLabelTextProperty() {
    return periodOfLabelText.getReadOnlyProperty();
  }

  public final String getPeriodOfLabelText() {
    return periodOfLabelText.get();
  }

  // --- periodFromToLabelText

  private final ReadOnlyStringWrapper periodFromToLabelText = new ReadOnlyStringWrapper();

  public final ReadOnlyStringProperty periodFromToLabelTextProperty() {
    return periodFromToLabelText.getReadOnlyProperty();
  }

  public final String getPeriodFromToLabelText() {
    return periodFromToLabelText.get();
  }

  /* *************************************************************************
   *                                                                         *
   * Public API                                                              *
   *                                                                         *
   **************************************************************************/

  public void back() {
    switch (periodOfChoiceValue.get()) {
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
    periodChanged.emit(null);
  }

  public void forward() {
    switch (periodOfChoiceValue.get()) {
      case DAYS -> {
        from.set(from.get().plusDays(1));
        to.set(to.get().plusDays(1));
      }
      case WEEKS -> {
        from.set(from.get().plusWeeks(1));
        to.set(to.get().plusWeeks(1));
      }
      case MONTHS -> {
        var firstDay = from.get().plusMonths(1);
        from.set(firstDay);
        var lastDay = firstDay.plusMonths(1).minusDays(1);
        to.set(lastDay);
      }
      default -> {
        // do nothing
      }
    }
    periodChanged.emit(null);
  }
}
