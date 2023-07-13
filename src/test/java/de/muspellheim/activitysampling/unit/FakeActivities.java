/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import de.muspellheim.activitysampling.domain.Activities;
import de.muspellheim.activitysampling.domain.Activity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class FakeActivities extends ArrayList<Activity> implements Activities {

  @Override
  public List<Activity> findInPeriod(LocalDate from, LocalDate to) {
    return stream().filter(a -> isBetween(a.timestamp().toLocalDate(), from, to)).toList();
  }

  private static boolean isBetween(LocalDate date, LocalDate from, LocalDate to) {
    return !date.isBefore(from) && !date.isAfter(to);
  }

  @Override
  public void append(Activity activity) {
    add(activity);
  }
}
