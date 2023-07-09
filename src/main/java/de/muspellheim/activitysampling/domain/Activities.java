/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.LocalDate;
import java.util.List;

public interface Activities {
  void append(Activity activity);

  List<Activity> findInPeriod(LocalDate from, LocalDate to);
}
