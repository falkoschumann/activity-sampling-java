/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.Timesheet;
import java.time.LocalDate;

public interface ActivitiesService {
  void logActivity(Activity activity);

  RecentActivities getRecentActivities();

  Timesheet getTimesheet(LocalDate from, LocalDate to);
}
