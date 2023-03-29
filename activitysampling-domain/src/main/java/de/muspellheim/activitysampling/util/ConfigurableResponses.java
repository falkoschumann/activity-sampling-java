/*
 * Activity Sampling - Domain
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.util;

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

  @SuppressWarnings("unchecked")
  public T next() {
    if (response instanceof Queue<?> q) {
      var v = q.poll();
      if (v == null) {
        throw new IllegalStateException("No more values configured.");
      } else if (response instanceof RuntimeException e) {
        throw e;
      } else {
        return (T) response;
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
      } else if (response instanceof Exception e) {
        throw e;
      } else {
        return (T) response;
      }
    } else if (response instanceof Exception e) {
      throw e;
    } else {
      return (T) response;
    }
  }
}
