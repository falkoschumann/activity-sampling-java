/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public record Activity(LocalDateTime timestamp, Duration duration, String description)
    implements Comparable<Activity> {
  public Activity {
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(duration, "duration");
    Objects.requireNonNull(description, "description");
  }

  public static Activity parse(String timestamp, String duration, String description) {
    return new Activity(LocalDateTime.parse(timestamp), Duration.parse(duration), description);
  }

  @Override
  public int compareTo(Activity o) {
    return timestamp.compareTo(o.timestamp);
  }
}
