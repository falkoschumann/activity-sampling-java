/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui;

import de.muspellheim.activitysampling.domain.ActivitiesService;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.common.util.ConfigurableResponses;
import de.muspellheim.common.util.EventEmitter;
import de.muspellheim.common.util.OutputTracker;
import java.time.LocalDate;

public class ActivitiesServiceStub implements ActivitiesService {
  private final EventEmitter<Activity> onActivityLogged = new EventEmitter<>();

  private ConfigurableResponses<?> logActivityResponses = ConfigurableResponses.empty();
  private ConfigurableResponses<RecentActivities> recentActivitiesResponses =
      ConfigurableResponses.empty();
  private ConfigurableResponses<Timesheet> timesheetResponses = ConfigurableResponses.empty();

  public void initLogActivityResponses(ConfigurableResponses<?> responses) {
    this.logActivityResponses = responses;
  }

  public void initRecentActivitiesResponses(ConfigurableResponses<RecentActivities> responses) {
    this.recentActivitiesResponses = responses;
  }

  public void initTimesheetResponses(ConfigurableResponses<Timesheet> responses) {
    this.timesheetResponses = responses;
  }

  public OutputTracker<Activity> trackLoggedActivity() {
    return new OutputTracker<>(onActivityLogged);
  }

  @Override
  public void logActivity(Activity activity) {
    logActivityResponses.next();
    onActivityLogged.emit(activity);
  }

  @Override
  public RecentActivities getRecentActivities() {
    return recentActivitiesResponses.next();
  }

  @Override
  public Timesheet getTimesheet(LocalDate from, LocalDate to) {
    return timesheetResponses.next();
  }
}
