package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

class TimesheetByDayProjection {
  private final LocalDate from;
  private final LocalDate to;

  private final SortedMap<LocalDate, SortedMap<String, Duration>> entries = new TreeMap<>();

  TimesheetByDayProjection(LocalDate from, LocalDate to) {
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

  List<TimesheetByDay> get() {
    return entries.entrySet().stream()
        .map(
            d ->
                new TimesheetByDay(
                    d.getKey(), getTimesheetEntries(d), getSubtotal(d.getValue().values())))
        .toList();
  }

  private static List<TimesheetEntry> getTimesheetEntries(
      Map.Entry<LocalDate, SortedMap<String, Duration>> d) {
    return d.getValue().entrySet().stream()
        .map(e -> new TimesheetEntry(e.getKey(), e.getValue()))
        .toList();
  }

  private static Duration getSubtotal(Collection<Duration> durations) {
    return durations.stream().reduce(Duration::plus).orElse(Duration.ZERO);
  }
}
