/*
 * Muspellheim - Utilities
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.utilities;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ConfigurableResponses<T> {
  private final Object response;

  public ConfigurableResponses(T value) {
    response = value;
  }

  public ConfigurableResponses(Exception throwing) {
    response = throwing;
  }

  public ConfigurableResponses(List<?> responses) {
    response = new LinkedList<>(responses);
  }

  public static <T> ConfigurableResponses<T> empty() {
    return new ConfigurableResponses<>(List.of());
  }

  @SuppressWarnings("unchecked")
  public T next() {
    if (response instanceof Queue<?> q) {
      var v = q.poll();
      if (v == null) {
        throw new IllegalStateException("No more values configured.");
      } else if (v instanceof RuntimeException e) {
        throw e;
      } else {
        return (T) v;
      }
    } else if (response instanceof RuntimeException e) {
      throw e;
    } else {
      return (T) response;
    }
  }

  @SuppressWarnings("unchecked")
  public T tryNext() throws Exception {
    if (response instanceof Queue<?> q) {
      var v = q.poll();
      if (v == null) {
        throw new IllegalStateException("No more values configured.");
      } else if (v instanceof Exception e) {
        throw e;
      } else {
        return (T) v;
      }
    } else if (response instanceof Exception e) {
      throw e;
    } else {
      return (T) response;
    }
  }
}
