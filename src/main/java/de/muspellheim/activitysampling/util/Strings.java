/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.util;

public class Strings {
  private Strings() {}

  public static String requireNonBlank(String s, String message) {
    if (s.isBlank()) {
      throw new IllegalArgumentException(message);
    }
    return s;
  }
}
