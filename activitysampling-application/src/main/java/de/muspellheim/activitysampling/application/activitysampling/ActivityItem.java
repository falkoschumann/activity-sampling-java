/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import java.util.Objects;

public record ActivityItem(String text, String description) {
  public ActivityItem {
    Objects.requireNonNull(text, "text");
  }

  public ActivityItem(String text) {
    this(text, null);
  }

  public boolean isActivity() {
    return description != null;
  }
}
