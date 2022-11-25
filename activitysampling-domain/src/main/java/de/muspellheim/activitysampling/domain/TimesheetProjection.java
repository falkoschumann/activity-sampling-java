package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

class TimesheetProjection {
  private final LocalDate from;
  private final LocalDate to;

  private final SortedMap<LocalDate, SortedMap<String, Duration>> activitiesPerDay =
      new TreeMap<>();

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

    var activities = activitiesPerDay.getOrDefault(date, new TreeMap<>());
    activitiesPerDay.put(date, activities);

    var duration = activities.getOrDefault(event.description(), Duration.ZERO);
    duration = duration.plus(event.duration());
    activities.put(event.description(), duration);
  }

  List<TimesheetEntry> get() {
    var entries = new ArrayList<TimesheetEntry>();
    for (var day : activitiesPerDay.entrySet()) {
      for (var activity : day.getValue().entrySet()) {
        entries.add(new TimesheetEntry(day.getKey(), activity.getKey(), activity.getValue()));
      }
    }
    return List.copyOf(entries);
  }
}
