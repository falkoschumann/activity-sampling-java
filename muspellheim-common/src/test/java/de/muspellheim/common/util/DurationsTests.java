/*
 * Muspellheim - Common
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class DurationsTests {
  @Test
  void requireNonNegative() {
    var result = Durations.requireNonNegative(Duration.ofMinutes(5), "error message");

    assertEquals(Duration.ofMinutes(5), result);
  }

  @Test
  void requireNonNegativ_Negative_ThrowsException() {
    var exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> Durations.requireNonNegative(Duration.ofMinutes(-3), "error message"));
    assertEquals("error message", exception.getMessage());
  }
}
