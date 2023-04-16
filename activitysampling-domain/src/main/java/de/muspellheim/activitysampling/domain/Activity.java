/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

public record Activity(LocalDateTime timestamp, Duration duration, String description)
    implements Comparable<Activity> {
  public Activity {
    Objects.requireNonNull(timestamp, "The timestamp must not be null.");
    Objects.requireNonNull(duration, "The duration must not be null.");
    Objects.requireNonNull(description, "The description must be null.");

    if (duration.isNegative()) {
      throw new IllegalArgumentException(
          "The duration cannot be negative: %s.".formatted(duration));
    }
    if (description.isBlank()) {
      throw new IllegalArgumentException("The description cannot be empty.");
    }
  }

  @Override
  public int compareTo(Activity other) {
    return Comparator.comparing(Activity::timestamp).reversed().compare(this, other);
  }
}
