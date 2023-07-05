/*
 * Activity Sampling
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
    var r = nextResponse();
    if (r instanceof RuntimeException e) {
      throw e;
    }
    return (T) r;
  }

  @SuppressWarnings("unchecked")
  public T tryNext() throws Exception {
    var r = nextResponse();
    if (r instanceof Exception e) {
      throw e;
    }
    return (T) r;
  }

  private Object nextResponse() {
    if (!(response instanceof Queue<?> q)) {
      return response;
    }

    var r = q.poll();
    if (r == null) {
      throw new IllegalStateException("No more values configured.");
    }
    return r;
  }
}
