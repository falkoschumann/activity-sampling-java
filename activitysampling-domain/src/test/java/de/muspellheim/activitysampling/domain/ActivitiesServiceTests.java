package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class ActivitiesServiceTests {
  private Activities activities;
  private MutableClock clock;
  private ActivitiesService sut;

  @BeforeEach
  void init() {
    activities = mock(ActivitiesBase.class);
    when(activities.findInPeriod(any(), any())).thenCallRealMethod();
    clock = new MutableClock();
    sut = new ActivitiesServiceImpl(activities, clock);
  }

  @Test
  void logActivity_RecordsActivityLogged() {
    clock.setTimestamp(Instant.parse("2022-11-16T11:26:00Z"));
    sut.logActivity("Lorem ipsum");

    verify(activities)
        .append(
            new Activity(
                LocalDateTime.parse("2022-11-16T12:26:00"), Duration.ofMinutes(20), "Lorem ipsum"));
  }

  @Test
  void logActivity_TrimsDescription() {
    clock.setTimestamp(Instant.parse("2022-11-16T11:26:00Z"));
    sut.logActivity("  Lorem ipsum ");

    verify(activities)
        .append(
            new Activity(
                LocalDateTime.parse("2022-11-16T12:26:00"), Duration.ofMinutes(20), "Lorem ipsum"));
  }

  @Test
  void getRecentActivities_MonthWith30Days_ReturnsLast31DaysInDescendentOrder() {
    clock.setTimestamp(Instant.parse("2022-09-30T10:00:00Z"));
    when(activities.findAll())
        .thenReturn(
            List.of(
                // Last month
                new Activity(
                    LocalDateTime.parse("2022-08-30T18:00:00"), Duration.ofMinutes(20), "A1"),
                new Activity(
                    LocalDateTime.parse("2022-08-31T17:00:00"), Duration.ofMinutes(20), "A2"),
                // First day of this month
                new Activity(
                    LocalDateTime.parse("2022-09-01T16:00:00"), Duration.ofMinutes(20), "A3"),
                // This Month
                new Activity(
                    LocalDateTime.parse("2022-09-05T15:00:00"), Duration.ofMinutes(20), "A4"),
                // This Week
                new Activity(
                    LocalDateTime.parse("2022-09-26T14:00:00"), Duration.ofMinutes(20), "A5"),
                new Activity(
                    LocalDateTime.parse("2022-09-28T13:00:00"), Duration.ofMinutes(20), "A6"),
                // Yesterday
                new Activity(
                    LocalDateTime.parse("2022-09-29T11:00:00"), Duration.ofMinutes(20), "A7"),
                new Activity(
                    LocalDateTime.parse("2022-09-29T12:00:00"), Duration.ofMinutes(20), "A8"),
                // Today
                new Activity(
                    LocalDateTime.parse("2022-09-30T09:00:00"), Duration.ofMinutes(20), "A9"),
                new Activity(
                    LocalDateTime.parse("2022-09-30T10:00:00"), Duration.ofMinutes(20), "A10")));

    var activities = sut.getRecentActivities();

    assertAll(
        "Recent activities",
        () ->
            assertEquals(
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
                                "A2")))),
                activities.getWorkingDays(),
                "Working days"),
        () ->
            assertEquals(
                new TimeSummary(
                    Duration.ofMinutes(40),
                    Duration.ofMinutes(40),
                    Duration.ofMinutes(120),
                    Duration.ofMinutes(160)),
                activities.getTimeSummary(),
                "Time summary"));
  }

  @Test
  void getRecentActivities_MonthWith31Days_ReturnsLast31DaysInDescendentOrder() {
    clock.setTimestamp(Instant.parse("2022-12-31T10:00:00Z"));
    when(activities.findAll())
        .thenReturn(
            List.of(
                // Last month
                new Activity(
                    LocalDateTime.parse("2022-11-30T16:00:00"), Duration.ofMinutes(20), "A1"),
                // First day of this month
                new Activity(
                    LocalDateTime.parse("2022-12-01T15:00:00"), Duration.ofMinutes(20), "A2"),
                // This Month
                new Activity(
                    LocalDateTime.parse("2022-12-05T14:00:00"), Duration.ofMinutes(20), "A3"),
                // This Week
                new Activity(
                    LocalDateTime.parse("2022-12-26T13:00:00"), Duration.ofMinutes(20), "A4"),
                new Activity(
                    LocalDateTime.parse("2022-12-28T12:00:00"), Duration.ofMinutes(20), "A5"),
                // Yesterday
                new Activity(
                    LocalDateTime.parse("2022-12-30T10:00:00"), Duration.ofMinutes(20), "A6"),
                new Activity(
                    LocalDateTime.parse("2022-12-30T11:00:00"), Duration.ofMinutes(20), "A7"),
                // Today
                new Activity(
                    LocalDateTime.parse("2022-12-31T08:00:00"), Duration.ofMinutes(20), "A8"),
                new Activity(
                    LocalDateTime.parse("2022-12-31T09:00:00"), Duration.ofMinutes(20), "A9")));

    var activities = sut.getRecentActivities();

    assertAll(
        "Recent activities",
        () ->
            assertEquals(
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
                                "A2")))),
                activities.getWorkingDays(),
                "Working days"),
        () ->
            assertEquals(
                new TimeSummary(
                    Duration.ofMinutes(40),
                    Duration.ofMinutes(40),
                    Duration.ofMinutes(120),
                    Duration.ofMinutes(160)),
                activities.getTimeSummary(),
                "Time summary"));
  }

  @Test
  void createTimesheet() {
    when(activities.findAll())
        .thenReturn(
            List.of(
                // Last day before interval
                new Activity(
                    LocalDateTime.parse("2022-11-13T16:00:00"), Duration.ofMinutes(20), "A1"),
                // First day in the interval
                new Activity(
                    LocalDateTime.parse("2022-11-14T15:00:00"), Duration.ofMinutes(20), "A1"),
                // A day in the interval
                new Activity(
                    LocalDateTime.parse("2022-11-15T12:00:00"), Duration.ofMinutes(20), "A2"),
                new Activity(
                    LocalDateTime.parse("2022-11-15T13:00:00"), Duration.ofMinutes(20), "A1"),
                new Activity(
                    LocalDateTime.parse("2022-11-15T14:00:00"), Duration.ofMinutes(20), "A2"),
                // Another day in the interval
                new Activity(
                    LocalDateTime.parse("2022-11-17T09:00:00"), Duration.ofMinutes(20), "A1"),
                new Activity(
                    LocalDateTime.parse("2022-11-17T10:00:00"), Duration.ofMinutes(20), "A1"),
                new Activity(
                    LocalDateTime.parse("2022-11-17T11:00:00"), Duration.ofMinutes(20), "A2"),
                // Last day of interval
                new Activity(
                    LocalDateTime.parse("2022-11-18T08:00:00"), Duration.ofMinutes(20), "A2"),
                // First day after interval
                new Activity(
                    LocalDateTime.parse("2022-11-19T07:00:00"), Duration.ofMinutes(20), "A2")));

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
                timesheet.getEntries(),
                "entries"),
        () -> assertEquals(Duration.ofMinutes(160), timesheet.getTotal(), "total"));
  }
}
