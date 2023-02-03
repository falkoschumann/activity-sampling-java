/*
 * Activity Sampling - Domain
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record WorkingDays(List<WorkingDay> workingDays) {
  public WorkingDays {
    workingDays = List.copyOf(Objects.requireNonNull(workingDays, "workingDays"));
  }

  public WorkingDays() {
    this(List.of());
  }

  public static WorkingDays from(List<Activity> activities) {
    return new WorkingDays().add(activities);
  }

  public WorkingDays add(Activity activity) {
    var list = new ArrayList<>(workingDays);

    var index = -1;
    for (var i = 0; i < list.size(); i++) {
      var e = list.get(i);
      if (e.date().equals(activity.timestamp().toLocalDate())) {
        index = i;
        break;
      }
    }

    if (index != -1) {
      var day = list.get(index);
      day = day.add(activity);
      list.set(index, day);
    } else {
      var day = new WorkingDay(activity.timestamp().toLocalDate(), List.of(activity));
      list.add(day);
    }

    list.sort((a1, a2) -> a2.date().compareTo(a1.date()));
    return new WorkingDays(list);
  }

  public WorkingDays add(List<Activity> activities) {
    var days = new WorkingDays(workingDays);
    for (var a : activities) {
      days = days.add(a);
    }
    return days;
  }
}
