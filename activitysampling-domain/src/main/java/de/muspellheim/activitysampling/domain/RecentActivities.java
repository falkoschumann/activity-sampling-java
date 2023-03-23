/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.util.List;
import java.util.Objects;

public record RecentActivities(List<WorkingDay> workingDays, TimeSummary timeSummary) {
  public RecentActivities {
    workingDays = List.copyOf(Objects.requireNonNull(workingDays, "workingDays"));
    Objects.requireNonNull(timeSummary, "timeSummary");
  }
}
