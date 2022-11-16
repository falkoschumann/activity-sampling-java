package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import java.util.*;

public record ActivityItem(String text, Activity activity) {
  public ActivityItem {
    Objects.requireNonNull(text, "text");
  }

  public ActivityItem(String text) {
    this(text, null);
  }
}
