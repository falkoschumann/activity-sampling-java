/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class RecentActivities {
  private final SortedMap<LocalDate, SortedSet<Activity>> workingDays =
      new TreeMap<>(Comparator.reverseOrder());
  private TimeSummary timeSummary;

  public RecentActivities() {
    this(LocalDate.now());
  }

  public RecentActivities(LocalDate today) {
    timeSummary = TimeSummary.of(today);
  }

  public void apply(Activity activity) {
    var date = activity.timestamp().toLocalDate();
    if (date.isAfter(timeSummary.today())) {
      return;
    }

    if (!workingDays.containsKey(date)) {
      workingDays.put(date, new TreeSet<>(Comparator.reverseOrder()));
    }
    var activities = workingDays.get(date);
    activities.add(activity);

    timeSummary = timeSummary.add(activity);
  }

  public Iterable<WorkingDay> getWorkingDays() {
    return workingDays.entrySet().stream()
        .map(d -> new WorkingDay(d.getKey(), List.copyOf(d.getValue())))
        .toList();
  }

  public TimeSummary getTimeSummary() {
    return timeSummary;
  }
}
