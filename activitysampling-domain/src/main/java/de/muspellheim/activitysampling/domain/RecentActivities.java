/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record RecentActivities(List<WorkingDay> workingDays, TimeSummary timeSummary) {
  public static final RecentActivities EMPTY = new RecentActivities(List.of(), TimeSummary.ZERO);

  public RecentActivities {
    Objects.requireNonNull(workingDays, "The working days must not be null.");
    Objects.requireNonNull(timeSummary, "The time summary must not be null.");
  }

  public static RecentActivities of(List<Activity> activities) {
    return new RecentActivities(
        WorkingDay.of(activities), TimeSummary.of(LocalDate.now(), activities));
  }
}
