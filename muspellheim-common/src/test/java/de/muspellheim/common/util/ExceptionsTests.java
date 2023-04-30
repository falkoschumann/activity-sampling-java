/*
 * Muspellheim - Common
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExceptionsTests {
  @Test
  void summarizeMessages() {
    var result =
        Exceptions.summarizeMessages("m1", new IOException("m2", new IllegalStateException()));

    assertEquals("m1 IOException: m2 IllegalStateException: null", result);
  }

  @Test
  void collectExceptionMessages() {
    var result =
        Exceptions.collectExceptionMessages(
            "m1", new IOException("m2", new IllegalStateException()));

    assertEquals(List.of("m1", "IOException: m2", "IllegalStateException: null"), result);
  }
}
