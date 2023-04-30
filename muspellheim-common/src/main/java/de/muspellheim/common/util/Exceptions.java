/*
 * Muspellheim - Common
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import java.util.ArrayList;
import java.util.List;

public class Exceptions {
  private Exceptions() {}

  public static String summarizeMessages(String errorMessage, Throwable cause) {
    List<String> messages = collectExceptionMessages(errorMessage, cause);
    return String.join(" ", messages);
  }

  public static List<String> collectExceptionMessages(String errorMessage, Throwable cause) {
    if (cause == null) {
      return List.of(errorMessage);
    }

    var messages = new ArrayList<String>();
    messages.add(errorMessage);
    var message = "%s: %s".formatted(cause.getClass().getSimpleName(), cause.getMessage());
    var causeMessages = collectExceptionMessages(message, cause.getCause());
    messages.addAll(causeMessages);
    return List.copyOf(messages);
  }
}
