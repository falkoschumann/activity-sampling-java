package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record ActivityLoggedEvent(Instant timestamp, Duration duration, String description)
    implements Event {
  public ActivityLoggedEvent {
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(duration, "duration");
    Objects.requireNonNull(description, "description");
  }
}
