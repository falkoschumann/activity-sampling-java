/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class EventEmitter<T> {
  private final List<Consumer<T>> listeners = new CopyOnWriteArrayList<>();

  public void addListener(Consumer<T> listener) {
    Objects.requireNonNull(listener, "listener");
    listeners.add(listener);
  }

  public void removeListener(Consumer<T> listener) {
    Objects.requireNonNull(listener, "listener");
    listeners.remove(listener);
  }

  public void emit(T value) {
    listeners.forEach(l -> l.accept(value));
  }
}
