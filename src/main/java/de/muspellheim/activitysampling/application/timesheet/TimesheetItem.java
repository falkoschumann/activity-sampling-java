/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.timesheet;

import java.util.Objects;

record TimesheetItem(String date, String activity, String duration) {
  TimesheetItem {
    Objects.requireNonNull(date, "The date must not be null.");
    Objects.requireNonNull(activity, "The activity must not be null.");
    Objects.requireNonNull(duration, "The duration must not be null.");
  }
}
