package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public class RecentActivities {
  private final LocalDate today;

  private final SortedMap<LocalDate, SortedSet<Activity>> workingDays = new TreeMap<>();
  private Duration hoursToday = Duration.ZERO;
  private Duration hoursYesterday = Duration.ZERO;
  private Duration hoursThisWeek = Duration.ZERO;
  private Duration hoursThisMonth = Duration.ZERO;

  public RecentActivities(LocalDate today) {
    this.today = today;
  }

  public void apply(Activity activity) {
    var date = activity.timestamp().toLocalDate();
    var startOfMonth = today.withDayOfMonth(1);

    if (date.equals(today)) {
      hoursToday = hoursToday.plus(activity.duration());
    } else if (date.equals(today.minusDays(1))) {
      hoursYesterday = hoursYesterday.plus(activity.duration());
    }
    if ((today.getDayOfYear() - date.getDayOfYear()) < 7
        && date.getDayOfWeek().getValue() <= today.getDayOfWeek().getValue()) {
      hoursThisWeek = hoursThisWeek.plus(activity.duration());
    }
    if (!date.isBefore(startOfMonth)) {
      hoursThisMonth = hoursThisMonth.plus(activity.duration());
    }

    var activities =
        workingDays.getOrDefault(
            date, new TreeSet<>((a1, a2) -> a2.timestamp().compareTo(a1.timestamp())));
    activities.add(activity);
    workingDays.put(date, activities);
  }

  public Iterable<WorkingDay> getWorkingDays() {
    return workingDays.entrySet().stream()
        .map(d -> new WorkingDay(d.getKey(), List.copyOf(d.getValue())))
        .sorted((d1, d2) -> d2.date().compareTo(d1.date()))
        .toList();
  }

  public TimeSummary getTimeSummary() {
    return new TimeSummary(hoursToday, hoursYesterday, hoursThisWeek, hoursThisMonth);
  }
}
