/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record WorkingDay(LocalDate date, List<Activity> activities)
    implements Comparable<WorkingDay> {
  public WorkingDay {
    Objects.requireNonNull(date, "date");
    activities = List.copyOf(Objects.requireNonNull(activities, "activities"));
  }

  @Override
  public int compareTo(WorkingDay other) {
    return Comparator.comparing(WorkingDay::date).reversed().compare(this, other);
  }
}
