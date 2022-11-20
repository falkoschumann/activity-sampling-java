package de.muspellheim.activitysampling.domain;

import java.time.*;

class TimeSummaryProjection {
  private final LocalDate today;
  private final LocalDate startOfMonth;

  private Duration hoursToday = Duration.ZERO;
  private Duration hoursYesterday = Duration.ZERO;
  private Duration hoursThisWeek = Duration.ZERO;
  private Duration hoursThisMonth = Duration.ZERO;

  TimeSummaryProjection(LocalDate today) {
    this.today = today;
    startOfMonth = today.withDayOfMonth(1);
  }

  void apply(Event event) {
    if (event instanceof ActivityLoggedEvent e) {
      apply(e);
    }
  }

  private void apply(ActivityLoggedEvent event) {
    var date = LocalDate.ofInstant(event.timestamp(), ZoneId.systemDefault());
    if (date.isBefore(startOfMonth)) {
      return;
    }

    if (date.equals(today)) {
      hoursToday = hoursToday.plus(event.duration());
    } else if (date.equals(today.minusDays(1))) {
      hoursYesterday = hoursYesterday.plus(event.duration());
    }
    if ((today.getDayOfYear() - date.getDayOfYear()) < 7
        && date.getDayOfWeek().getValue() <= today.getDayOfWeek().getValue()) {
      hoursThisWeek = hoursThisWeek.plus(event.duration());
    }
    hoursThisMonth = hoursThisMonth.plus(event.duration());
  }

  TimeSummary get() {
    return new TimeSummary(hoursToday, hoursYesterday, hoursThisWeek, hoursThisMonth);
  }
}
