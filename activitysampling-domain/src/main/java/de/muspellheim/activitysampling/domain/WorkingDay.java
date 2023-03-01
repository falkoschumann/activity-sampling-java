/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record WorkingDay(LocalDate date, List<Activity> activities) {
  public WorkingDay {
    Objects.requireNonNull(date, "date");
    activities = List.copyOf(Objects.requireNonNull(activities, "activities"));
  }
}
