/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class WorkingDayTests {
  @Test
  void of_MultipleActivitiesOnSameDay_SortsActivitiesByTimeDescending() {
    var today = LocalDate.now();
    var a = newActivity(today, LocalTime.of(11, 0));
    var b = newActivity(today, LocalTime.of(12, 0));

    var days = WorkingDay.of(List.of(a, b));

    assertEquals(days, List.of(new WorkingDay(today, List.of(b, a))));
  }

  @Test
  void of_MultipleActivitiesOnDifferentDays_SortsDatesDescending() {
    var today = LocalDate.now();
    var yesterday = today.minusDays(1);
    var a = newActivity(yesterday, LocalTime.of(12, 0));
    var b = newActivity(today, LocalTime.of(12, 0));

    var days = WorkingDay.of(List.of(a, b));

    assertEquals(
        days, List.of(new WorkingDay(today, List.of(b)), new WorkingDay(yesterday, List.of(a))));
  }

  private static Activity newActivity(LocalDate date, LocalTime time) {
    return new Activity(
        LocalDateTime.of(date, time), Duration.ofMinutes(20), "client", "project", "notes");
  }
}
