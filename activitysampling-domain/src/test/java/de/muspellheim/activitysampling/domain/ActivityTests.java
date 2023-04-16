/*
 * Activity Sampling - Domain
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ActivityTests {
  @Test
  void durationMustNotBeNegative() {
    var exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Activity(LocalDateTime.now(), Duration.ofMinutes(-5), "xyz"));
    assertTrue(exception.getMessage().contains("duration"));
  }

  @Test
  void descriptionMustNotBeEmpty() {
    var exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Activity(LocalDateTime.now(), Duration.ofMinutes(5), ""));
    assertTrue(exception.getMessage().contains("description"));
  }
}
