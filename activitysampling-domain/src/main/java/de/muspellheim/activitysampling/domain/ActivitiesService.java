/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;

public interface ActivitiesService {
  void logActivity(String description, Duration duration);

  RecentActivities getRecentActivities();

  Timesheet createTimesheet(LocalDate from, LocalDate to);
}
