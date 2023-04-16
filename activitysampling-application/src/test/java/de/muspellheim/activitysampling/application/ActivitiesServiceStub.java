/*
 * Activity Sampling - Application
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.ActivitiesService;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.utilities.ConfigurableResponses;
import de.muspellheim.utilities.EventEmitter;
import de.muspellheim.utilities.OutputTracker;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ActivitiesServiceStub implements ActivitiesService {
  private final EventEmitter<Activity> onActivityLogged = new EventEmitter<>();

  private ConfigurableResponses<?> logActivityResponses = new ConfigurableResponses<>(List.of());
  private ConfigurableResponses<RecentActivities> recentActivitiesResponses =
      new ConfigurableResponses<>(List.of());
  private ConfigurableResponses<Timesheet> timesheetResponses =
      new ConfigurableResponses<>(List.of());

  public void initLogActivityResponses(ConfigurableResponses<?> responses) {
    this.logActivityResponses = responses;
  }

  @Override
  public void logActivity(LocalDateTime timestamp, Duration duration, String description) {
    logActivityResponses.next();
    onActivityLogged.emit(new Activity(timestamp, duration, description));
  }

  public OutputTracker<Activity> getLoggedActivityTracker() {
    return new OutputTracker<>(onActivityLogged);
  }

  public void initRecentActivitiesResponses(ConfigurableResponses<RecentActivities> responses) {
    this.recentActivitiesResponses = responses;
  }

  @Override
  public RecentActivities getRecentActivities() {
    return recentActivitiesResponses.next();
  }

  public void initTimesheetResponses(ConfigurableResponses<Timesheet> responses) {
    this.timesheetResponses = responses;
  }

  @Override
  public Timesheet getTimesheet(LocalDate from, LocalDate to) {
    return timesheetResponses.next();
  }
}
