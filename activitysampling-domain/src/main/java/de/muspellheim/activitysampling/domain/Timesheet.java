package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public class Timesheet {
  private final SortedMap<LocalDate, SortedMap<String, Duration>> activitiesPerDay =
      new TreeMap<>();
  private Duration total = Duration.ZERO;

  public Iterable<TimesheetEntry> getEntries() {
    var entries = new ArrayList<TimesheetEntry>();
    for (var day : activitiesPerDay.entrySet()) {
      for (var activity : day.getValue().entrySet()) {
        entries.add(new TimesheetEntry(day.getKey(), activity.getKey(), activity.getValue()));
      }
    }
    return List.copyOf(entries);
  }

  public Duration getTotal() {
    return total;
  }

  public void apply(Activity activity) {
    var date = activity.timestamp().toLocalDate();
    var activities = activitiesPerDay.getOrDefault(date, new TreeMap<>());
    activitiesPerDay.put(date, activities);

    var duration = activities.getOrDefault(activity.description(), Duration.ZERO);
    duration = duration.plus(activity.duration());
    activities.put(activity.description(), duration);

    total = total.plus(activity.duration());
  }
}
