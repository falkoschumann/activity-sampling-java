/*
 * Muspellheim - Common
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.format.FormatStyle;
import org.junit.jupiter.api.Test;

class DurationsTests {
  @Test
  void requireNonNegative_Positive_ReturnsValue() {
    var result = Durations.requireNonNegative(Duration.ofMinutes(5), "error message");

    assertEquals(Duration.ofMinutes(5), result);
  }

  @Test
  void requireNonNegative_Zero_ReturnsValue() {
    var result = Durations.requireNonNegative(Duration.ofMinutes(0), "error message");

    assertEquals(Duration.ofMinutes(0), result);
  }

  @Test
  void requireNonNegativ_Negative_ThrowsException() {
    var exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> Durations.requireNonNegative(Duration.ofMinutes(-3), "error message"));
    assertEquals("error message", exception.getMessage());
  }

  @Test
  void format_Short_hhmm() {
    var text =
        Durations.format(Duration.ofHours(47).plusMinutes(11).plusSeconds(59), FormatStyle.SHORT);

    assertEquals("47:11", text);
  }

  @Test
  void format_Medium_hhmm() {
    var text =
        Durations.format(Duration.ofHours(1).plusMinutes(23).plusSeconds(45), FormatStyle.MEDIUM);

    assertEquals("01:23:45", text);
  }
}
