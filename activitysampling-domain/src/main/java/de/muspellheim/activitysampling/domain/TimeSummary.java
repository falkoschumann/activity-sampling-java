package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record TimeSummary(
    Duration hoursToday, Duration hoursYesterday, Duration hoursThisWeek, Duration hoursThisMonth) {
  public TimeSummary {
    Objects.requireNonNull(hoursToday, "hoursToday");
    Objects.requireNonNull(hoursYesterday, "hoursYesterday");
    Objects.requireNonNull(hoursThisWeek, "hoursThisWeek");
    Objects.requireNonNull(hoursThisMonth, "hoursThisMonth");
  }
}
