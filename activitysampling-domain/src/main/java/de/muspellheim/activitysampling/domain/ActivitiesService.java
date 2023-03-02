/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public interface ActivitiesService {
  void logActivity(LocalDateTime timestamp, Duration duration, String description);

  RecentActivities getRecentActivities(LocalDate date, Period period);

  Timesheet getTimesheet(LocalDate from, LocalDate to);
}
