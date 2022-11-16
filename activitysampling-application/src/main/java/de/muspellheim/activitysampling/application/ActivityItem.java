package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import java.util.*;

record ActivityItem(String text, Activity activity) {
  ActivityItem {
    Objects.requireNonNull(text, "text");
  }

  ActivityItem(String text) {
    this(text, null);
  }
}
