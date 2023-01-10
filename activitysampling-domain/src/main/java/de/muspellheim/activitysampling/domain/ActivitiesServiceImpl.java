/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class ActivitiesServiceImpl implements ActivitiesService {
  private final Activities activities;
  @Getter @Setter private Clock clock = Clock.systemDefaultZone();

  public ActivitiesServiceImpl(Activities activities) {
    this.activities = activities;
  }

  @Override
  public void logActivity(String description, Duration duration) {
    var activity = new Activity(LocalDateTime.now(clock), duration, description.trim());
    activities.append(activity);
  }

  @Override
  public RecentActivities getRecentActivities() {
    var today = LocalDate.now(clock);
    var from = today.minusDays(30);
    var recentActivities = new RecentActivities(today);
    activities.findInPeriod(from, today).forEach(recentActivities::apply);
    return recentActivities;
  }

  @Override
  public Timesheet createTimesheet(LocalDate from, LocalDate to) {
    var timesheet = new Timesheet();
    activities.findInPeriod(from, to).forEach(timesheet::apply);
    return timesheet;
  }
}
