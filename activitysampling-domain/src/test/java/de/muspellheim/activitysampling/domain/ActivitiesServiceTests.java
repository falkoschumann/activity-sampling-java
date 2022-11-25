package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;

class ActivitiesServiceTests {
  private EventStore eventStore;
  private MutableClock clock;
  private ActivitiesService sut;

  @BeforeEach
  void init() {
    eventStore = mock(EventStore.class);
    clock = new MutableClock();
    sut = new ActivitiesServiceImpl(eventStore, clock);
  }

  @Test
  void logActivity_RecordsActivityLogged() {
    clock.setTimestamp(Instant.parse("2022-11-16T11:26:00Z"));
    sut.logActivity("Lorem ipsum");

    verify(eventStore)
        .record(
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T11:26:00Z"), Duration.ofMinutes(20), "Lorem ipsum"));
  }

  @Test
  void logActivity_TrimsDescription() {
    clock.setTimestamp(Instant.parse("2022-11-16T11:26:00Z"));
    sut.logActivity("  Lorem ipsum ");

    verify(eventStore)
        .record(
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T11:26:00Z"), Duration.ofMinutes(20), "Lorem ipsum"));
  }

  @Test
  void selectRecentActivities_MonthWith30Days_ReturnsLast31DaysInDescendentOrder() {
    clock.setTimestamp(Instant.parse("2022-09-30T10:00:00Z"));
    when(eventStore.replay())
        .thenReturn(
            Stream.of(
                // Last month
                new ActivityLoggedEvent(
                    Instant.parse("2022-08-30T16:00:00Z"), Duration.ofMinutes(20), "A1"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-08-31T15:00:00Z"), Duration.ofMinutes(20), "A2"),
                // First day of this month
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-01T14:00:00Z"), Duration.ofMinutes(20), "A3"),
                // This Month
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-05T13:00:00Z"), Duration.ofMinutes(20), "A4"),
                // This Week
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-26T12:00:00Z"), Duration.ofMinutes(20), "A5"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-28T11:00:00Z"), Duration.ofMinutes(20), "A6"),
                // Yesterday
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-29T09:00:00Z"), Duration.ofMinutes(20), "A7"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-29T10:00:00Z"), Duration.ofMinutes(20), "A8"),
                // Today
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-30T07:00:00Z"), Duration.ofMinutes(20), "A9"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-30T08:00:00Z"), Duration.ofMinutes(20), "A10")));

    var activities = sut.selectRecentActivities();

    assertEquals(
        new RecentActivities(
            List.of(
                new WorkingDay(
                    LocalDate.of(2022, 9, 30),
                    List.of(
                        new Activity(LocalTime.of(10, 0), "A10"),
                        new Activity(LocalTime.of(9, 0), "A9"))),
                new WorkingDay(
                    LocalDate.of(2022, 9, 29),
                    List.of(
                        new Activity(LocalTime.of(12, 0), "A8"),
                        new Activity(LocalTime.of(11, 0), "A7"))),
                new WorkingDay(
                    LocalDate.of(2022, 9, 28), List.of(new Activity(LocalTime.of(13, 0), "A6"))),
                new WorkingDay(
                    LocalDate.of(2022, 9, 26), List.of(new Activity(LocalTime.of(14, 0), "A5"))),
                new WorkingDay(
                    LocalDate.of(2022, 9, 5), List.of(new Activity(LocalTime.of(15, 0), "A4"))),
                new WorkingDay(
                    LocalDate.of(2022, 9, 1), List.of(new Activity(LocalTime.of(16, 0), "A3"))),
                new WorkingDay(
                    LocalDate.of(2022, 8, 31), List.of(new Activity(LocalTime.of(17, 0), "A2")))),
            new TimeSummary(
                Duration.ofMinutes(40),
                Duration.ofMinutes(40),
                Duration.ofMinutes(120),
                Duration.ofMinutes(160))),
        activities);
  }

  @Test
  void selectRecentActivities_MonthWith31Days_ReturnsLast31DaysInDescendentOrder() {
    clock.setTimestamp(Instant.parse("2022-12-31T10:00:00Z"));
    when(eventStore.replay())
        .thenReturn(
            Stream.of(
                // Last month
                new ActivityLoggedEvent(
                    Instant.parse("2022-11-30T15:00:00Z"), Duration.ofMinutes(20), "A1"),
                // First day of this month
                new ActivityLoggedEvent(
                    Instant.parse("2022-12-01T14:00:00Z"), Duration.ofMinutes(20), "A2"),
                // This Month
                new ActivityLoggedEvent(
                    Instant.parse("2022-12-05T13:00:00Z"), Duration.ofMinutes(20), "A3"),
                // This Week
                new ActivityLoggedEvent(
                    Instant.parse("2022-12-26T12:00:00Z"), Duration.ofMinutes(20), "A4"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-12-28T11:00:00Z"), Duration.ofMinutes(20), "A5"),
                // Yesterday
                new ActivityLoggedEvent(
                    Instant.parse("2022-12-30T09:00:00Z"), Duration.ofMinutes(20), "A6"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-12-30T10:00:00Z"), Duration.ofMinutes(20), "A7"),
                // Today
                new ActivityLoggedEvent(
                    Instant.parse("2022-12-31T07:00:00Z"), Duration.ofMinutes(20), "A8"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-12-31T08:00:00Z"), Duration.ofMinutes(20), "A9")));

    var activities = sut.selectRecentActivities();

    assertEquals(
        new RecentActivities(
            List.of(
                new WorkingDay(
                    LocalDate.of(2022, 12, 31),
                    List.of(
                        new Activity(LocalTime.of(9, 0), "A9"),
                        new Activity(LocalTime.of(8, 0), "A8"))),
                new WorkingDay(
                    LocalDate.of(2022, 12, 30),
                    List.of(
                        new Activity(LocalTime.of(11, 0), "A7"),
                        new Activity(LocalTime.of(10, 0), "A6"))),
                new WorkingDay(
                    LocalDate.of(2022, 12, 28), List.of(new Activity(LocalTime.of(12, 0), "A5"))),
                new WorkingDay(
                    LocalDate.of(2022, 12, 26), List.of(new Activity(LocalTime.of(13, 0), "A4"))),
                new WorkingDay(
                    LocalDate.of(2022, 12, 5), List.of(new Activity(LocalTime.of(14, 0), "A3"))),
                new WorkingDay(
                    LocalDate.of(2022, 12, 1), List.of(new Activity(LocalTime.of(15, 0), "A2")))),
            new TimeSummary(
                Duration.ofMinutes(40),
                Duration.ofMinutes(40),
                Duration.ofMinutes(120),
                Duration.ofMinutes(160))),
        activities);
  }

  @Test
  void createTimesheet() {
    when(eventStore.replay())
        .thenReturn(
            Stream.of(
                // Last day before interval
                new ActivityLoggedEvent(
                    Instant.parse("2022-11-13T16:00:00Z"), Duration.ofMinutes(20), "A1"),
                // First day in the interval
                new ActivityLoggedEvent(
                    Instant.parse("2022-11-14T15:00:00Z"), Duration.ofMinutes(20), "A1"),
                // A day in the interval
                new ActivityLoggedEvent(
                    Instant.parse("2022-11-15T12:00:00Z"), Duration.ofMinutes(20), "A2"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-11-15T13:00:00Z"), Duration.ofMinutes(20), "A1"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-11-15T14:00:00Z"), Duration.ofMinutes(20), "A2"),
                // Another day in the interval
                new ActivityLoggedEvent(
                    Instant.parse("2022-11-17T09:00:00Z"), Duration.ofMinutes(20), "A1"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-11-17T10:00:00Z"), Duration.ofMinutes(20), "A1"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-11-17T11:00:00Z"), Duration.ofMinutes(20), "A2"),
                // Last day of interval
                new ActivityLoggedEvent(
                    Instant.parse("2022-11-18T08:00:00Z"), Duration.ofMinutes(20), "A2"),
                // First day after interval
                new ActivityLoggedEvent(
                    Instant.parse("2022-11-19T07:00:00Z"), Duration.ofMinutes(20), "A2")));

    var timesheet = sut.createTimesheet(LocalDate.of(2022, 11, 14), LocalDate.of(2022, 11, 18));

    assertEquals(
        new Timesheet(
            List.of(
                new TimesheetEntry(LocalDate.of(2022, 11, 14), "A1", Duration.ofMinutes(20)),
                new TimesheetEntry(LocalDate.of(2022, 11, 15), "A1", Duration.ofMinutes(20)),
                new TimesheetEntry(LocalDate.of(2022, 11, 15), "A2", Duration.ofMinutes(40)),
                new TimesheetEntry(LocalDate.of(2022, 11, 17), "A1", Duration.ofMinutes(40)),
                new TimesheetEntry(LocalDate.of(2022, 11, 17), "A2", Duration.ofMinutes(20)),
                new TimesheetEntry(LocalDate.of(2022, 11, 18), "A2", Duration.ofMinutes(20))),
            Duration.ofMinutes(160)),
        timesheet);
  }
}
