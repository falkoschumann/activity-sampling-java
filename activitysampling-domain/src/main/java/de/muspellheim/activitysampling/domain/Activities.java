/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.LocalDate;
import java.util.List;

public interface Activities {
  List<Activity> findInPeriod(LocalDate from, LocalDate to);

  void append(Activity activity);
}
