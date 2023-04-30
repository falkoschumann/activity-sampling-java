/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ActivitiesService {
  void logActivity(LocalDateTime timestamp, Duration duration, String description);

  RecentActivities getRecentActivities();

  TimeSummary getTimeSummary();

  Timesheet getTimesheet(LocalDate from, LocalDate to);
}
