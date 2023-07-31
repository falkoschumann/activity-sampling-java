/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.time;

import java.util.Objects;
import lombok.Builder;

@Builder
public record TimeItem(String name, String client, String hours) {
  public TimeItem {
    Objects.requireNonNull(name, "The name must not be null.");
    Objects.requireNonNull(hours, "The hours must not be null.");
  }
}
