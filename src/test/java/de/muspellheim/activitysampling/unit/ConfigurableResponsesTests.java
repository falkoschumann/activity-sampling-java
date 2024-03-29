/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.muspellheim.activitysampling.util.ConfigurableResponses;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class ConfigurableResponsesTests {
  @Test
  void next_Empty_ThrowsException() {
    var responses = ConfigurableResponses.empty();

    assertThrows(IllegalStateException.class, responses::next);
  }

  @Test
  void next_Null_ReturnsAlwaysNull() {
    var responses = ConfigurableResponses.always(null);

    assertNull(responses.next());
    assertNull(responses.next());
    assertNull(responses.next());
  }

  @Test
  void next_Value_ReturnsAlwaysThisValue() {
    var responses = ConfigurableResponses.always("foo");

    assertEquals("foo", responses.next());
    assertEquals("foo", responses.next());
    assertEquals("foo", responses.next());
  }

  @Test
  void next_Exception_ThrowsAlwaysThisException() {
    var responses = ConfigurableResponses.always(new ArithmeticException());

    assertThrows(ArithmeticException.class, responses::next);
    assertThrows(ArithmeticException.class, responses::next);
    assertThrows(ArithmeticException.class, responses::next);
  }

  @Test
  void next_Sequence_ReturnsValueOrThrowsExceptionInThisOrder() {
    var responses = ConfigurableResponses.sequence(new ArithmeticException(), "foo");

    assertThrows(ArithmeticException.class, responses::next);
    assertEquals("foo", responses.next());
    assertThrows(IllegalStateException.class, responses::next);
  }

  @Test
  void tryNext_Empty_ThrowsException() {
    var responses = ConfigurableResponses.empty();

    assertThrows(IllegalStateException.class, responses::tryNext);
  }

  @Test
  void tryNext_Null_ReturnsAlwaysNull() throws Exception {
    var responses = ConfigurableResponses.always(null);

    assertNull(responses.tryNext());
    assertNull(responses.tryNext());
    assertNull(responses.tryNext());
  }

  @Test
  void tryNext_Value_ReturnsAlwaysThisValue() throws Exception {
    var responses = ConfigurableResponses.always("foo");

    assertEquals("foo", responses.tryNext());
    assertEquals("foo", responses.tryNext());
    assertEquals("foo", responses.tryNext());
  }

  @Test
  void tryNext_Exception_ThrowsAlwaysThisException() {
    var responses = ConfigurableResponses.always(new IOException());

    assertThrows(IOException.class, responses::tryNext);
    assertThrows(IOException.class, responses::tryNext);
    assertThrows(IOException.class, responses::tryNext);
  }

  @Test
  void tryNext_Sequence_ReturnsValueOrThrowsExceptionInThisOrder() throws Exception {
    var responses = ConfigurableResponses.sequence(new IOException(), "foo");

    assertThrows(IOException.class, responses::tryNext);
    assertEquals("foo", responses.tryNext());
    assertThrows(IllegalStateException.class, responses::tryNext);
  }
}
