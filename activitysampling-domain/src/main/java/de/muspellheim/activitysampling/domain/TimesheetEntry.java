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
    Objects.requireNonNull(date, "date");
    Objects.requireNonNull(notes, "notes");
    Objects.requireNonNull(hours, "hours");
  }

  public static TimesheetEntry parse(String date, String notes, String hours) {
    return new TimesheetEntry(LocalDate.parse(date), notes, Duration.parse(hours));
  }

  @Override
  public int compareTo(TimesheetEntry other) {
    return Comparator.comparing(TimesheetEntry::date)
        .thenComparing(TimesheetEntry::notes)
        .compare(this, other);
  }
}
