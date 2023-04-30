/*
 * Muspellheim - Common
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StringsTests {
  @Test
  void requireNonBlank() {
    var result = Strings.requireNonBlank("not blank", "error message");

    assertEquals("not blank", result);
  }

  @Test
  void requireNonBlank_Empty_ThrowsException() {
    var exception =
        assertThrows(
            IllegalArgumentException.class, () -> Strings.requireNonBlank("", "error message"));
    assertEquals("error message", exception.getMessage());
  }
}
