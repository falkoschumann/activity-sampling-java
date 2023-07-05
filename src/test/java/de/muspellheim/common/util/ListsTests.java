/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ListsTests {
  @Test
  void indexOf_ItemFound_ReturnsIndex() {
    var list = List.of("foo", "bar");

    var index = Lists.indexOf(list, v -> v.startsWith("b"));

    assertEquals(1, index);
  }

  @Test
  void indexOf_ItemDidNotFound_ReturnsNone() {
    var list = List.of("foo", "bar");

    var index = Lists.indexOf(list, v -> v.startsWith("a"));

    assertEquals(-1, index);
  }

  @Test
  void indexOf_EmptyList_ReturnsNone() {
    var list = List.<String>of();

    var index = Lists.indexOf(list, v -> v.startsWith("a"));

    assertEquals(-1, index);
  }
}
