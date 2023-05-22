/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.LocalDate;
import java.time.Period;

public class ActivitiesServiceImpl implements ActivitiesService {
  private final Activities activities;

  public ActivitiesServiceImpl(Activities activities) {
    this.activities = activities;
  }

  @Override
  public void logActivity(Activity activity) {
    activities.append(activity);
  }

  @Override
  public RecentActivities getRecentActivities() {
    var today = LocalDate.now();
    var start = today.minus(Period.ofDays(31));
    var activities = this.activities.findInPeriod(start, today);
    return RecentActivities.of(today, activities);
  }

  @Override
  public Timesheet getTimesheet(LocalDate from, LocalDate to) {
    var activities = this.activities.findInPeriod(from, to);
    return Timesheet.of(activities);
  }
}
