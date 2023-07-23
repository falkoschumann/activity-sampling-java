/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.Timesheet;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class TimesheetTests {

  @Test
  void from_NoActivities_CreatesEmptyTimesheet() {
    var timesheet = Timesheet.from(List.of());

    assertEquals(new Timesheet(List.of()), timesheet);
    assertEquals(Duration.ZERO, timesheet.total());
  }

  @Test
  void from_OneActivity_CreatesTimesheetWithOneEntry() {
    var today = LocalDate.now();
    var a = newActivity(today, "c", "p", "t");

    var timesheet = Timesheet.from(List.of(a));

    assertEquals(
        new Timesheet(
            List.of(
                Timesheet.Entry.builder()
                    .date(today)
                    .client("c")
                    .project("p")
                    .task("t")
                    .hours(Duration.ofMinutes(30))
                    .build())),
        timesheet);
    assertEquals(Duration.ofMinutes(30), timesheet.total());
  }

  @Test
  void from_RepetitiveActivity_CreatesTimesheetWithOneSummarizedEntry() {
    var today = LocalDate.now();
    var a1 = newActivity(today, "c", "p", "t");
    var a2 = newActivity(today, "c", "p", "t");

    var timesheet = Timesheet.from(List.of(a1, a2));

    assertEquals(
        new Timesheet(
            List.of(
                Timesheet.Entry.builder()
                    .date(today)
                    .client("c")
                    .project("p")
                    .task("t")
                    .hours(Duration.ofMinutes(60))
                    .build())),
        timesheet);
    assertEquals(Duration.ofMinutes(60), timesheet.total());
  }

  @Test
  void from_MultipleActivities_CreatesTimesheetWithEntriesOrderedByDateClientProjectAndTask() {
    var today = LocalDate.now();
    var yesterday = today.minusDays(1);
    var y1 = newActivity(yesterday, "c2", "p2", "t2");
    var t1 = newActivity(today, "c2", "p2", "t2");
    var t2 = newActivity(today, "c2", "p1", "t1");
    var t3 = newActivity(today, "c1", "p1", "t1");
    var t4 = newActivity(today, "c2", "p2", "t1");

    var timesheet = Timesheet.from(List.of(y1, t1, t2, t3, t4));

    assertEquals(
        new Timesheet(
            List.of(
                newTimesheetEntry(yesterday, "c2", "p2", "t2"),
                newTimesheetEntry(today, "c1", "p1", "t1"),
                newTimesheetEntry(today, "c2", "p1", "t1"),
                newTimesheetEntry(today, "c2", "p2", "t1"),
                newTimesheetEntry(today, "c2", "p2", "t2"))),
        timesheet);
    assertEquals(Duration.ofMinutes(150), timesheet.total());
  }

  private static Activity newActivity(LocalDate date, String client, String project, String task) {
    return Activity.builder()
        .timestamp(LocalDateTime.of(date, LocalTime.of(13, 0)))
        .duration(Duration.ofMinutes(30))
        .client(client)
        .project(project)
        .task(task)
        .build();
  }

  private static Timesheet.Entry newTimesheetEntry(
      LocalDate date, String client, String project, String task) {
    return Timesheet.Entry.builder()
        .date(date)
        .client(client)
        .project(project)
        .task(task)
        .hours(Duration.ofMinutes(30))
        .build();
  }
}
