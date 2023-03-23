/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public record Timesheet(List<TimesheetEntry> entries, Duration total) {
  public Timesheet {
    entries = List.copyOf(Objects.requireNonNull(entries, "entries"));
    Objects.requireNonNull(total, "total");
  }
}
