/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.util.Objects;

public record TimeSummary(
    Duration hoursToday, Duration hoursYesterday, Duration hoursThisWeek, Duration hoursThisMonth) {
  public TimeSummary {
    Objects.requireNonNull(hoursToday, "hoursToday");
    Objects.requireNonNull(hoursYesterday, "hoursYesterday");
    Objects.requireNonNull(hoursThisWeek, "hoursThisWeek");
    Objects.requireNonNull(hoursThisMonth, "hoursThisMonth");
  }
}
