package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record ActivityLoggedEvent(Instant timestamp, String description)
    implements EventStore.Event {
  public ActivityLoggedEvent {
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(description, "description");
  }
}
