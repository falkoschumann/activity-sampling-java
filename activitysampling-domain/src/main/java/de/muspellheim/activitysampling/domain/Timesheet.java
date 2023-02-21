/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record Timesheet(List<TimesheetEntry> entries, Duration total) {
  public Timesheet {
    Objects.requireNonNull(entries, "entries");
    Objects.requireNonNull(total, "total");
  }

  public Timesheet() {
    this(List.of(), Duration.ZERO);
  }

  public static Timesheet from(Iterable<Activity> activities) {
    return new Timesheet().add(activities);
  }

  public Timesheet add(Activity activity) {
    var newEntries = new ArrayList<>(entries);
    var date = activity.timestamp().toLocalDate();

    var index = -1;
    for (var i = 0; i < newEntries.size(); i++) {
      var e = newEntries.get(i);
      if (e.date().equals(date) && e.notes().equals(activity.description())) {
        index = i;
        break;
      }
    }

    if (index != -1) {
      var entry = newEntries.get(index);
      entry = entry.add(activity);
      newEntries.set(index, entry);
    } else {
      var entry = new TimesheetEntry(date, activity.description(), activity.duration());
      newEntries.add(entry);
    }

    newEntries.sort(
        Comparator.comparing(TimesheetEntry::date).thenComparing(TimesheetEntry::notes));
    var newTotal = total.plus(activity.duration());
    return new Timesheet(newEntries, newTotal);
  }

  public Timesheet add(Iterable<Activity> activities) {
    var timesheet = this;
    for (var activity : activities) {
      timesheet = timesheet.add(activity);
    }
    return timesheet;
  }
}
