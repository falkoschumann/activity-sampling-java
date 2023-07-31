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

  // TODO test groupByClient without using from
  // TODO test groupByProject without using from
  // TODO test groupByTask without using from

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
        new TimeReport(List.of(newReportEntry("c", "p", "t", Duration.ofMinutes(30)))), report);
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
  void groupByClient_NoActivities_CreatesEmptyReport() {
    var timeReport = new TimeReport(List.of());

    var report = timeReport.groupByClient();

    assertEquals(new TimeReport(List.of()), report);
    assertEquals(Duration.ZERO, report.total());
  }

  @Test
  void groupByClient_OneActivity_CreatesReportWithOneEntry() {
    var timeReport = new TimeReport(List.of(newReportEntry("c", "p", "t")));

    var report = timeReport.groupByClient();

    assertEquals(new TimeReport(List.of(newReportEntry("c", Duration.ofMinutes(15)))), report);
    assertEquals(Duration.ofMinutes(15), report.total());
  }

  @Test
  void groupByClient_RepetitiveActivity_CreatesReportWithOneSummarizedEntry() {
    var timeReport =
        new TimeReport(List.of(newReportEntry("c", "p", "t"), newReportEntry("c", "p", "t")));

    var report = timeReport.groupByClient();

    assertEquals(new TimeReport(List.of(newReportEntry("c", Duration.ofMinutes(30)))), report);
    assertEquals(Duration.ofMinutes(30), report.total());
  }

  @Test
  void groupByClient_MultipleActivities_CreatesReportWithEntriesOrderedByClientProjectAndTask() {
    var timeReport =
        new TimeReport(
            List.of(
                newReportEntry("c2", "p2", "t2"),
                newReportEntry("c1", "p2", "t2"),
                newReportEntry("c1", "p1", "t2"),
                newReportEntry("c1", "p1", "t1")));

    var report = timeReport.groupByClient();

    assertEquals(
        new TimeReport(
            List.of(
                newReportEntry("c1", Duration.ofMinutes(45)),
                newReportEntry("c2", Duration.ofMinutes(15)))),
        report);
    assertEquals(Duration.ofMinutes(60), report.total());
  }

  @Test
  void groupByProject_NoActivities_CreatesEmptyReport() {
    var timeReport = new TimeReport(List.of());

    var report = timeReport.groupByProject();

    assertEquals(new TimeReport(List.of()), report);
    assertEquals(Duration.ZERO, report.total());
  }

  @Test
  void groupByProject_OneActivity_CreatesReportWithOneEntry() {
    var timeReport = new TimeReport(List.of(newReportEntry("c", "p", "t")));

    var report = timeReport.groupByProject();

    assertEquals(new TimeReport(List.of(newReportEntry("c", "p", Duration.ofMinutes(15)))), report);
    assertEquals(Duration.ofMinutes(15), report.total());
  }

  @Test
  void groupByProject_RepetitiveActivity_CreatesReportWithOneSummarizedEntry() {
    var timeReport =
        new TimeReport(List.of(newReportEntry("c", "p", "t"), newReportEntry("c", "p", "t")));

    var report = timeReport.groupByProject();

    assertEquals(new TimeReport(List.of(newReportEntry("c", "p", Duration.ofMinutes(30)))), report);
    assertEquals(Duration.ofMinutes(30), report.total());
  }

  @Test
  void groupByProject_MultipleActivities_CreatesReportWithEntriesOrderedByClientProjectAndTask() {
    var timeReport =
        new TimeReport(
            List.of(
                newReportEntry("c2", "p2", "t2"),
                newReportEntry("c1", "p2", "t2"),
                newReportEntry("c1", "p1", "t2"),
                newReportEntry("c1", "p1", "t1")));

    var report = timeReport.groupByProject();

    assertEquals(
        new TimeReport(
            List.of(
                newReportEntry("c1", "p1", Duration.ofMinutes(30)),
                newReportEntry("c1, c2", "p2", Duration.ofMinutes(30)))),
        report);
    assertEquals(Duration.ofMinutes(60), report.total());
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

  @Test
  void groupByTask_NoActivities_CreatesEmptyReport() {
    var timeReport = new TimeReport(List.of());

    var report = timeReport.groupByTask();

    assertEquals(new TimeReport(List.of()), report);
    assertEquals(Duration.ZERO, report.total());
  }

  @Test
  void groupByTask_OneActivity_CreatesReportWithOneEntry() {
    var timeReport = new TimeReport(List.of(newReportEntry("c", "p", "t")));

    var report = timeReport.groupByTask();

    assertEquals(new TimeReport(List.of(newReportEntry("N/A", "N/A", "t"))), report);
    assertEquals(Duration.ofMinutes(15), report.total());
  }

  @Test
  void groupByTask_RepetitiveActivity_CreatesReportWithOneSummarizedEntry() {
    var timeReport =
        new TimeReport(List.of(newReportEntry("c", "p", "t"), newReportEntry("c", "p", "t")));

    var report = timeReport.groupByTask();

    assertEquals(
        new TimeReport(List.of(newReportEntry("N/A", "N/A", "t", Duration.ofMinutes(30)))), report);
    assertEquals(Duration.ofMinutes(30), report.total());
  }

  @Test
  void groupByTask_MultipleActivities_CreatesReportWithEntriesOrderedByClientProjectAndTask() {
    var timeReport =
        new TimeReport(
            List.of(
                newReportEntry("c2", "p2", "t2"),
                newReportEntry("c1", "p2", "t2"),
                newReportEntry("c1", "p1", "t2"),
                newReportEntry("c1", "p1", "t1")));

    var report = timeReport.groupByTask();

    assertEquals(
        new TimeReport(
            List.of(
                newReportEntry("N/A", "N/A", "t1", Duration.ofMinutes(15)),
                newReportEntry("N/A", "N/A", "t2", Duration.ofMinutes(45)))),
        report);
    assertEquals(Duration.ofMinutes(60), report.total());
  }

  private static TimeReport.Entry newReportEntry(String client, String project, String task) {
    return newReportEntry(client, project, task, Duration.ofMinutes(15));
  }

  private static TimeReport.Entry newReportEntry(
      String client, String project, String task, Duration hours) {
    return TimeReport.Entry.builder()
        .client(client)
        .project(project)
        .task(task)
        .hours(hours)
        .build();
  }

  private static TimeReport.Entry newReportEntry(String client, String project, Duration hours) {
    return newReportEntry(client, project, "N/A", hours);
  }

  private static TimeReport.Entry newReportEntry(String client, Duration hours) {
    return newReportEntry(client, "N/A", "N/A", hours);
  }
}
