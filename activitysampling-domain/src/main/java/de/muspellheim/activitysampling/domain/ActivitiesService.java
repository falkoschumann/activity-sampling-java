/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;

public interface ActivitiesService {
  // TODO add timestamp
  void logActivity(String description, Duration duration);

  // TODO add today and period
  RecentActivities getRecentActivities();

  Timesheet getTimesheet(LocalDate from, LocalDate to);
}
