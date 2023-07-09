/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Exceptions {
  private Exceptions() {}

  public static List<Throwable> collect(Throwable exception) {
    if (exception == null) {
      return List.of();
    }

    var exceptions = new ArrayList<Throwable>();
    exceptions.add(exception);
    exceptions.addAll(collect(exception.getCause()));
    return List.copyOf(exceptions);
  }

  public static String summarizeMessages(Throwable exception) {
    var messages =
        collect(exception).stream().map(Throwable::getMessage).filter(Objects::nonNull).toList();
    return String.join(" ", messages);
  }
}
