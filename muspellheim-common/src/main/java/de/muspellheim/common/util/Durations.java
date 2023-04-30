/*
 * Muspellheim - Common
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import java.time.Duration;

public class Durations {
  private Durations() {}

  public static Duration requireNonNegative(Duration d, String message) {
    if (d.isNegative()) {
      throw new IllegalArgumentException(message);
    }
    return d;
  }
}
