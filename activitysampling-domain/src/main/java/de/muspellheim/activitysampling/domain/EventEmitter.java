/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventEmitter<T> {
  private final List<Consumer<T>> listeners = new CopyOnWriteArrayList<>();

  public EventEmitter() {}

  public void addListener(Consumer<T> listener) {
    Objects.requireNonNull(listener, "listener");
    listeners.add(listener);
  }

  public void removeListener(Consumer<T> listener) {
    Objects.requireNonNull(listener, "listener");
    listeners.remove(listener);
  }

  public int listenerCount() {
    return listeners.size();
  }

  public void emit(T value) {
    listeners.forEach(l -> l.accept(value));
  }
}
