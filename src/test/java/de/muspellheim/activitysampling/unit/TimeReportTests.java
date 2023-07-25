/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.TimeReport;
import java.time.Duration;
import java.time.LocalDateTime;
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
    var a = newActivity("c", "p", "t");

    var report = TimeReport.from(List.of(a));

    assertEquals(new TimeReport(List.of(newReportEntry("c", "p", "t"))), report);
    assertEquals(Duration.ofMinutes(15), report.total());
  }

  @Test
  void from_RepetitiveActivity_CreatesReportWithOneSummarizedEntry() {
    var a1 = newActivity("c", "p", "t");
    var a2 = newActivity("c", "p", "t");

    var report = TimeReport.from(List.of(a1, a2));

    assertEquals(
        new TimeReport(
            List.of(
                TimeReport.Entry.builder()
                    .client("c")
                    .project("p")
                    .task("t")
                    .hours(Duration.ofMinutes(30))
                    .build())),
        report);
    assertEquals(Duration.ofMinutes(30), report.total());
  }

  @Test
  void from_MultipleActivities_CreatesReportWithEntriesOrderedByClientProjectAndTask() {
    var a1 = newActivity("c2", "p2", "t2");
    var a2 = newActivity("c1", "p2", "t2");
    var a3 = newActivity("c1", "p1", "t2");
    var a4 = newActivity("c1", "p1", "t1");

    var report = TimeReport.from(List.of(a1, a2, a3, a4));

    assertEquals(
        new TimeReport(
            List.of(
                newReportEntry("c1", "p1", "t1"),
                newReportEntry("c1", "p1", "t2"),
                newReportEntry("c1", "p2", "t2"),
                newReportEntry("c2", "p2", "t2"))),
        report);
    assertEquals(Duration.ofMinutes(60), report.total());
  }

  @Test
  void groupByClient() {
    var a1 = newActivity("c2", "p2", "t2");
    var a2 = newActivity("c1", "p2", "t2");
    var a3 = newActivity("c1", "p1", "t1");
    var timeReport = TimeReport.from(List.of(a1, a2, a3));

    var report = timeReport.groupByClient();

    assertEquals(
        new TimeReport(
            List.of(
                newReportEntry("c1", Duration.ofMinutes(30)),
                newReportEntry("c2", Duration.ofMinutes(15)))),
        report);
    assertEquals(Duration.ofMinutes(45), report.total());
  }

  @Test
  void groupByProject() {
    var a1 = newActivity("c2", "p2", "t2");
    var a2 = newActivity("c1", "p1", "t2");
    var a3 = newActivity("c1", "p1", "t1");
    var timeReport = TimeReport.from(List.of(a1, a2, a3));

    var report = timeReport.groupByProject();

    assertEquals(
        new TimeReport(
            List.of(
                newReportEntry("c1", "p1", Duration.ofMinutes(30)),
                newReportEntry("c2", "p2", Duration.ofMinutes(15)))),
        report);
    assertEquals(Duration.ofMinutes(45), report.total());
  }

  private static Activity newActivity(String client, String project, String task) {
    return Activity.builder()
        .timestamp(LocalDateTime.now())
        .duration(Duration.ofMinutes(15))
        .client(client)
        .project(project)
        .task(task)
        .build();
  }

  private static TimeReport.Entry newReportEntry(String client, String project, String task) {
    return TimeReport.Entry.builder()
        .client(client)
        .project(project)
        .task(task)
        .hours(Duration.ofMinutes(15))
        .build();
  }

  private static TimeReport.Entry newReportEntry(String client, String project, Duration hours) {
    return TimeReport.Entry.builder()
        .client(client)
        .project(project)
        .task("N/A")
        .hours(hours)
        .build();
  }

  private static TimeReport.Entry newReportEntry(String client, Duration hours) {
    return TimeReport.Entry.builder()
        .client(client)
        .project("N/A")
        .task("N/A")
        .hours(hours)
        .build();
  }
}
