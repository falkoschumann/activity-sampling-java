/*
 * Muspellheim - Utilities
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OutputTracker<T> {
  private final List<T> data = new ArrayList<>();
  private final Consumer<T> tracker = data::add;
  private final EventEmitter<T> emitter;

  public OutputTracker(EventEmitter<T> emitter) {
    this.emitter = emitter;

    emitter.addListener(tracker);
  }

  public List<T> data() {
    return List.copyOf(data);
  }

  public List<T> clear() {
    var result = data();
    data.clear();
    return result;
  }

  public void stop() {
    emitter.removeListener(tracker);
  }
}
