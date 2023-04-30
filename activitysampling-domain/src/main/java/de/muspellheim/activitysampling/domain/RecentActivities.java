/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.util.List;
import java.util.Objects;

public record RecentActivities(List<WorkingDay> workingDays) {
  public static final RecentActivities EMPTY = new RecentActivities(List.of());

  public RecentActivities {
    Objects.requireNonNull(workingDays, "The working days must not be null.");
  }

  public static RecentActivities of(List<Activity> activities) {
    return new RecentActivities(WorkingDay.of(activities));
  }
}
