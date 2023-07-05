/*
 * Activity Sampling
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
  void of_NoActivities_CreatesEmptyTimesheet() {
    var timesheet = Timesheet.of(List.of());

    assertEquals(new Timesheet(List.of(), Duration.ZERO), timesheet);
  }

  @Test
  void of_OneActivity_CreatesTimesheetWithOneEntry() {
    var today = LocalDate.now();
    var a = newActivity(today, "c", "p", "n");

    var timesheet = Timesheet.of(List.of(a));

    assertEquals(
        new Timesheet(
            List.of(new Timesheet.Entry(today, "c", "p", "n", Duration.ofMinutes(30))),
            Duration.ofMinutes(30)),
        timesheet);
  }

  @Test
  void of_RepetitiveActivity_CreatesTimesheetWithOneSummarizedEntry() {
    var today = LocalDate.now();
    var a1 = newActivity(today, "c", "p", "n");
    var a2 = newActivity(today, "c", "p", "n");

    var timesheet = Timesheet.of(List.of(a1, a2));

    assertEquals(
        new Timesheet(
            List.of(new Timesheet.Entry(today, "c", "p", "n", Duration.ofMinutes(60))),
            Duration.ofMinutes(60)),
        timesheet);
  }

  @Test
  void of_MultipleActivities_CreatesTimesheetWithEntriesOrderedByDateClientProjectAndNotes() {
    var today = LocalDate.now();
    var yesterday = today.minusDays(1);
    var y1 = newActivity(yesterday, "c2", "p2", "a2");
    var t1 = newActivity(today, "c2", "p2", "a2");
    var t2 = newActivity(today, "c2", "p1", "a1");
    var t3 = newActivity(today, "c1", "p1", "a1");
    var t4 = newActivity(today, "c2", "p2", "a1");

    var timesheet = Timesheet.of(List.of(y1, t1, t2, t3, t4));

    assertEquals(
        new Timesheet(
            List.of(
                new Timesheet.Entry(yesterday, "c2", "p2", "a2", Duration.ofMinutes(30)),
                new Timesheet.Entry(today, "c1", "p1", "a1", Duration.ofMinutes(30)),
                new Timesheet.Entry(today, "c2", "p1", "a1", Duration.ofMinutes(30)),
                new Timesheet.Entry(today, "c2", "p2", "a1", Duration.ofMinutes(30)),
                new Timesheet.Entry(today, "c2", "p2", "a2", Duration.ofMinutes(30))),
            Duration.ofMinutes(150)),
        timesheet);
  }

  private static Activity newActivity(
      LocalDate date, String client, String project, String activity) {
    return new Activity(
        LocalDateTime.of(date, LocalTime.of(13, 0)),
        Duration.ofMinutes(30),
        client,
        project,
        activity);
  }
}
