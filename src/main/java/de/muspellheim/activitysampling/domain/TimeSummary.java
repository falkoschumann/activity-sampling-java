/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record TimeSummary(
    Duration hoursToday, Duration hoursYesterday, Duration hoursThisWeek, Duration hoursThisMonth) {

  public static final TimeSummary ZERO =
      new TimeSummary(Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO);

  public TimeSummary {
    Objects.requireNonNull(hoursToday, "The hours today must not be null.");
    Objects.requireNonNull(hoursYesterday, "The hours yesterday must not be null.");
    Objects.requireNonNull(hoursThisWeek, "The hours this week must not be null.");
    Objects.requireNonNull(hoursThisMonth, "The hours this month must not be null.");
  }

  static TimeSummary of(LocalDate today, List<Activity> activities) {
    var hoursToday = Duration.ZERO;
    var hoursYesterday = Duration.ZERO;
    var hoursThisWeek = Duration.ZERO;
    var hoursThisMonth = Duration.ZERO;
    for (var activity : activities) {
      var date = activity.timestamp().toLocalDate();
      if (date.isAfter(today)) {
        continue;
      }

      if (isToday(today, date)) {
        hoursToday = hoursToday.plus(activity.duration());
      }
      if (isYesterday(today, date)) {
        hoursYesterday = hoursYesterday.plus(activity.duration());
      }
      if (isSameWeek(today, date)) {
        hoursThisWeek = hoursThisWeek.plus(activity.duration());
      }
      if (isSameMonth(today, date)) {
        hoursThisMonth = hoursThisMonth.plus(activity.duration());
      }
    }
    return new TimeSummary(hoursToday, hoursYesterday, hoursThisWeek, hoursThisMonth);
  }

  private static boolean isToday(LocalDate today, LocalDate other) {
    return today.equals(other);
  }

  private static boolean isYesterday(LocalDate today, LocalDate other) {
    var yesterday = today.minusDays(1);
    return yesterday.equals(other);
  }

  private static boolean isSameWeek(LocalDate today, LocalDate other) {
    return (today.getDayOfYear() - other.getDayOfYear()) < 7
        && other.getDayOfWeek().getValue() <= today.getDayOfWeek().getValue();
  }

  private static boolean isSameMonth(LocalDate today, LocalDate other) {
    var startOfMonth = today.withDayOfMonth(1);
    return !other.isBefore(startOfMonth);
  }
}
