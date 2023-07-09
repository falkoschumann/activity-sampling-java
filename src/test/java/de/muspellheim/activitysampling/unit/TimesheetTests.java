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
  void of_NoActivities_CreatesEmptyTimesheet() {
    var timesheet = Timesheet.of(List.of());

    assertEquals(new Timesheet(List.of()), timesheet);
    assertEquals(Duration.ZERO, timesheet.total());
  }

  @Test
  void of_OneActivity_CreatesTimesheetWithOneEntry() {
    var today = LocalDate.now();
    var a = newActivity(today, "c", "p", "n");

    var timesheet = Timesheet.of(List.of(a));

    assertEquals(
        new Timesheet(
            List.of(
                Timesheet.Entry.builder()
                    .date(today)
                    .client("c")
                    .project("p")
                    .notes("n")
                    .hours(Duration.ofMinutes(30))
                    .build())),
        timesheet);
    assertEquals(Duration.ofMinutes(30), timesheet.total());
  }

  @Test
  void of_RepetitiveActivity_CreatesTimesheetWithOneSummarizedEntry() {
    var today = LocalDate.now();
    var a1 = newActivity(today, "c", "p", "n");
    var a2 = newActivity(today, "c", "p", "n");

    var timesheet = Timesheet.of(List.of(a1, a2));

    assertEquals(
        new Timesheet(
            List.of(
                Timesheet.Entry.builder()
                    .date(today)
                    .client("c")
                    .project("p")
                    .notes("n")
                    .hours(Duration.ofMinutes(60))
                    .build())),
        timesheet);
    assertEquals(Duration.ofMinutes(60), timesheet.total());
  }

  @Test
  void of_MultipleActivities_CreatesTimesheetWithEntriesOrderedByDateClientProjectAndNotes() {
    var today = LocalDate.now();
    var yesterday = today.minusDays(1);
    var y1 = newActivity(yesterday, "c2", "p2", "n2");
    var t1 = newActivity(today, "c2", "p2", "n2");
    var t2 = newActivity(today, "c2", "p1", "n1");
    var t3 = newActivity(today, "c1", "p1", "n1");
    var t4 = newActivity(today, "c2", "p2", "n1");

    var timesheet = Timesheet.of(List.of(y1, t1, t2, t3, t4));

    assertEquals(
        new Timesheet(
            List.of(
                new Timesheet.Entry(yesterday, "c2", "p2", "n2", Duration.ofMinutes(30)),
                new Timesheet.Entry(today, "c1", "p1", "n1", Duration.ofMinutes(30)),
                new Timesheet.Entry(today, "c2", "p1", "n1", Duration.ofMinutes(30)),
                new Timesheet.Entry(today, "c2", "p2", "n1", Duration.ofMinutes(30)),
                new Timesheet.Entry(today, "c2", "p2", "n2", Duration.ofMinutes(30)))),
        timesheet);
    assertEquals(Duration.ofMinutes(150), timesheet.total());
  }

  private static Activity newActivity(LocalDate date, String client, String project, String notes) {
    return new Activity(
        LocalDateTime.of(date, LocalTime.of(13, 0)),
        Duration.ofMinutes(30),
        client,
        project,
        notes);
  }
}
