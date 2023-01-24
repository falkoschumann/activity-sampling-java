/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class Timesheet {
  public record Entry(LocalDate date, String notes, Duration hours) {}

  private final SortedMap<LocalDate, SortedMap<String, Duration>> activitiesPerDay =
      new TreeMap<>();
  private Duration total = Duration.ZERO;

  public void add(Activity activity) {
    var date = activity.timestamp().toLocalDate();
    if (!activitiesPerDay.containsKey(date)) {
      activitiesPerDay.put(date, new TreeMap<>());
    }
    var activities = activitiesPerDay.get(date);
    var duration =
        activities.getOrDefault(activity.description(), Duration.ZERO).plus(activity.duration());
    activities.put(activity.description(), duration);

    total = total.plus(activity.duration());
  }

  public void addAll(Iterable<Activity> activities) {
    activities.forEach(this::add);
  }

  public Iterable<Entry> getEntries() {
    var entries = new ArrayList<Entry>();
    for (var day : activitiesPerDay.entrySet()) {
      for (var activity : day.getValue().entrySet()) {
        entries.add(new Entry(day.getKey(), activity.getKey(), activity.getValue()));
      }
    }
    return List.copyOf(entries);
  }

  public Duration getTotal() {
    return total;
  }
}
