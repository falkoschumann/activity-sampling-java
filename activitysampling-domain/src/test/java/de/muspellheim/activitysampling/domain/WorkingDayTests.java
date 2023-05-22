/*
 * Activity Sampling - Domain
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
  void oneDayWithOneActivity() {
    var now = LocalDateTime.now();
    var activity = new Activity(now, Duration.ofMinutes(15), "a");

    var days = WorkingDay.of(List.of(activity));

    assertEquals(days, List.of(new WorkingDay(now.toLocalDate(), List.of(activity))));
  }

  @Test
  void oneDayWithTwoActivities_SortsActivitiesByTimeDescending() {
    var today = LocalDate.now();
    var a = new Activity(LocalDateTime.of(today, LocalTime.of(11, 0)), Duration.ofMinutes(20), "a");
    var b = new Activity(LocalDateTime.of(today, LocalTime.of(12, 0)), Duration.ofMinutes(20), "a");

    var days = WorkingDay.of(List.of(a, b));

    assertEquals(days, List.of(new WorkingDay(today, List.of(b, a))));
  }

  @Test
  void twoDays_SortsDatesDescending() {
    var today = LocalDate.now();
    var yesterday = today.minusDays(1);
    var a =
        new Activity(LocalDateTime.of(yesterday, LocalTime.of(12, 0)), Duration.ofMinutes(20), "a");
    var b = new Activity(LocalDateTime.of(today, LocalTime.of(12, 0)), Duration.ofMinutes(20), "a");

    var days = WorkingDay.of(List.of(a, b));

    assertEquals(
        days, List.of(new WorkingDay(today, List.of(b)), new WorkingDay(yesterday, List.of(a))));
  }
}
