/*
 * Muspellheim - Common
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.common.util;

import java.util.List;
import java.util.function.Predicate;

public class Lists {
  private Lists() {}

  public static <T> int indexOf(List<T> list, Predicate<T> predicate) {
    for (var i = 0; i < list.size(); i++) {
      var v = list.get(i);
      if (predicate.test(v)) {
        return i;
      }
    }
    return -1;
  }
}
