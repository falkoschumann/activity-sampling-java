/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.shared;

import java.util.ArrayList;
import java.util.List;

public class Exceptions {
  public String summarizeMessages(String errorMessage, Throwable cause) {
    List<String> messages = collectExceptionMessages(errorMessage, cause);
    return String.join(" ", messages);
  }

  public static List<String> collectExceptionMessages(String errorMessage, Throwable cause) {
    if (cause == null) {
      return List.of(errorMessage);
    }

    var messages = new ArrayList<String>();
    messages.add(errorMessage);
    var causeMessages = collectExceptionMessages(cause.getMessage(), cause.getCause());
    messages.addAll(causeMessages);
    return messages;
  }
}
