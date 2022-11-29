package de.muspellheim.activitysampling.domain;

import java.time.*;

public interface ActivitiesService {
  void logActivity(String description, Duration duration);

  RecentActivities getRecentActivities();

  Timesheet createTimesheet(LocalDate from, LocalDate to);
}
