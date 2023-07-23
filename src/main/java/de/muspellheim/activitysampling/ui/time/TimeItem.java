/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.time;

import java.util.Objects;

public record TimeItem(String client, String hours) {
  public TimeItem {
    Objects.requireNonNull(client, "The client must not be null.");
    Objects.requireNonNull(hours, "The hours must not be null.");
  }
}
