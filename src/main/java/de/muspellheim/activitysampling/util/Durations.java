/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.util;

import java.time.Duration;
import java.time.format.FormatStyle;

public class Durations {

  private Durations() {}

  public static Duration requireNonNegative(Duration d, String message) {
    if (d.isNegative()) {
      throw new IllegalArgumentException(message);
    }
    return d;
  }

  public static String format(Duration duration, FormatStyle style) {
    if (style == FormatStyle.SHORT) {
      return "%1$02d:%2$02d".formatted(duration.toHours(), duration.toMinutesPart());
    } else {
      return "%1$02d:%2$02d:%3$02d"
          .formatted(duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
    }
  }
}
