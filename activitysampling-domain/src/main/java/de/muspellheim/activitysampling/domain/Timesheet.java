/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record Timesheet(List<TimesheetEntry> entries, Duration total) {
  public static final Timesheet EMPTY = new Timesheet(List.of(), Duration.ZERO);

  public Timesheet {
    Objects.requireNonNull(entries, "The entries must not be null.");
    Objects.requireNonNull(total, "The total must not be null.");
  }

  static Timesheet of(List<Activity> activities) {
    var entries = new ArrayList<TimesheetEntry>();
    var total = Duration.ZERO;
    for (var activity : activities) {
      var date = activity.timestamp().toLocalDate();

      var index = -1;
      for (var i = 0; i < entries.size(); i++) {
        var e = entries.get(i);
        if (e.date().equals(date) && e.notes().equals(activity.description())) {
          index = i;
          break;
        }
      }

      if (index != -1) {
        var entry = entries.get(index);
        var hours = entry.hours().plus(activity.duration());
        entry = new TimesheetEntry(entry.date(), entry.notes(), hours);
        entries.set(index, entry);
      } else {
        var entry = new TimesheetEntry(date, activity.description(), activity.duration());
        entries.add(entry);
      }

      Collections.sort(entries);
      total = total.plus(activity.duration());
    }
    return new Timesheet(entries, total);
  }
}
