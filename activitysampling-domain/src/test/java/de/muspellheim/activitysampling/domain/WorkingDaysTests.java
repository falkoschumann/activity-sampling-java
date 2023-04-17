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

class WorkingDaysTests {
  @Test
  void oneDayAndOneActivity() {
    var now = LocalDateTime.now();
    var activity = new Activity(now, Duration.ofMinutes(15), "a");

    var workingDays = WorkingDay.of(List.of(activity));

    assertEquals(workingDays, List.of(new WorkingDay(now.toLocalDate(), List.of(activity))));
  }

  @Test
  void oneDayAndTwoActivities_SortsActivitiesByTimeDescending() {
    var today = LocalDate.now();
    var a = new Activity(LocalDateTime.of(today, LocalTime.of(11, 0)), Duration.ofMinutes(20), "a");
    var b = new Activity(LocalDateTime.of(today, LocalTime.of(12, 0)), Duration.ofMinutes(20), "a");

    var workingDays = WorkingDay.of(List.of(a, b));

    assertEquals(workingDays, List.of(new WorkingDay(today, List.of(b, a))));
  }

  @Test
  void twoDays_SortsDateDescending() {
    var today = LocalDate.now();
    var yesterday = today.minusDays(1);
    var a =
        new Activity(LocalDateTime.of(yesterday, LocalTime.of(12, 0)), Duration.ofMinutes(20), "a");
    var b = new Activity(LocalDateTime.of(today, LocalTime.of(12, 0)), Duration.ofMinutes(20), "a");

    var workingDays = WorkingDay.of(List.of(a, b));

    assertEquals(
        workingDays,
        List.of(new WorkingDay(today, List.of(b)), new WorkingDay(yesterday, List.of(a))));
  }
}
