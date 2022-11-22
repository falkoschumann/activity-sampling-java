package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record Activity(LocalTime time, String description) {
  public Activity {
    Objects.requireNonNull(time, "time");
    Objects.requireNonNull(description, "description");
  }
}
