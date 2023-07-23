/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.muspellheim.activitysampling.ui.shared.PeriodViewModel;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PeriodViewModelTests {

  private PeriodViewModel sut;

  @BeforeEach
  void init() {
    var clock = Clock.fixed(Instant.parse("2023-04-12T12:00:00Z"), ZoneId.systemDefault());
    sut = new PeriodViewModel(Locale.GERMANY, clock);
  }

  @Test
  void newInstance() {
    assertValues(LocalDate.of(2023, 4, 10), LocalDate.of(2023, 4, 16), ChronoUnit.WEEKS);
    assertLabels("This Week: ", "10.04.2023 - 16.04.2023");
  }

  @Test
  void setPeriodOfChoiceValue_FromWeekToDay_DisplaysMonday() {
    sut.setPeriodOfChoiceValue(ChronoUnit.DAYS);

    assertValues(LocalDate.of(2023, 4, 10), LocalDate.of(2023, 4, 10), ChronoUnit.DAYS);
    assertLabels("This Day: ", "10.04.2023");
  }

  @Test
  void setPeriodOfChoiceValue_FromWeekToMonth_DisplaysEntireMonth() {
    sut.setPeriodOfChoiceValue(ChronoUnit.MONTHS);

    assertValues(LocalDate.of(2023, 4, 1), LocalDate.of(2023, 4, 30), ChronoUnit.MONTHS);
    assertLabels("This Month: ", "01.04.2023 - 30.04.2023");
  }

  @Test
  void setPeriodOfChoiceValue_FromDayToWeek_DisplaysEntireWeek() {
    sut.setPeriodOfChoiceValue(ChronoUnit.DAYS);

    sut.setPeriodOfChoiceValue(ChronoUnit.WEEKS);

    assertValues(LocalDate.of(2023, 4, 10), LocalDate.of(2023, 4, 16), ChronoUnit.WEEKS);
    assertLabels("This Week: ", "10.04.2023 - 16.04.2023");
  }

  @Test
  void setPeriodOfChoiceValue_FromDayToMonth_DisplaysEntireMonth() {
    sut.setPeriodOfChoiceValue(ChronoUnit.DAYS);

    sut.setPeriodOfChoiceValue(ChronoUnit.MONTHS);

    assertValues(LocalDate.of(2023, 4, 1), LocalDate.of(2023, 4, 30), ChronoUnit.MONTHS);
    assertLabels("This Month: ", "01.04.2023 - 30.04.2023");
  }

  @Test
  void setPeriodOfChoiceValue_FromMonthToDay_DisplaysFirstDayOfMonth() {
    sut.setPeriodOfChoiceValue(ChronoUnit.MONTHS);

    sut.setPeriodOfChoiceValue(ChronoUnit.DAYS);

    assertValues(LocalDate.of(2023, 4, 1), LocalDate.of(2023, 4, 1), ChronoUnit.DAYS);
    assertLabels("This Day: ", "01.04.2023");
  }

  @Test
  void setPeriodOfChoiceValue_FromMonthToWeek_DisplaysFirstWeekOfMonth() {
    sut.setPeriodOfChoiceValue(ChronoUnit.MONTHS);

    sut.setPeriodOfChoiceValue(ChronoUnit.WEEKS);

    assertValues(LocalDate.of(2023, 3, 27), LocalDate.of(2023, 4, 2), ChronoUnit.WEEKS);
    assertLabels("This Week: ", "27.03.2023 - 02.04.2023");
  }

  @Test
  void setPeriodOfChoiceValue_UnsupportedPeriod_ThrowsException() {
    assertThrows(
        IllegalArgumentException.class, () -> sut.setPeriodOfChoiceValue(ChronoUnit.HOURS));
  }

  @Test
  void back_PeriodIsDay_DisplaysYesterday() {
    sut.setPeriodOfChoiceValue(ChronoUnit.DAYS);

    sut.back();

    assertValues(LocalDate.of(2023, 4, 9), LocalDate.of(2023, 4, 9), ChronoUnit.DAYS);
    assertLabels("This Day: ", "09.04.2023");
  }

  @Test
  void back_PeriodIsWeek_DisplaysLastWeek() {
    sut.back();

    assertValues(LocalDate.of(2023, 4, 3), LocalDate.of(2023, 4, 9), ChronoUnit.WEEKS);
    assertLabels("This Week: ", "03.04.2023 - 09.04.2023");
  }

  @Test
  void back_PeriodIsMonth_DisplaysLastMonth() {
    sut.setPeriodOfChoiceValue(ChronoUnit.MONTHS);

    sut.back();

    assertValues(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 31), ChronoUnit.MONTHS);
    assertLabels("This Month: ", "01.03.2023 - 31.03.2023");
  }

  @Test
  void forward_PeriodIsDay_DisplaysTomorrow() {
    sut.setPeriodOfChoiceValue(ChronoUnit.DAYS);

    sut.forward();

    assertValues(LocalDate.of(2023, 4, 11), LocalDate.of(2023, 4, 11), ChronoUnit.DAYS);
    assertLabels("This Day: ", "11.04.2023");
  }

  @Test
  void forward_PeriodIsWeek_DisplaysNextWeek() {
    sut.forward();

    assertValues(LocalDate.of(2023, 4, 17), LocalDate.of(2023, 4, 23), ChronoUnit.WEEKS);
    assertLabels("This Week: ", "17.04.2023 - 23.04.2023");
  }

  @Test
  void forward_PeriodIsMonth_DisplaysNextMonth() {
    sut.setPeriodOfChoiceValue(ChronoUnit.MONTHS);

    sut.forward();

    assertValues(LocalDate.of(2023, 5, 1), LocalDate.of(2023, 5, 31), ChronoUnit.MONTHS);
    assertLabels("This Month: ", "01.05.2023 - 31.05.2023");
  }

  private void assertValues(LocalDate from, LocalDate to, ChronoUnit period) {
    assertEquals(from, sut.getFrom());
    assertEquals(to, sut.getTo());
    assertEquals(period, sut.getPeriodOfChoiceValue());
  }

  private void assertLabels(String periodOf, String periodFromTo) {
    assertEquals(periodOf, sut.getPeriodOfLabelText());
    assertEquals(periodFromTo, sut.getPeriodFromToLabelText());
  }
}
