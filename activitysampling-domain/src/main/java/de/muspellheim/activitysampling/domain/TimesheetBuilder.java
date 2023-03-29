/*
 * Activity Sampling - Domain
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TimesheetBuilder {
  private final List<TimesheetEntry> entries = new ArrayList<>();
  private Duration total = Duration.ZERO;

  void add(Activity activity) {
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

  Timesheet build() {
    return new Timesheet(entries, total);
  }
}
