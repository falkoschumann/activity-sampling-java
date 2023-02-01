/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public record WorkingDay(LocalDate date, List<Activity> activities) {
  public WorkingDay {
    Objects.requireNonNull(date, "date");
    Objects.requireNonNull(activities, "activities");
  }

  public static List<WorkingDay> from(List<Activity> activities) {
    var workingDays = new TreeMap<LocalDate, SortedSet<Activity>>(Comparator.reverseOrder());

    for (var activity : activities) {
      var date = activity.timestamp().toLocalDate();
      if (!workingDays.containsKey(date)) {
        workingDays.put(date, new TreeSet<>(Comparator.reverseOrder()));
      }
      workingDays.get(date).add(activity);
    }

    return workingDays.entrySet().stream()
        .map(d -> new WorkingDay(d.getKey(), List.copyOf(d.getValue())))
        .toList();
  }
}
