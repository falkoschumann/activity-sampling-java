/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.TimeReport;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class TimeReportTests {

  @Test
  void from_NoActivities_CreatesEmptyReport() {
    var report = TimeReport.from(List.of());

    assertEquals(new TimeReport(List.of()), report);
    assertEquals(Duration.ZERO, report.total());
  }

  @Test
  void from_OneActivity_CreatesReportWithOneEntry() {
    var today = LocalDate.now();
    var a = newActivity(today, "c");

    var report = TimeReport.from(List.of(a));

    assertEquals(new TimeReport(List.of(newReportEntry("c"))), report);
    assertEquals(Duration.ofMinutes(15), report.total());
  }

  @Test
  void from_RepetitiveActivity_CreatesReportWithOneSummarizedEntry() {
    var today = LocalDate.now();
    var a1 = newActivity(today, "c");
    var a2 = newActivity(today, "c");

    var report = TimeReport.from(List.of(a1, a2));

    assertEquals(
        new TimeReport(
            List.of(TimeReport.Entry.builder().client("c").hours(Duration.ofMinutes(30)).build())),
        report);
    assertEquals(Duration.ofMinutes(30), report.total());
  }

  @Test
  void from_MultipleActivities_CreatesReportWithEntriesOrderedByClient() {
    var today = LocalDate.now();
    var yesterday = today.minusDays(1);
    var y1 = newActivity(yesterday, "c2");
    var t1 = newActivity(today, "c1");

    var report = TimeReport.from(List.of(y1, t1));

    assertEquals(new TimeReport(List.of(newReportEntry("c1"), newReportEntry("c2"))), report);
    assertEquals(Duration.ofMinutes(30), report.total());
  }

  private static Activity newActivity(LocalDate date, String client) {
    return Activity.builder()
        .timestamp(LocalDateTime.of(date, LocalTime.of(16, 0)))
        .duration(Duration.ofMinutes(15))
        .client(client)
        .project("p")
        .task("t")
        .build();
  }

  private static TimeReport.Entry newReportEntry(String client) {
    return TimeReport.Entry.builder().client(client).hours(Duration.ofMinutes(15)).build();
  }
}
