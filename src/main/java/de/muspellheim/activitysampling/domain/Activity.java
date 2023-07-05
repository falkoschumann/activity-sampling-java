/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import de.muspellheim.common.util.Durations;
import de.muspellheim.common.util.Strings;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public record Activity(
    LocalDateTime timestamp, Duration duration, String client, String project, String notes) {

  public Activity {
    Objects.requireNonNull(timestamp, "The timestamp is null.");
    Objects.requireNonNull(duration, "The duration is null.");
    Durations.requireNonNegative(duration, "The duration is negative: %s.".formatted(duration));
    Objects.requireNonNull(client, "The client is null.");
    Strings.requireNonBlank(client, "The client is blank.");
    Objects.requireNonNull(project, "The project is null.");
    Strings.requireNonBlank(project, "The project is blank.");
    Objects.requireNonNull(notes, "The notes is null.");
    Strings.requireNonBlank(notes, "The notes is blank.");
  }
}
