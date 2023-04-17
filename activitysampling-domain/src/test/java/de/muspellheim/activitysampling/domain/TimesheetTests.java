/*
 * Activity Sampling - Domain
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class TimesheetTests {
  @Test
  void noActivities() {
    var timesheet = Timesheet.of(List.of());

    assertEquals(new Timesheet(List.of(), Duration.ZERO), timesheet);
  }

  @Test
  void firstActivities() {
    var today = LocalDate.now();
    var a = new Activity(LocalDateTime.of(today, LocalTime.of(13, 0)), Duration.ofMinutes(20), "a");

    var timesheet = Timesheet.of(List.of(a));

    assertEquals(
        new Timesheet(
            List.of(new TimesheetEntry(today, "a", Duration.ofMinutes(20))),
            Duration.ofMinutes(20)),
        timesheet);
  }

  @Test
  void repetitiveActivity() {
    var today = LocalDate.now();
    var a1 =
        new Activity(LocalDateTime.of(today, LocalTime.of(13, 0)), Duration.ofMinutes(20), "a");
    var a2 =
        new Activity(LocalDateTime.of(today, LocalTime.of(14, 0)), Duration.ofMinutes(30), "a");

    var timesheet = Timesheet.of(List.of(a1, a2));

    assertEquals(
        new Timesheet(
            List.of(new TimesheetEntry(today, "a", Duration.ofMinutes(50))),
            Duration.ofMinutes(50)),
        timesheet);
  }

  @Test
  void multipleActivities_OrderdByDateAndNotes() {
    var today = LocalDate.now();
    var yesterday = today.minusDays(1);
    var a1 =
        new Activity(LocalDateTime.of(yesterday, LocalTime.of(13, 0)), Duration.ofMinutes(30), "a");
    var b1 =
        new Activity(LocalDateTime.of(today, LocalTime.of(12, 0)), Duration.ofMinutes(30), "b");
    var c1 =
        new Activity(LocalDateTime.of(today, LocalTime.of(13, 0)), Duration.ofMinutes(30), "c");
    var a2 =
        new Activity(LocalDateTime.of(today, LocalTime.of(14, 0)), Duration.ofMinutes(30), "a");

    var timesheet = Timesheet.of(List.of(a1, b1, c1, a2));

    assertEquals(
        new Timesheet(
            List.of(
                new TimesheetEntry(yesterday, "a", Duration.ofMinutes(30)),
                new TimesheetEntry(today, "a", Duration.ofMinutes(30)),
                new TimesheetEntry(today, "b", Duration.ofMinutes(30)),
                new TimesheetEntry(today, "c", Duration.ofMinutes(30))),
            Duration.ofMinutes(120)),
        timesheet);
  }
}
