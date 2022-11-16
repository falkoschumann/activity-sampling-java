package de.muspellheim.activitysampling.application;

import java.util.*;

public record ActivityItem(String text, boolean header) {
  public ActivityItem {
    Objects.requireNonNull(text, "text");
  }
}
