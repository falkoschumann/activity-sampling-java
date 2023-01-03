/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public class RecentActivities {
  private final LocalDate today;

  private final SortedMap<LocalDate, SortedSet<Activity>> workingDays =
      new TreeMap<>(Comparator.reverseOrder());
  private Duration hoursToday = Duration.ZERO;
  private Duration hoursYesterday = Duration.ZERO;
  private Duration hoursThisWeek = Duration.ZERO;
  private Duration hoursThisMonth = Duration.ZERO;

  public RecentActivities() {
    this(LocalDate.now());
  }

  public RecentActivities(LocalDate today) {
    this.today = today;
  }

  public void apply(Activity activity) {
    var date = activity.timestamp().toLocalDate();
    if (date.isAfter(today)) {
      return;
    }

    if (!workingDays.containsKey(date)) {
      workingDays.put(date, new TreeSet<>(Comparator.reverseOrder()));
    }
    var activities = workingDays.get(date);
    activities.add(activity);

    var startOfMonth = today.withDayOfMonth(1);
    if (date.equals(today)) {
      hoursToday = hoursToday.plus(activity.duration());
    } else if (date.equals(today.minusDays(1))) {
      hoursYesterday = hoursYesterday.plus(activity.duration());
    }
    if ((today.getDayOfYear() - date.getDayOfYear()) >= 0
        && (today.getDayOfYear() - date.getDayOfYear()) < 7
        && date.getDayOfWeek().getValue() <= today.getDayOfWeek().getValue()) {
      hoursThisWeek = hoursThisWeek.plus(activity.duration());
    }
    if (!date.isBefore(startOfMonth)) {
      hoursThisMonth = hoursThisMonth.plus(activity.duration());
    }
  }

  public Iterable<WorkingDay> getWorkingDays() {
    return workingDays.entrySet().stream()
        .map(d -> new WorkingDay(d.getKey(), List.copyOf(d.getValue())))
        .toList();
  }

  public TimeSummary getTimeSummary() {
    return new TimeSummary(hoursToday, hoursYesterday, hoursThisWeek, hoursThisMonth);
  }
}
