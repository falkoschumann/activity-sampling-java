package de.muspellheim.activitysampling.domain;

import java.time.*;

public class ActivitiesServiceImpl implements ActivitiesService {
  private final Activities activities;
  private final Clock clock;

  public ActivitiesServiceImpl(Activities activities) {
    this(activities, Clock.systemDefaultZone());
  }

  public ActivitiesServiceImpl(Activities activities, Clock clock) {
    this.activities = activities;
    this.clock = clock;
  }

  @Override
  public void logActivity(String description, Duration duration) {
    activities.append(new Activity(LocalDateTime.now(clock), duration, description.trim()));
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
