package de.muspellheim.activitysampling.domain;

import java.time.*;

class TotalProjection {
  private final LocalDate from;
  private final LocalDate to;

  private Duration total = Duration.ZERO;

  TotalProjection(LocalDate from, LocalDate to) {
    this.from = from;
    this.to = to;
  }

  void apply(Event event) {
    if (event instanceof ActivityLoggedEvent e) {
      apply(e);
    }
  }

  private void apply(ActivityLoggedEvent event) {
    var date = LocalDate.ofInstant(event.timestamp(), ZoneId.systemDefault());
    if (date.isBefore(from) || date.isAfter(to)) {
      return;
    }

    total = total.plus(event.duration());
  }

  Duration get() {
    return total;
  }
}
