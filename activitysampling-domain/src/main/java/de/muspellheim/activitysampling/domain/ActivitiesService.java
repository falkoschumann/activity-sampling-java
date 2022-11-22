package de.muspellheim.activitysampling.domain;

import java.time.*;

public interface ActivitiesService {
  void logActivity(String description);

  RecentActivities selectRecentActivities();

  Timesheet createTimesheet(LocalDate from, LocalDate to);
}
