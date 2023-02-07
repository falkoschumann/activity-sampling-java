/*
 * Activity Sampling - Domain
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class ActivitiesRepositoryStub extends ArrayList<Activity> implements ActivitiesRepository {
  @Serial private static final long serialVersionUID = 0;

  @Override
  public List<Activity> findInPeriod(LocalDate from, LocalDate to) {
    return stream()
        .filter(
            a -> {
              var date = a.timestamp().toLocalDate();
              return !date.isBefore(from) && !date.isAfter(to);
            })
        .toList();
  }

  @Override
  public void append(Activity activity) {
    add(activity);
  }
}
