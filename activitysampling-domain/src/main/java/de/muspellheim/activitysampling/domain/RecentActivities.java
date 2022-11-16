package de.muspellheim.activitysampling.domain;

import java.util.*;

public record RecentActivities(List<WorkingDay> workingDays) {
  public RecentActivities {
    Objects.requireNonNull(workingDays, "workingDays");
  }
}
