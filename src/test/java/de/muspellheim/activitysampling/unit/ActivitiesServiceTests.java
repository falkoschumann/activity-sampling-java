/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
  private ActivitiesServiceImpl sut;

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
    var activity = newActivity(now);
    activitiesRepository.add(activity);

    var activities = sut.getRecentActivities();

    Assertions.assertEquals(
        new RecentActivities(
            List.of(new WorkingDay(now.toLocalDate(), List.of(activity))),
            new TimeSummary(
                Duration.ofMinutes(30),
                Duration.ZERO,
                Duration.ofMinutes(30),
                Duration.ofMinutes(30))),
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
                new Timesheet.Entry(
                    now.toLocalDate(), "client", "project", "notes", Duration.ofMinutes(30))),
            Duration.ofMinutes(30)),
        timesheet);
  }

  private static Activity newActivity(LocalDateTime timestamp) {
    return new Activity(timestamp, Duration.ofMinutes(30), "client", "project", "notes");
  }
}
