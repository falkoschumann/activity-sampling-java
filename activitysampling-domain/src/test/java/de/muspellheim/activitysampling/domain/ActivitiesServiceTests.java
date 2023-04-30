/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivitiesServiceTests {
  private FakeActivities activitiesRepository;
  private ActivitiesServiceImpl sut;

  @BeforeEach
  void init() {
    activitiesRepository = new FakeActivities();
    sut = new ActivitiesServiceImpl(activitiesRepository);
  }

  @Test
  void logActivity() {
    var now = LocalDateTime.now();
    var duration = Duration.ofMinutes(5);

    sut.logActivity(now, duration, "xyz");

    assertEquals(List.of(new Activity(now, duration, "xyz")), activitiesRepository);
  }

  @Test
  void getRecentActivities() {
    var now = LocalDateTime.now();
    var duration = Duration.ofMinutes(5);
    activitiesRepository.add(new Activity(now, duration, "xyz"));

    var activities = sut.getRecentActivities();

    assertEquals(
        new RecentActivities(
            List.of(
                new WorkingDay(now.toLocalDate(), List.of(new Activity(now, duration, "xyz"))))),
        activities);
  }

  @Test
  void getTimeSummary() {
    var now = LocalDateTime.now();
    var duration = Duration.ofMinutes(5);
    activitiesRepository.add(new Activity(now, duration, "xyz"));

    var summary = sut.getTimeSummary();

    assertEquals(new TimeSummary(duration, Duration.ZERO, duration, duration), summary);
  }

  @Test
  void getTimesheet() {
    activitiesRepository.addAll(
        List.of(
            new Activity(LocalDateTime.of(2022, 11, 14, 15, 0, 0), Duration.ofMinutes(20), "c"),
            new Activity(LocalDateTime.of(2022, 11, 14, 16, 0, 0), Duration.ofMinutes(20), "b"),
            new Activity(LocalDateTime.of(2022, 11, 18, 8, 0, 0), Duration.ofMinutes(20), "a")));

    var timesheet = sut.getTimesheet(LocalDate.parse("2022-11-14"), LocalDate.parse("2022-11-18"));

    assertEquals(
        new Timesheet(
            List.of(
                new Timesheet.Entry(LocalDate.of(2022, 11, 14), "b", Duration.ofMinutes(20)),
                new Timesheet.Entry(LocalDate.of(2022, 11, 14), "c", Duration.ofMinutes(20)),
                new Timesheet.Entry(LocalDate.of(2022, 11, 18), "a", Duration.ofMinutes(20))),
            Duration.ofMinutes(60)),
        timesheet);
  }
}
