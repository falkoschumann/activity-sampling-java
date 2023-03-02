/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ActivitiesServiceTests {
  private FakeActivitiesRepository activitiesRepository;
  private ActivitiesServiceImpl sut;

  @BeforeEach
  void init() {
    activitiesRepository = new FakeActivitiesRepository();
    sut = new ActivitiesServiceImpl(activitiesRepository);
  }

  @Test
  void logActivity_RecordsActivityLogged() {
    sut.logActivity(
        LocalDateTime.parse("2022-11-16T12:26:00"), Duration.ofMinutes(20), "Lorem ipsum");

    assertEquals(
        List.of(Activity.parse("2022-11-16T12:26:00", "PT20M", "Lorem ipsum")),
        activitiesRepository);
  }

  @Test
  void logActivity_TrimsDescription() {
    sut.logActivity(
        LocalDateTime.parse("2022-11-16T12:26:00"), Duration.ofMinutes(30), "  Lorem ipsum ");

    assertEquals(
        List.of(Activity.parse("2022-11-16T12:26:00", "PT30M", "Lorem ipsum")),
        activitiesRepository);
  }

  @Test
  void getRecentActivities_MonthWith30Days_ReturnsLast31DaysInDescendentOrder() {
    activitiesRepository.addAll(
        List.of(
            // Last month
            Activity.parse("2022-08-31T17:00:00", "PT20M", "A2"),
            // First day of this month
            Activity.parse("2022-09-01T16:00:00", "PT20M", "A3"),
            // This Month
            Activity.parse("2022-09-05T15:00:00", "PT20M", "A4"),
            // This Week
            Activity.parse("2022-09-26T14:00:00", "PT20M", "A5"),
            Activity.parse("2022-09-28T13:00:00", "PT20M", "A6"),
            // Yesterday
            Activity.parse("2022-09-29T11:00:00", "PT20M", "A7"),
            Activity.parse("2022-09-29T12:00:00", "PT20M", "A8"),
            // Today
            Activity.parse("2022-09-30T09:00:00", "PT20M", "A9"),
            Activity.parse("2022-09-30T10:00:00", "PT20M", "A10")));

    var recentActivities =
        sut.getRecentActivities(LocalDate.parse("2022-09-30"), Period.ofDays(30));

    assertEquals(
        new RecentActivities(
            List.of(
                new WorkingDay(
                    LocalDate.parse("2022-09-30"),
                    List.of(
                        Activity.parse("2022-09-30T10:00", "PT20M", "A10"),
                        Activity.parse("2022-09-30T09:00", "PT20M", "A9"))),
                new WorkingDay(
                    LocalDate.parse("2022-09-29"),
                    List.of(
                        Activity.parse("2022-09-29T12:00", "PT20M", "A8"),
                        Activity.parse("2022-09-29T11:00", "PT20M", "A7"))),
                new WorkingDay(
                    LocalDate.parse("2022-09-28"),
                    List.of(Activity.parse("2022-09-28T13:00", "PT20M", "A6"))),
                new WorkingDay(
                    LocalDate.parse("2022-09-26"),
                    List.of(Activity.parse("2022-09-26T14:00", "PT20M", "A5"))),
                new WorkingDay(
                    LocalDate.parse("2022-09-05"),
                    List.of(Activity.parse("2022-09-05T15:00", "PT20M", "A4"))),
                new WorkingDay(
                    LocalDate.parse("2022-09-01"),
                    List.of(Activity.parse("2022-09-01T16:00", "PT20M", "A3"))),
                new WorkingDay(
                    LocalDate.parse("2022-08-31"),
                    List.of(Activity.parse("2022-08-31T17:00", "PT20M", "A2")))),
            TimeSummary.parse("PT40M", "PT40M", "PT120M", "PT160M")),
        recentActivities);
  }

  @Test
  void getRecentActivities_MonthWith31Days_ReturnsLast31DaysInDescendentOrder() {
    activitiesRepository.addAll(
        List.of(
            // First day of this month
            Activity.parse("2022-12-01T15:00:00", "PT20M", "A2"),
            // This Month
            Activity.parse("2022-12-05T14:00:00", "PT20M", "A3"),
            // This Week
            Activity.parse("2022-12-26T13:00:00", "PT20M", "A4"),
            Activity.parse("2022-12-28T12:00:00", "PT20M", "A5"),
            // Yesterday
            Activity.parse("2022-12-30T10:00:00", "PT20M", "A6"),
            Activity.parse("2022-12-30T11:00:00", "PT20M", "A7"),
            // Today
            Activity.parse("2022-12-31T08:00:00", "PT20M", "A8"),
            Activity.parse("2022-12-31T09:00:00", "PT20M", "A9")));

    var recentActivities =
        sut.getRecentActivities(LocalDate.parse("2022-12-31"), Period.ofDays(30));

    assertEquals(
        new RecentActivities(
            List.of(
                new WorkingDay(
                    LocalDate.parse("2022-12-31"),
                    List.of(
                        Activity.parse("2022-12-31T09:00", "PT20M", "A9"),
                        Activity.parse("2022-12-31T08:00", "PT20M", "A8"))),
                new WorkingDay(
                    LocalDate.parse("2022-12-30"),
                    List.of(
                        Activity.parse("2022-12-30T11:00", "PT20M", "A7"),
                        Activity.parse("2022-12-30T10:00", "PT20M", "A6"))),
                new WorkingDay(
                    LocalDate.parse("2022-12-28"),
                    List.of(Activity.parse("2022-12-28T12:00", "PT20M", "A5"))),
                new WorkingDay(
                    LocalDate.parse("2022-12-26"),
                    List.of(Activity.parse("2022-12-26T13:00", "PT20M", "A4"))),
                new WorkingDay(
                    LocalDate.parse("2022-12-05"),
                    List.of(Activity.parse("2022-12-05T14:00", "PT20M", "A3"))),
                new WorkingDay(
                    LocalDate.parse("2022-12-01"),
                    List.of(Activity.parse("2022-12-01T15:00", "PT20M", "A2")))),
            TimeSummary.parse("PT40M", "PT40M", "PT120M", "PT160M")),
        recentActivities);
  }

  @Test
  void getTimesheet() {
    activitiesRepository.addAll(
        List.of(
            // First day in the interval
            Activity.parse("2022-11-14T15:00:00", "PT20M", "A1"),
            // A day in the interval
            Activity.parse("2022-11-15T13:00:00", "PT20M", "A1"),
            Activity.parse("2022-11-15T12:00:00", "PT20M", "A2"),
            Activity.parse("2022-11-15T14:00:00", "PT20M", "A2"),
            // Another day in the interval
            Activity.parse("2022-11-17T09:00:00", "PT20M", "A1"),
            Activity.parse("2022-11-17T10:00:00", "PT20M", "A1"),
            Activity.parse("2022-11-17T11:00:00", "PT20M", "A2"),
            // Last day of interval
            Activity.parse("2022-11-18T08:00:00", "PT20M", "A2")));

    var timesheet = sut.getTimesheet(LocalDate.parse("2022-11-14"), LocalDate.parse("2022-11-18"));

    assertEquals(
        new Timesheet(
            List.of(
                TimesheetEntry.parse("2022-11-14", "A1", "PT20M"),
                TimesheetEntry.parse("2022-11-15", "A1", "PT20M"),
                TimesheetEntry.parse("2022-11-15", "A2", "PT40M"),
                TimesheetEntry.parse("2022-11-17", "A1", "PT40M"),
                TimesheetEntry.parse("2022-11-17", "A2", "PT20M"),
                TimesheetEntry.parse("2022-11-18", "A2", "PT20M")),
            Duration.ofMinutes(160)),
        timesheet);
  }

  @Test
  @Disabled
  void createDetailedTimesheet() {
    fail();
    /*
    when(activities.findInPeriod(LocalDate.parse("2022-11-29"), LocalDate.parse("2022-11-29")))
        .thenReturn(
            List.of(
                new Activity(
                    LocalDateTime.parse("2022-11-29T09:20:00"), Duration.ofMinutes(20), "A1"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T09:40:00"), Duration.ofMinutes(20), "A1"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T10:00:00"), Duration.ofMinutes(20), "A1"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T10:20:00"), Duration.ofMinutes(20), "A1"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T10:30:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T11:00:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T11:20:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T11:40:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T12:00:00"), Duration.ofMinutes(20), "A2"),
                // Break
                new Activity(
                    LocalDateTime.parse("2022-11-29T12:40:00"), Duration.ofMinutes(20), "A1"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T13:00:00"), Duration.ofMinutes(20), "A1"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T13:20:00"), Duration.ofMinutes(20), "A3"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T13:40:00"), Duration.ofMinutes(20), "A3"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T14:00:00"), Duration.ofMinutes(20), "A3"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T14:20:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T14:40:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T15:00:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T15:20:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T15:40:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T16:00:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T16:20:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T16:40:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-29T17:00:00"), Duration.ofMinutes(20), "A2")));

    var timesheet =
        sut.createDetailedTimesheet(LocalDate.parse("2022-11-29"), LocalDate.parse("2022-11-29"));

    assertAll(
        "Detailed timesheet",
        () ->
            assertEquals(
                List.of(
                    // TODO change locale date to locale date time
                    new TimesheetEntry(
                        LocalDate.parse("2022-11-29T09:00"), "A1", Duration.ofMinutes(80)),
                    new TimesheetEntry(
                        LocalDate.parse("2022-11-29T10:20"), "A2", Duration.ofMinutes(100)),
                    // 20 min break
                    new TimesheetEntry(
                        LocalDate.parse("2022-11-29T12:20"), "A1", Duration.ofMinutes(40)),
                    new TimesheetEntry(
                        LocalDate.parse("2022-11-29T13:00"), "A3", Duration.ofMinutes(60)),
                    new TimesheetEntry(
                        LocalDate.parse("2022-11-29T14:00"), "A2", Duration.ofMinutes(180))),
                timesheet.getEntries(),
                "entries"),
        () -> assertEquals(Duration.ofMinutes(160), timesheet.getTotal(), "total"));
     */
  }
}
