/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record Activity(LocalDateTime timestamp, Duration duration, String description)
    implements Comparable<Activity> {
  public Activity {
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(duration, "duration");
    Objects.requireNonNull(description, "description");
  }

  @Override
  public int compareTo(Activity o) {
    return timestamp.compareTo(o.timestamp);
  }
}
