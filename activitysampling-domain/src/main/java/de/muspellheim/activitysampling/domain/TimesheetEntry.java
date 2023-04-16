/*
 * Activity Sampling - Domain
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

public record TimesheetEntry(LocalDate date, String notes, Duration hours)
    implements Comparable<TimesheetEntry> {
  public TimesheetEntry {
    Objects.requireNonNull(date, "The date must not be null.");
    Objects.requireNonNull(notes, "The notes must not be null.");
    Objects.requireNonNull(hours, "The hours must not be null.");
  }

  @Override
  public int compareTo(TimesheetEntry other) {
    return Comparator.comparing(TimesheetEntry::date)
        .thenComparing(TimesheetEntry::notes)
        .compare(this, other);
  }
}
