/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class ActivitiesServiceImpl implements ActivitiesService {
  private final ActivitiesRepository activitiesRepository;

  public ActivitiesServiceImpl(ActivitiesRepository activitiesRepository) {
    this.activitiesRepository = activitiesRepository;
  }

  @Override
  public void logActivity(LocalDateTime timestamp, Duration duration, String description) {
    var activity = new Activity(timestamp, duration, description);
    activitiesRepository.append(activity);
  }

  @Override
  public RecentActivities getRecentActivities() {
    var today = LocalDate.now();
    var start = today.minus(Period.ofDays(31));
    var activities = activitiesRepository.findInPeriod(start, today);
    return RecentActivities.of(activities);
  }

  @Override
  public Timesheet getTimesheet(LocalDate from, LocalDate to) {
    var activities = activitiesRepository.findInPeriod(from, to);
    return Timesheet.of(activities);
  }
}
