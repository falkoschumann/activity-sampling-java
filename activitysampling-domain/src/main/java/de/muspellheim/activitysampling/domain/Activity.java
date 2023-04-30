/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import de.muspellheim.common.util.Durations;
import de.muspellheim.common.util.Strings;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public record Activity(LocalDateTime timestamp, Duration duration, String description) {
  public Activity {
    Objects.requireNonNull(timestamp, "The timestamp is null.");
    Objects.requireNonNull(duration, "The duration is null.");
    Durations.requireNonNegative(duration, "The duration is negative: %s.".formatted(duration));
    Objects.requireNonNull(description, "The description is null.");
    Strings.requireNonBlank(description, "The description is blank.");
  }
}
