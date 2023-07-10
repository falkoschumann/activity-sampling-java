/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.Activities;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.Timesheet;
import java.time.LocalDate;
import java.time.Period;

public class ActivitiesServiceImpl implements ActivitiesService {
  private final Activities activities;

  public ActivitiesServiceImpl(Activities activities) {
    this.activities = activities;
  }

  @Override
  public void logActivity(Activity activity) {
    try {
      activities.append(activity);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to log activity: %s.".formatted(activity), e);
    }
  }

  @Override
  public RecentActivities getRecentActivities() {
    try {
      var today = LocalDate.now();
      var start = today.minus(Period.ofDays(31));
      var activities = this.activities.findInPeriod(start, today);
      return RecentActivities.from(today, activities);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to get recent activities.", e);
    }
  }

  @Override
  public Timesheet getTimesheet(LocalDate from, LocalDate to) {
    try {
      var activities = this.activities.findInPeriod(from, to);
      return Timesheet.from(activities);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Failed to get timesheet from %s to %s.".formatted(from, to), e);
    }
  }
}
