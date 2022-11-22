package de.muspellheim.activitysampling.application;

import java.util.*;

record TimesheetItem(String date, String activity, String duration) {
  TimesheetItem {
    Objects.requireNonNull(date, "date");
    Objects.requireNonNull(activity, "activity");
    Objects.requireNonNull(duration, "duration");
  }
}
