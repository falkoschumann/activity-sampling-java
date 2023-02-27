/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.timesheet;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.application.ActivitiesServiceStub;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.ConfigurableResponses;
import de.muspellheim.activitysampling.domain.Timesheet;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimesheetViewModelTests {
  private ActivitiesServiceStub activitiesService;
  private List<String> errors;

  private TimesheetViewModel sut;

  @BeforeEach
  void init() {
    errors = new ArrayList<>();

    var timesheet =
        Timesheet.from(
            List.of(
                new Activity(LocalDateTime.parse("2022-11-20T12:00"), Duration.ofMinutes(10), "A1"),
                new Activity(LocalDateTime.parse("2022-11-20T12:00"), Duration.ofMinutes(5), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-21T12:00"), Duration.ofMinutes(5), "A1")));
    activitiesService = new ActivitiesServiceStub();
    activitiesService.initTimesheet(new ConfigurableResponses<>(timesheet));

    var clock = Clock.fixed(Instant.parse("2022-11-23T20:42:00Z"), ZoneId.systemDefault());
    sut = new TimesheetViewModel(activitiesService, Locale.GERMANY, clock);
    sut.setOnError(e -> errors.addAll(e));
    sut.run();
  }

  @Test
  void create_DisplaysThisWeek() {
    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("21.11.2022 - 27.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"),
        () ->
            assertEquals(
                List.of(
                    new TimesheetItem("20.11.2022", "A1", "00:10"),
                    new TimesheetItem("20.11.2022", "A2", "00:05"),
                    new TimesheetItem("21.11.2022", "A1", "00:05")),
                sut.getTimesheetItems()),
        () -> assertEquals("00:20", sut.totalProperty().get()));
  }

  @Test
  void changePeriod_SetDay_DisplaysToday() {
    sut.periodProperty().set(ChronoUnit.DAYS);

    assertAll(
        () -> assertEquals("This Day: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("21.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.DAYS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void back_PeriodIsDay_DisplaysYesterday() {
    sut.periodProperty().set(ChronoUnit.DAYS);

    sut.back();

    assertAll(
        () -> assertEquals("This Day: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("20.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.DAYS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void forward_PeriodIsDay_DisplaysTomorrow() {
    sut.periodProperty().set(ChronoUnit.DAYS);

    sut.forward();

    assertAll(
        () -> assertEquals("This Day: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("22.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.DAYS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void changePeriod_SetWeek_DisplaysThisWeek() {
    sut.periodProperty().set(ChronoUnit.DAYS);

    sut.periodProperty().set(ChronoUnit.WEEKS);

    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("21.11.2022 - 27.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void back_PeriodIsWeek_DisplaysLastWeek() {
    sut.back();

    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("14.11.2022 - 20.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void forward_PeriodIsWeek_DisplaysNextWeek() {
    sut.forward();

    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("28.11.2022 - 04.12.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void changePeriod_SetMonth_DisplaysThisMonth() {
    sut.periodProperty().set(ChronoUnit.MONTHS);

    assertAll(
        () -> assertEquals("This Month: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("01.11.2022 - 30.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.MONTHS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void back_PeriodIsMonth_DisplaysLastMonth() {
    sut.periodProperty().set(ChronoUnit.MONTHS);

    sut.back();

    assertAll(
        () -> assertEquals("This Month: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("01.10.2022 - 31.10.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.MONTHS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void forward_PeriodIsMonth_DisplaysNextMonth() {
    sut.periodProperty().set(ChronoUnit.MONTHS);

    sut.forward();

    assertAll(
        () -> assertEquals("This Month: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("01.12.2022 - 31.12.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.MONTHS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void load_Failed_NotifyError() {
    activitiesService.initTimesheet(
        new ConfigurableResponses<>(new IllegalStateException("Something went wrong.")));

    sut.back();

    assertEquals(List.of("Failed to load timesheet.", "Something went wrong."), errors);
  }
}
