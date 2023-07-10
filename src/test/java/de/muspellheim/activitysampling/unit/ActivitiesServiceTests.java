/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.application.ActivitiesService;
import de.muspellheim.activitysampling.application.ActivitiesServiceImpl;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.TimeSummary;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.activitysampling.domain.WorkingDay;
import java.time.Duration;
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
