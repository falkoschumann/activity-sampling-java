/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExceptionsTests {
  @Test
  void collect_ReturnsListOfExceptionAndCauses() {
    var e3 = new IllegalArgumentException();
    var e2 = new IllegalStateException(e3);
    var e1 = new IOException(e2);
    var result = Exceptions.collect(e1);

    assertEquals(List.of(e1, e2, e3), result);
  }

  @Test
  void summarizeMessages_ReturnsConcatenatedMessagesOfExceptionAndCauses() {
    var e3 = new IllegalArgumentException("e3");
    var e2 = new IllegalStateException("e2", e3);
    var e1 = new IOException("e1", e2);
    var result = Exceptions.summarizeMessages(e1);

    assertEquals("e1 e2 e3", result);
  }
}
