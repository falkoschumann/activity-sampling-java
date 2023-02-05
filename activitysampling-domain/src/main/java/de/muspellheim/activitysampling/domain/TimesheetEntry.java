/*
 * Activity Sampling - Domain
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

public record TimesheetEntry(LocalDate date, String notes, Duration hours) {
  public TimesheetEntry {
    Objects.requireNonNull(date, "date");
    Objects.requireNonNull(notes, "notes");
    Objects.requireNonNull(hours, "hours");
  }

  public TimesheetEntry add(Activity activity) {
    // TODO validate date and notes
    var newHours = hours.plus(activity.duration());
    return new TimesheetEntry(date, notes, newHours);
  }
}
