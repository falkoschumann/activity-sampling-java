/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.LocalDate;

public interface ActivitiesService {
  void logActivity(Activity activity);

  RecentActivities getRecentActivities();

  Timesheet getTimesheet(LocalDate from, LocalDate to);
}
