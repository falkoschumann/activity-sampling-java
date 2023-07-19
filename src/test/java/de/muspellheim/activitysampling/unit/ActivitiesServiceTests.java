/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.muspellheim.activitysampling.application.ActivitiesService;
import de.muspellheim.activitysampling.application.ActivitiesServiceImpl;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.TimeSummary;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.activitysampling.domain.WorkingDay;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivitiesServiceTests {

  private FakeActivities activitiesRepository;
  private ActivitiesService sut;

  @BeforeEach
  void init() {
    activitiesRepository = new FakeActivities();
    sut = new ActivitiesServiceImpl(activitiesRepository);
  }

  @Test
  void logActivity() {
    var activity = newActivity(LocalDateTime.now());

    sut.logActivity(activity);

    assertEquals(List.of(activity), activitiesRepository);
  }

  @Test
  void logActivity_Failed_ThrowsException() {
    var activitiesRepository =
        new FakeActivities() {
          @Override
          public void append(Activity activity) throws Exception {
            throw new Exception();
          }
        };
    var sut = new ActivitiesServiceImpl(activitiesRepository);
    var activity = newActivity(LocalDateTime.now());

    assertThrows(IllegalStateException.class, () -> sut.logActivity(activity));
  }

  @Test
  void getRecentActivities() {
    var now = LocalDateTime.now();
    var lastMonth = now.minusDays(31);
    var activityLastMonth = newActivity(lastMonth);
    var activityToday = newActivity(now);
    activitiesRepository.addAll(List.of(activityLastMonth, activityToday));

    var activities = sut.getRecentActivities();

    Assertions.assertEquals(
        new RecentActivities(
            List.of(
                new WorkingDay(now.toLocalDate(), List.of(activityToday)),
                new WorkingDay(lastMonth.toLocalDate(), List.of(activityLastMonth))),
            TimeSummary.builder()
                .hoursToday(Duration.ofMinutes(30))
                .hoursYesterday(Duration.ZERO)
                .hoursThisWeek(Duration.ofMinutes(30))
                .hoursThisMonth(Duration.ofMinutes(30))
                .build()),
        activities);
  }

  @Test
  void getRecentActivities_Failed_ThrowsException() {
    var activitiesRepository =
        new FakeActivities() {
          @Override
          public List<Activity> findInPeriod(LocalDate from, LocalDate to) throws Exception {
            throw new Exception();
          }
        };
    var sut = new ActivitiesServiceImpl(activitiesRepository);

    assertThrows(IllegalStateException.class, sut::getRecentActivities);
  }

  @Test
  void getTimesheet() {
    var now = LocalDateTime.now();
    activitiesRepository.add(newActivity(now));

    var timesheet = sut.getTimesheet(now.toLocalDate(), now.toLocalDate());

    Assertions.assertEquals(
        new Timesheet(
            List.of(
                Timesheet.Entry.builder()
                    .date(now.toLocalDate())
                    .client("client")
                    .project("project")
                    .task("task")
                    .hours(Duration.ofMinutes(30))
                    .build())),
        timesheet);
  }

  @Test
  void getTimesheet_Failed_ThrowsException() {
    var activitiesRepository =
        new FakeActivities() {
          @Override
          public List<Activity> findInPeriod(LocalDate from, LocalDate to) throws Exception {
            throw new Exception();
          }
        };
    var sut = new ActivitiesServiceImpl(activitiesRepository);
    var now = LocalDate.now();

    assertThrows(IllegalStateException.class, () -> sut.getTimesheet(now, now));
  }

  private static Activity newActivity(LocalDateTime timestamp) {
    return Activity.builder()
        .timestamp(timestamp)
        .duration(Duration.ofMinutes(30))
        .client("client")
        .project("project")
        .task("task")
        .notes("notes")
        .build();
  }
}
