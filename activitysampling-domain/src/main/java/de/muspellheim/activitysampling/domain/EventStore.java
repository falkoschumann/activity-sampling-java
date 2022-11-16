package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;
import java.util.stream.*;

public interface EventStore {
  default void record(Event event) {
    record(List.of(event));
  }

  void record(Iterable<Event> events);

  Stream<Event> replay();

  interface Event {
    Instant timestamp();
  }
}
