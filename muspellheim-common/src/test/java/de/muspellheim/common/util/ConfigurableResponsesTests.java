/*
 * Muspellheim - Common
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ConfigurableResponsesTests {
  @Test
  void next_SingleNullResponse_ReturnsAlwaysNull() {
    var responses = new ConfigurableResponses<>((String) null);

    assertNull(responses.next());
    assertNull(responses.next());
    assertNull(responses.next());
  }

  @Test
  void next_SingleValueResponse_ReturnsAlwaysThisValue() {
    var responses = new ConfigurableResponses<>("foo");

    assertEquals("foo", responses.next());
    assertEquals("foo", responses.next());
    assertEquals("foo", responses.next());
  }

  @Test
  void next_SingleExceptionResponse_ThrowsAlwaysThisException() {
    var responses = new ConfigurableResponses<>(new ArithmeticException());

    assertThrows(ArithmeticException.class, responses::next);
    assertThrows(ArithmeticException.class, responses::next);
    assertThrows(ArithmeticException.class, responses::next);
  }
}
