/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.timesheet;

import java.util.*;

public record TimesheetItem(String date, String activity, String duration) {
  public TimesheetItem {
    Objects.requireNonNull(date, "date");
    Objects.requireNonNull(activity, "activity");
    Objects.requireNonNull(duration, "duration");
  }
}
