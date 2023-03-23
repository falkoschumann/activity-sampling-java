/*
 * Activity Sampling - Application
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.ActivitiesService;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.activitysampling.domain.util.ConfigurableResponses;
import de.muspellheim.activitysampling.domain.util.EventEmitter;
import de.muspellheim.activitysampling.domain.util.OutputTracker;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

public class ActivitiesServiceStub implements ActivitiesService {
  private ConfigurableResponses<Object> logActivities = new ConfigurableResponses<>(true);
  private final EventEmitter<Activity> onActivityLogged = new EventEmitter<>();
  private ConfigurableResponses<RecentActivities> recentActivities =
      new ConfigurableResponses<>(List.of());
  private ConfigurableResponses<Timesheet> timesheet = new ConfigurableResponses<>(List.of());

  public void initLogActivity(ConfigurableResponses<Object> logActivities) {
    this.logActivities = logActivities;
  }

  public OutputTracker<Activity> getLoggedActivityTracker() {
    return new OutputTracker<>(onActivityLogged);
  }

  @Override
  public void logActivity(LocalDateTime timestamp, Duration duration, String description) {
    logActivities.next();
    onActivityLogged.emit(new Activity(timestamp, duration, description));
  }

  public void initRecentActivities(ConfigurableResponses<RecentActivities> recentActivities) {
    this.recentActivities = recentActivities;
  }

  @Override
  public RecentActivities getRecentActivities(LocalDate date, Period period) {
    return recentActivities.next();
  }

  public void initTimesheet(ConfigurableResponses<Timesheet> timesheet) {
    this.timesheet = timesheet;
  }

  @Override
  public Timesheet getTimesheet(LocalDate from, LocalDate to) {
    return timesheet.next();
  }
}
