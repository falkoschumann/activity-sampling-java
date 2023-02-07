/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ActivitiesServiceTests {
  private ActivitiesRepositoryStub activitiesRepository;
  private ActivitiesServiceImpl sut;

  @BeforeEach
  void init() {
    activitiesRepository = new ActivitiesRepositoryStub();
    sut = new ActivitiesServiceImpl(activitiesRepository);
  }

  private void setClock(String timestamp) {
    sut.setClock(Clock.fixed(Instant.parse(timestamp), ZoneId.of("Europe/Berlin")));
  }

  @Test
  void logActivity_RecordsActivityLogged() {
    setClock("2022-11-16T11:26:00Z");

    sut.logActivity("Lorem ipsum", Duration.ofMinutes(20));

    assertEquals(
        List.of(
            new Activity(
                LocalDateTime.parse("2022-11-16T12:26:00"), Duration.ofMinutes(20), "Lorem ipsum")),
        activitiesRepository);
  }

  @Test
  void logActivity_TrimsDescription() {
    setClock("2022-11-16T11:26:00Z");

    sut.logActivity("  Lorem ipsum ", Duration.ofMinutes(30));

    assertEquals(
        List.of(
            new Activity(
                LocalDateTime.parse("2022-11-16T12:26:00"), Duration.ofMinutes(30), "Lorem ipsum")),
        activitiesRepository);
  }

  @Test
  void getRecentActivities_MonthWith30Days_ReturnsLast31DaysInDescendentOrder() {
    setClock("2022-09-30T10:00:00Z");
    activitiesRepository.addAll(
        List.of(
            // Last month
            new Activity(LocalDateTime.parse("2022-08-31T17:00:00"), Duration.ofMinutes(20), "A2"),
            // First day of this month
            new Activity(LocalDateTime.parse("2022-09-01T16:00:00"), Duration.ofMinutes(20), "A3"),
            // This Month
            new Activity(LocalDateTime.parse("2022-09-05T15:00:00"), Duration.ofMinutes(20), "A4"),
            // This Week
            new Activity(LocalDateTime.parse("2022-09-26T14:00:00"), Duration.ofMinutes(20), "A5"),
            new Activity(LocalDateTime.parse("2022-09-28T13:00:00"), Duration.ofMinutes(20), "A6"),
            // Yesterday
            new Activity(LocalDateTime.parse("2022-09-29T11:00:00"), Duration.ofMinutes(20), "A7"),
            new Activity(LocalDateTime.parse("2022-09-29T12:00:00"), Duration.ofMinutes(20), "A8"),
            // Today
            new Activity(LocalDateTime.parse("2022-09-30T09:00:00"), Duration.ofMinutes(20), "A9"),
            new Activity(
                LocalDateTime.parse("2022-09-30T10:00:00"), Duration.ofMinutes(20), "A10")));

    var activities = sut.getRecentActivities();

    assertAll(
        "Recent activities",
        () ->
            assertEquals(
                new WorkingDays(
                    List.of(
                        new WorkingDay(
                            LocalDate.parse("2022-09-30"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-09-30T10:00"),
                                    Duration.ofMinutes(20),
                                    "A10"),
                                new Activity(
                                    LocalDateTime.parse("2022-09-30T09:00"),
                                    Duration.ofMinutes(20),
                                    "A9"))),
                        new WorkingDay(
                            LocalDate.parse("2022-09-29"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-09-29T12:00"),
                                    Duration.ofMinutes(20),
                                    "A8"),
                                new Activity(
                                    LocalDateTime.parse("2022-09-29T11:00"),
                                    Duration.ofMinutes(20),
                                    "A7"))),
                        new WorkingDay(
                            LocalDate.parse("2022-09-28"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-09-28T13:00"),
                                    Duration.ofMinutes(20),
                                    "A6"))),
                        new WorkingDay(
                            LocalDate.parse("2022-09-26"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-09-26T14:00"),
                                    Duration.ofMinutes(20),
                                    "A5"))),
                        new WorkingDay(
                            LocalDate.parse("2022-09-05"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-09-05T15:00"),
                                    Duration.ofMinutes(20),
                                    "A4"))),
                        new WorkingDay(
                            LocalDate.parse("2022-09-01"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-09-01T16:00"),
                                    Duration.ofMinutes(20),
                                    "A3"))),
                        new WorkingDay(
                            LocalDate.parse("2022-08-31"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-08-31T17:00"),
                                    Duration.ofMinutes(20),
                                    "A2"))))),
                activities.workingDays(),
                "Working days"),
        () ->
            assertEquals(
                new TimeSummary(
                    LocalDate.of(2022, 9, 30),
                    Duration.ofMinutes(40),
                    Duration.ofMinutes(40),
                    Duration.ofMinutes(120),
                    Duration.ofMinutes(160)),
                activities.timeSummary(),
                "Time summary"));
  }

  @Test
  void getRecentActivities_MonthWith31Days_ReturnsLast31DaysInDescendentOrder() {
    setClock("2022-12-31T10:00:00Z");
    activitiesRepository.addAll(
        List.of(
            // First day of this month
            new Activity(LocalDateTime.parse("2022-12-01T15:00:00"), Duration.ofMinutes(20), "A2"),
            // This Month
            new Activity(LocalDateTime.parse("2022-12-05T14:00:00"), Duration.ofMinutes(20), "A3"),
            // This Week
            new Activity(LocalDateTime.parse("2022-12-26T13:00:00"), Duration.ofMinutes(20), "A4"),
            new Activity(LocalDateTime.parse("2022-12-28T12:00:00"), Duration.ofMinutes(20), "A5"),
            // Yesterday
            new Activity(LocalDateTime.parse("2022-12-30T10:00:00"), Duration.ofMinutes(20), "A6"),
            new Activity(LocalDateTime.parse("2022-12-30T11:00:00"), Duration.ofMinutes(20), "A7"),
            // Today
            new Activity(LocalDateTime.parse("2022-12-31T08:00:00"), Duration.ofMinutes(20), "A8"),
            new Activity(
                LocalDateTime.parse("2022-12-31T09:00:00"), Duration.ofMinutes(20), "A9")));

    var activities = sut.getRecentActivities();

    assertAll(
        "Recent activities",
        () ->
            assertEquals(
                new WorkingDays(
                    List.of(
                        new WorkingDay(
                            LocalDate.parse("2022-12-31"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-12-31T09:00"),
                                    Duration.ofMinutes(20),
                                    "A9"),
                                new Activity(
                                    LocalDateTime.parse("2022-12-31T08:00"),
                                    Duration.ofMinutes(20),
                                    "A8"))),
                        new WorkingDay(
                            LocalDate.parse("2022-12-30"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-12-30T11:00"),
                                    Duration.ofMinutes(20),
                                    "A7"),
                                new Activity(
                                    LocalDateTime.parse("2022-12-30T10:00"),
                                    Duration.ofMinutes(20),
                                    "A6"))),
                        new WorkingDay(
                            LocalDate.parse("2022-12-28"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-12-28T12:00"),
                                    Duration.ofMinutes(20),
                                    "A5"))),
                        new WorkingDay(
                            LocalDate.parse("2022-12-26"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-12-26T13:00"),
                                    Duration.ofMinutes(20),
                                    "A4"))),
                        new WorkingDay(
                            LocalDate.parse("2022-12-05"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-12-05T14:00"),
                                    Duration.ofMinutes(20),
                                    "A3"))),
                        new WorkingDay(
                            LocalDate.parse("2022-12-01"),
                            List.of(
                                new Activity(
                                    LocalDateTime.parse("2022-12-01T15:00"),
                                    Duration.ofMinutes(20),
                                    "A2"))))),
                activities.workingDays(),
                "Working days"),
        () ->
            assertEquals(
                new TimeSummary(
                    LocalDate.of(2022, 12, 31),
                    Duration.ofMinutes(40),
                    Duration.ofMinutes(40),
                    Duration.ofMinutes(120),
                    Duration.ofMinutes(160)),
                activities.timeSummary(),
                "Time summary"));
  }

  @Test
  void createTimesheet() {
    activitiesRepository.addAll(
        List.of(
            // First day in the interval
            new Activity(LocalDateTime.parse("2022-11-14T15:00:00"), Duration.ofMinutes(20), "A1"),
            // A day in the interval
            new Activity(LocalDateTime.parse("2022-11-15T12:00:00"), Duration.ofMinutes(20), "A2"),
            new Activity(LocalDateTime.parse("2022-11-15T13:00:00"), Duration.ofMinutes(20), "A1"),
            new Activity(LocalDateTime.parse("2022-11-15T14:00:00"), Duration.ofMinutes(20), "A2"),
            // Another day in the interval
            new Activity(LocalDateTime.parse("2022-11-17T09:00:00"), Duration.ofMinutes(20), "A1"),
            new Activity(LocalDateTime.parse("2022-11-17T10:00:00"), Duration.ofMinutes(20), "A1"),
            new Activity(LocalDateTime.parse("2022-11-17T11:00:00"), Duration.ofMinutes(20), "A2"),
            // Last day of interval
            new Activity(
                LocalDateTime.parse("2022-11-18T08:00:00"), Duration.ofMinutes(20), "A2")));

    var timesheet =
        sut.createTimesheet(LocalDate.parse("2022-11-14"), LocalDate.parse("2022-11-18"));

    assertAll(
        "Timesheet",
        () ->
            assertEquals(
                List.of(
                    new TimesheetEntry(LocalDate.parse("2022-11-14"), "A1", Duration.ofMinutes(20)),
                    new TimesheetEntry(LocalDate.parse("2022-11-15"), "A1", Duration.ofMinutes(20)),
                    new TimesheetEntry(LocalDate.parse("2022-11-15"), "A2", Duration.ofMinutes(40)),
                    new TimesheetEntry(LocalDate.parse("2022-11-17"), "A1", Duration.ofMinutes(40)),
                    new TimesheetEntry(LocalDate.parse("2022-11-17"), "A2", Duration.ofMinutes(20)),
                    new TimesheetEntry(
                        LocalDate.parse("2022-11-18"), "A2", Duration.ofMinutes(20))),
                timesheet.entries(),
                "entries"),
        () -> assertEquals(Duration.ofMinutes(160), timesheet.total(), "total"));
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
