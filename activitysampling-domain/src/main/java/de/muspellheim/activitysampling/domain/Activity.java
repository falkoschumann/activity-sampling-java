package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record Activity(LocalDateTime timestamp, Duration duration, String description) {
  public Activity {
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(duration, "duration");
    Objects.requireNonNull(description, "description");
  }
}
