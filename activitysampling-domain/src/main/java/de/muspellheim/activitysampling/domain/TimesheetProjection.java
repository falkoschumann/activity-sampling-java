package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

class TimesheetProjection {
  private final LocalDate from;
  private final LocalDate to;

  private final SortedMap<LocalDate, SortedMap<String, Duration>> entries = new TreeMap<>();

  TimesheetProjection(LocalDate from, LocalDate to) {
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

    var activities = entries.getOrDefault(date, new TreeMap<>());
    entries.put(date, activities);

    var duration = activities.getOrDefault(event.description(), Duration.ZERO);
    duration = duration.plus(event.duration());
    activities.put(event.description(), duration);
  }

  List<WorkingDay> get() {
    return null;
    /*
    return entries.entrySet().stream()
        .map(
            d ->
                new WorkingDay(
                    d.getKey(),
                    d.getValue().entrySet().stream()
                        .map(e -> new TimesheetEntry(d.getKey(), e.getKey(), e.getValue()))
                        .toList()))
        .toList();

    */
  }
}
