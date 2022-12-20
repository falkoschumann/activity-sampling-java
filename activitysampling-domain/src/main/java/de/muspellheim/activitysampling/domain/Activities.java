/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public interface Activities {
  List<Activity> findInPeriod(LocalDate from, LocalDate to);

  void append(Activity activity);
}
