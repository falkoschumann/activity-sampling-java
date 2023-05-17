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
  void collect() {
    var causeCause = new IllegalArgumentException();
    var cause = new IllegalStateException("Cause message.", causeCause);
    var exception = new IOException("Exception message.", cause);
    var result = Exceptions.collect(exception);

    assertEquals(List.of(exception, cause, causeCause), result);
  }

  @Test
  void summarizeMessages() {
    var causeCause = new IllegalArgumentException();
    var cause = new IllegalStateException("Cause message.", causeCause);
    var exception = new IOException("Exception message.", cause);
    var result = Exceptions.summarizeMessages(exception);

    assertEquals("Exception message. Cause message.", result);
  }
}
