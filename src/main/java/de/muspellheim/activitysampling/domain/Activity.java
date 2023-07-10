/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import de.muspellheim.activitysampling.util.Durations;
import de.muspellheim.activitysampling.util.Strings;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;

@Builder
public record Activity(
    LocalDateTime timestamp,
    Duration duration,
    String client,
    String project,
    String task,
    String notes) {

  public Activity {
    Objects.requireNonNull(timestamp, "The timestamp cannot be null.");
    Objects.requireNonNull(duration, "The duration cannot be null.");
    Durations.requireNonNegative(
        duration, "The duration cannot be negative: %s.".formatted(duration));
    Objects.requireNonNull(client, "The client cannot be null.");
    Strings.requireNonBlank(client, "The client cannot be blank.");
    Objects.requireNonNull(project, "The project cannot be null.");
    Strings.requireNonBlank(project, "The project cannot be blank.");
    Objects.requireNonNull(task, "The task cannot be null.");
    Strings.requireNonBlank(task, "The task cannot be blank.");
  }
}
