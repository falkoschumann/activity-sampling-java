/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.e2e;

import de.muspellheim.activitysampling.application.ActivitiesService;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.TimeReport;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.activitysampling.util.ConfigurableResponses;
import de.muspellheim.activitysampling.util.EventEmitter;
import de.muspellheim.activitysampling.util.OutputTracker;
import java.time.LocalDate;

class ActivitiesServiceStub implements ActivitiesService {

  private final EventEmitter<Activity> onActivityLogged = new EventEmitter<>();

  private ConfigurableResponses<?> logActivityResponses = ConfigurableResponses.empty();
  private ConfigurableResponses<RecentActivities> recentActivitiesResponses =
      ConfigurableResponses.empty();
  private ConfigurableResponses<Timesheet> timesheetResponses = ConfigurableResponses.empty();
  private ConfigurableResponses<TimeReport> timeReportResponses = ConfigurableResponses.empty();

  void initLogActivityResponses(ConfigurableResponses<?> responses) {
    this.logActivityResponses = responses;
  }

  void initRecentActivitiesResponses(ConfigurableResponses<RecentActivities> responses) {
    this.recentActivitiesResponses = responses;
  }

  void initTimesheetResponses(ConfigurableResponses<Timesheet> responses) {
    this.timesheetResponses = responses;
  }

  void initReportResponses(ConfigurableResponses<TimeReport> responses) {
    this.timeReportResponses = responses;
  }

  OutputTracker<Activity> trackLoggedActivity() {
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

  @Override
  public TimeReport getTimeReport(LocalDate from, LocalDate to) {
    return timeReportResponses.next();
  }
}
