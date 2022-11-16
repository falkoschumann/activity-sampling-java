package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record Activity(LocalDateTime timestamp, String description) {
  public Activity {
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(description, "description");
  }
}
