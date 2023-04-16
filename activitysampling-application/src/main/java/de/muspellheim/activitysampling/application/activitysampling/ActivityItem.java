/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import java.util.Objects;

record ActivityItem(String text, String description) {
  public ActivityItem {
    Objects.requireNonNull(text, "The text must not be null.");
  }

  ActivityItem(String text) {
    this(text, null);
  }

  boolean isActivity() {
    return description != null;
  }
}
