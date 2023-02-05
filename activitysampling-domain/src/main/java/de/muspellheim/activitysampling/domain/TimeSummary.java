/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

public record TimeSummary(
    LocalDate today,
    Duration hoursToday,
    Duration hoursYesterday,
    Duration hoursThisWeek,
    Duration hoursThisMonth) {

  public TimeSummary {
    Objects.requireNonNull(today, "today");
    Objects.requireNonNull(hoursToday, "hoursToday");
    Objects.requireNonNull(hoursYesterday, "hoursYesterday");
    Objects.requireNonNull(hoursThisWeek, "hoursThisWeek");
    Objects.requireNonNull(hoursThisMonth, "hoursThisMonth");
  }

  public static TimeSummary of(LocalDate today) {
    return new TimeSummary(today, Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO);
  }

  public TimeSummary add(Activity activity) {
    var date = activity.timestamp().toLocalDate();
    if (date.isAfter(today)) {
      return this;
    }

    var hoursToday = this.hoursToday;
    if (date.equals(today)) {
      hoursToday = hoursToday.plus(activity.duration());
    }

    var yesterday = today.minusDays(1);
    var hoursYesterday = this.hoursYesterday;
    if (date.equals(yesterday)) {
      hoursYesterday = hoursYesterday.plus(activity.duration());
    }

    var sameYear = (today.getDayOfYear() - date.getDayOfYear()) >= 0;
    var sameWeek =
        (today.getDayOfYear() - date.getDayOfYear()) < 7
            && date.getDayOfWeek().getValue() <= today.getDayOfWeek().getValue();
    var hoursThisWeek = this.hoursThisWeek;
    if (sameYear && sameWeek) {
      hoursThisWeek = hoursThisWeek.plus(activity.duration());
    }

    var startOfMonth = today.withDayOfMonth(1);
    var hoursThisMonth = this.hoursThisMonth;
    if (!date.isBefore(startOfMonth)) {
      hoursThisMonth = hoursThisMonth.plus(activity.duration());
    }

    return new TimeSummary(today, hoursToday, hoursYesterday, hoursThisWeek, hoursThisMonth);
  }

  public TimeSummary add(Iterable<Activity> activities) {
    var summary = this;
    for (var a : activities) {
      summary = summary.add(a);
    }
    return summary;
  }
}
