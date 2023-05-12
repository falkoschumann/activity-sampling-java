/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.timesheet;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.muspellheim.activitysampling.application.ActivitiesServiceStub;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.common.util.ConfigurableResponses;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimesheetViewModelTests {
  private ActivitiesServiceStub activitiesService;
  private TimesheetViewModel sut;
  private List<String> errors;

  @BeforeEach
  void init() {
    activitiesService = new ActivitiesServiceStub();
    activitiesService.initTimesheetResponses(ConfigurableResponses.always(newTimesheet()));
    var clock = Clock.fixed(Instant.parse("2023-04-16T14:05:00Z"), ZoneId.systemDefault());
    sut = new TimesheetViewModel(activitiesService, Locale.GERMANY, clock);
    errors = new ArrayList<>();
    sut.addOnErrorListener(errors::addAll);
  }

  private static Timesheet newTimesheet() {
    return new Timesheet(
        List.of(newTimesheetEntry(LocalDate.of(2023, 4, 14))), Duration.ofMinutes(20));
  }

  private static Timesheet.Entry newTimesheetEntry(LocalDate date) {
    return new Timesheet.Entry(date, "Lorem ipsum", Duration.ofMinutes(20));
  }

  @Test
  void run_LoadsThisWeek() {
    sut.run();

    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("10.04.2023 - 16.04.2023", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"),
        () ->
            assertEquals(
                List.of(new TimesheetItem("14.04.2023", "Lorem ipsum", "00:20")),
                sut.getTimesheetItems()),
        () -> assertEquals("00:20", sut.totalProperty().get()));
  }

  @Test
  void load_Failed_NotifyErrors() {
    activitiesService.initTimesheetResponses(
        ConfigurableResponses.sequence(
            Timesheet.EMPTY, new IllegalStateException("Something went wrong.")));
    sut.run();

    sut.load();

    assertErrors(List.of("Failed to load timesheet.", "Something went wrong."));
  }

  @Test
  void changePeriod_FromWeekToDay_DisplaysMonday() {
    sut.run();

    sut.periodProperty().set(ChronoUnit.DAYS);

    assertAll(
        () -> assertEquals("This Day: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("10.04.2023", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.DAYS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void changePeriod_FromWeekToMonth_DisplaysThisMonth() {
    sut.run();

    sut.periodProperty().set(ChronoUnit.MONTHS);

    assertAll(
        () -> assertEquals("This Month: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("01.04.2023 - 30.04.2023", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.MONTHS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void changePeriod_FromDayToWeek_DisplaysThisWeek() {
    sut.run();
    sut.periodProperty().set(ChronoUnit.DAYS);

    sut.periodProperty().set(ChronoUnit.WEEKS);

    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("10.04.2023 - 16.04.2023", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void changePeriod_ToUnsupportedPeriod_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> sut.periodProperty().set(ChronoUnit.HOURS));
  }

  @Test
  void back_PeriodIsDay_DisplaysYesterday() {
    sut.run();
    sut.periodProperty().set(ChronoUnit.DAYS);

    sut.back();

    assertAll(
        () -> assertEquals("This Day: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("09.04.2023", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.DAYS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void back_PeriodIsWeek_DisplaysLastWeek() {
    sut.run();

    sut.back();

    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("03.04.2023 - 09.04.2023", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void back_PeriodIsMonth_DisplaysLastMonth() {
    sut.run();
    sut.periodProperty().set(ChronoUnit.MONTHS);

    sut.back();

    assertAll(
        () -> assertEquals("This Month: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("01.03.2023 - 31.03.2023", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.MONTHS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void forward_PeriodIsDay_DisplaysTomorrow() {
    sut.periodProperty().set(ChronoUnit.DAYS);

    sut.forward();

    assertAll(
        () -> assertEquals("This Day: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("11.04.2023", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.DAYS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void forward_PeriodIsWeek_DisplaysNextWeek() {
    sut.forward();

    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("17.04.2023 - 23.04.2023", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void forward_PeriodIsMonth_DisplaysNextMonth() {
    sut.periodProperty().set(ChronoUnit.MONTHS);

    sut.forward();

    assertAll(
        () -> assertEquals("This Month: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("01.05.2023 - 31.05.2023", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.MONTHS, sut.periodProperty().get(), "Period"));
  }

  private void assertErrors(List<String> errors) {
    assertEquals(errors, this.errors, "Errors");
  }
}
