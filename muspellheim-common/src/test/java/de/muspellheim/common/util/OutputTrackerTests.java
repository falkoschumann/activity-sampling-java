/*
 * Muspellheim - Common
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class OutputTrackerTests {
  @Test
  void data() {
    var emitter = new EventEmitter<String>();
    var tracker = new OutputTracker<>(emitter);

    emitter.emit("foo");
    emitter.emit("bar");

    assertEquals(List.of("foo", "bar"), tracker.data());
  }

  @Test
  void clear() {
    var emitter = new EventEmitter<String>();
    var tracker = new OutputTracker<>(emitter);
    emitter.emit("foo");

    var result = tracker.clear();

    assertEquals(List.of("foo"), result);
    assertEquals(List.of(), tracker.data());
  }

  @Test
  void stop() {
    var emitter = new EventEmitter<String>();
    var tracker = new OutputTracker<>(emitter);
    emitter.emit("foo");

    tracker.stop();
    emitter.emit("bar");

    assertEquals(List.of("foo"), tracker.data());
  }
}
