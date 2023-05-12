/*
 * Muspellheim - Common
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ConfigurableResponses<T> {
  private final Object response;

  private ConfigurableResponses(Object value) {
    response = value;
  }

  public static <T> ConfigurableResponses<T> empty() {
    return sequence(List.of());
  }

  public static <T> ConfigurableResponses<T> always(T value) {
    return new ConfigurableResponses<>(value);
  }

  public static ConfigurableResponses<?> always(Exception exception) {
    return new ConfigurableResponses<>(exception);
  }

  public static <T> ConfigurableResponses<T> sequence(List<?> values) {
    return new ConfigurableResponses<>(new LinkedList<>(values));
  }

  public static <T> ConfigurableResponses<T> sequence(Object... values) {
    return sequence(List.of(values));
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
