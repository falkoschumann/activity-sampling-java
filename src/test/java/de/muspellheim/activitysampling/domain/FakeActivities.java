/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class FakeActivities extends ArrayList<Activity> implements Activities {
  @Serial private static final long serialVersionUID = 0;

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
