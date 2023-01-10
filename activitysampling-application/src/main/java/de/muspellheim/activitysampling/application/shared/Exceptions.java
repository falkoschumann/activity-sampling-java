/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.shared;

import java.util.ArrayList;
import java.util.List;

public class Exceptions {
  public static List<String> collectExceptionMessages(String errorMessage, Throwable cause) {
    if (cause == null) {
      return List.of(errorMessage);
    }

    var messages = new ArrayList<String>();
    messages.add(errorMessage);
    List<String> causeMessages = collectExceptionMessages(cause.getMessage(), cause.getCause());
    messages.addAll(causeMessages);
    return messages;
  }
}
