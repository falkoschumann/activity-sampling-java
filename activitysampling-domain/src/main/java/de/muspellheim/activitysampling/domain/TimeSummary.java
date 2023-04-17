/*
 * Activity Sampling - Domain
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

      if (date.equals(today)) {
        hoursToday = hoursToday.plus(activity.duration());
      }

      var yesterday = today.minusDays(1);
      if (date.equals(yesterday)) {
        hoursYesterday = hoursYesterday.plus(activity.duration());
      }

      var sameYear = (today.getDayOfYear() - date.getDayOfYear()) >= 0;
      var sameWeek =
          (today.getDayOfYear() - date.getDayOfYear()) < 7
              && date.getDayOfWeek().getValue() <= today.getDayOfWeek().getValue();
      if (sameYear && sameWeek) {
        hoursThisWeek = hoursThisWeek.plus(activity.duration());
      }

      var startOfMonth = today.withDayOfMonth(1);
      if (!date.isBefore(startOfMonth)) {
        hoursThisMonth = hoursThisMonth.plus(activity.duration());
      }
    }
    return new TimeSummary(hoursToday, hoursYesterday, hoursThisWeek, hoursThisMonth);
  }
}
