/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record WorkingDay(LocalDate date, List<Activity> activities)
    implements Comparable<WorkingDay> {
  public WorkingDay {
    Objects.requireNonNull(date, "The date must not be null.");
    activities =
        List.copyOf(Objects.requireNonNull(activities, "The activities must not be null."));
  }

  static List<WorkingDay> of(List<Activity> activities) {
    var workingDays = new ArrayList<WorkingDay>();
    for (var activity : activities) {
      var index = -1;
      for (var i = 0; i < workingDays.size(); i++) {
        var e = workingDays.get(i);
        if (e.date().equals(activity.timestamp().toLocalDate())) {
          index = i;
          break;
        }
      }

      if (index != -1) {
        var day = workingDays.get(index);

        var list = new ArrayList<>(day.activities());
        list.add(activity);
        Collections.sort(list);

        day = new WorkingDay(day.date(), list);
        workingDays.set(index, day);
      } else {
        var day = new WorkingDay(activity.timestamp().toLocalDate(), List.of(activity));
        workingDays.add(day);
      }
    }
    Collections.sort(workingDays);
    return workingDays;
  }

  @Override
  public int compareTo(WorkingDay other) {
    return Comparator.comparing(WorkingDay::date).reversed().compare(this, other);
  }
}
