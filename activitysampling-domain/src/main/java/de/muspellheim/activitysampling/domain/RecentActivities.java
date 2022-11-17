package de.muspellheim.activitysampling.domain;

import java.util.*;

public record RecentActivities(List<WorkingDay> workingDays, TimeSummary timeSummary) {
  public RecentActivities {
    Objects.requireNonNull(workingDays, "workingDays");
    Objects.requireNonNull(timeSummary, "timeSummary");
  }
}
