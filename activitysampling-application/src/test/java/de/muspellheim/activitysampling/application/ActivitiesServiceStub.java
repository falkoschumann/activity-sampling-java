/*
 * Activity Sampling - Application
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.ActivitiesService;
import de.muspellheim.activitysampling.domain.ConfigurableResponses;
import de.muspellheim.activitysampling.domain.EventEmitter;
import de.muspellheim.activitysampling.domain.OutputTracker;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.Timesheet;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class ActivitiesServiceStub implements ActivitiesService {

  public record ActivityLogged(String description, Duration duration) {}

  private ConfigurableResponses<Object> logActivities = new ConfigurableResponses<>(true);
  private final EventEmitter<ActivityLogged> onActivityLogged = new EventEmitter<>();
  private ConfigurableResponses<RecentActivities> recentActivities =
      new ConfigurableResponses<>(List.of());
  private ConfigurableResponses<Timesheet> timesheet = new ConfigurableResponses<>(List.of());

  public void initLogActivity(ConfigurableResponses<Object> logActivities) {
    this.logActivities = logActivities;
  }

  public OutputTracker<ActivityLogged> getLoggedActivityTracker() {
    return new OutputTracker<>(onActivityLogged);
  }

  @Override
  public void logActivity(String description, Duration duration) {
    logActivities.next();
    onActivityLogged.emit(new ActivityLogged(description, duration));
  }

  public void initRecentActivities(ConfigurableResponses<RecentActivities> recentActivities) {
    this.recentActivities = recentActivities;
  }

  @Override
  public RecentActivities getRecentActivities() {
    return recentActivities.next();
  }

  public void initTimesheet(ConfigurableResponses<Timesheet> timesheet) {
    this.timesheet = timesheet;
  }

  @Override
  public Timesheet createTimesheet(LocalDate from, LocalDate to) {
    return timesheet.next();
  }
}
