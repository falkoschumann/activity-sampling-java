/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import de.muspellheim.common.util.Lists;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record WorkingDay(LocalDate date, List<Activity> activities) {
  public WorkingDay {
    Objects.requireNonNull(date, "The date must not be null.");
    activities =
        List.copyOf(Objects.requireNonNull(activities, "The activities must not be null."));
  }

  public static List<WorkingDay> of(List<Activity> activities) {
    var workingDays = new ArrayList<WorkingDay>();
    for (var activity : activities) {
      var date = activity.timestamp().toLocalDate();
      var index = Lists.indexOf(workingDays, d -> d.date().equals(date));
      if (index == -1) {
        var day = new WorkingDay(date, List.of(activity));
        workingDays.add(day);
        workingDays.sort(Comparator.comparing(WorkingDay::date).reversed());
      } else {
        var workingDay = workingDays.get(index);

        var list = new ArrayList<>(workingDay.activities());
        list.add(activity);
        list.sort(Comparator.comparing(Activity::timestamp).reversed());

        workingDay = new WorkingDay(date, list);
        workingDays.set(index, workingDay);
      }
    }
    return workingDays;
  }
}
