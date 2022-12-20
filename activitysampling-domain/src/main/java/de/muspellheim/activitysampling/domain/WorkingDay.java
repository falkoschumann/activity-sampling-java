/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record WorkingDay(LocalDate date, List<Activity> activities) {
  public WorkingDay {
    Objects.requireNonNull(date, "date");
    Objects.requireNonNull(activities, "activities");
  }
}
