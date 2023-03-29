/*
 * Activity Sampling - Application
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.ActivitiesService;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.activitysampling.util.ConfigurableResponses;
import de.muspellheim.activitysampling.util.EventEmitter;
import de.muspellheim.activitysampling.util.OutputTracker;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

public class ActivitiesServiceStub implements ActivitiesService {
  private final EventEmitter<Activity> onActivityLogged = new EventEmitter<>();

  private ConfigurableResponses<Object> logActivities = new ConfigurableResponses<>(true);
  private ConfigurableResponses<RecentActivities> recentActivities =
      new ConfigurableResponses<>(List.of());
  private ConfigurableResponses<Timesheet> timesheet = new ConfigurableResponses<>(List.of());

  public void initLogActivity(ConfigurableResponses<Object> logActivities) {
    this.logActivities = logActivities;
  }

  public void initRecentActivities(ConfigurableResponses<RecentActivities> recentActivities) {
    this.recentActivities = recentActivities;
  }

  public void initTimesheet(ConfigurableResponses<Timesheet> timesheet) {
    this.timesheet = timesheet;
  }

  public OutputTracker<Activity> getLoggedActivityTracker() {
    return new OutputTracker<>(onActivityLogged);
  }

  @Override
  public void logActivity(LocalDateTime timestamp, Duration duration, String description) {
    logActivities.next();
    onActivityLogged.emit(new Activity(timestamp, duration, description));
  }

  @Override
  public RecentActivities getRecentActivities(LocalDate date, Period period) {
    return recentActivities.next();
  }

  @Override
  public Timesheet getTimesheet(LocalDate from, LocalDate to) {
    return timesheet.next();
  }
}
