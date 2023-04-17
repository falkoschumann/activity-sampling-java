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

class TimeSummaryTests {
  @Test
  void firstDay() {
    var today = LocalDate.now();
    var activities =
        List.of(
            new Activity(LocalDateTime.of(today, LocalTime.of(9, 0)), Duration.ofMinutes(5), "a"),
            new Activity(LocalDateTime.of(today, LocalTime.of(8, 0)), Duration.ofMinutes(10), "b"));

    var summary = TimeSummary.of(today, activities);

    assertEquals(
        new TimeSummary(
            Duration.ofMinutes(15), Duration.ZERO, Duration.ofMinutes(15), Duration.ofMinutes(15)),
        summary);
  }

  @Test
  void secondDay() {
    var friday = LocalDate.of(2023, 4, 7);
    var yesterday = friday.minusDays(1);
    var activities =
        List.of(
            new Activity(
                LocalDateTime.of(yesterday, LocalTime.of(19, 0)), Duration.ofMinutes(20), "a"),
            new Activity(
                LocalDateTime.of(yesterday, LocalTime.of(18, 0)), Duration.ofMinutes(12), "b"),
            new Activity(LocalDateTime.of(friday, LocalTime.of(9, 0)), Duration.ofMinutes(5), "c"));

    var summary = TimeSummary.of(friday, activities);

    assertEquals(
        new TimeSummary(
            Duration.ofMinutes(5),
            Duration.ofMinutes(32),
            Duration.ofMinutes(37),
            Duration.ofMinutes(37)),
        summary);
  }

  @Test
  void oneWeek() {
    var thursday = LocalDate.of(2023, 4, 6);
    var wednesday = LocalDate.of(2023, 4, 5);
    var monday = LocalDate.of(2023, 4, 3);
    var activities =
        List.of(
            new Activity(
                LocalDateTime.of(monday, LocalTime.of(19, 0)), Duration.ofMinutes(20), "a"),
            new Activity(
                LocalDateTime.of(wednesday, LocalTime.of(18, 0)), Duration.ofMinutes(12), "b"),
            new Activity(
                LocalDateTime.of(thursday, LocalTime.of(9, 0)), Duration.ofMinutes(8), "c"));

    var summary = TimeSummary.of(thursday, activities);

    assertEquals(
        new TimeSummary(
            Duration.ofMinutes(8),
            Duration.ofMinutes(12),
            Duration.ofMinutes(40),
            Duration.ofMinutes(40)),
        summary);
  }

  @Test
  void oneMonth() {
    var day24 = LocalDate.of(2023, 3, 24);
    var day23 = LocalDate.of(2023, 3, 23);
    var day22SameWeek = LocalDate.of(2023, 3, 22);
    var day1 = LocalDate.of(2023, 3, 1);
    var activities =
        List.of(
            new Activity(LocalDateTime.of(day1, LocalTime.of(19, 0)), Duration.ofMinutes(30), "a"),
            new Activity(
                LocalDateTime.of(day22SameWeek, LocalTime.of(18, 0)), Duration.ofMinutes(15), "b"),
            new Activity(LocalDateTime.of(day23, LocalTime.of(9, 0)), Duration.ofMinutes(8), "c"),
            new Activity(LocalDateTime.of(day24, LocalTime.of(9, 0)), Duration.ofMinutes(12), "d"));

    var summary = TimeSummary.of(day24, activities);

    assertEquals(
        new TimeSummary(
            Duration.ofMinutes(12),
            Duration.ofMinutes(8),
            Duration.ofMinutes(35),
            Duration.ofMinutes(65)),
        summary);
  }

  @Test
  void ignoreLastMonth() {
    var day1 = LocalDate.of(2023, 4, 1);
    var lastMonthDay31 = LocalDate.of(2023, 3, 31);
    var activities =
        List.of(
            new Activity(
                LocalDateTime.of(lastMonthDay31, LocalTime.of(9, 0)), Duration.ofMinutes(8), "a"),
            new Activity(LocalDateTime.of(day1, LocalTime.of(8, 0)), Duration.ofMinutes(12), "b"));

    var summary = TimeSummary.of(day1, activities);

    assertEquals(
        new TimeSummary(
            Duration.ofMinutes(12),
            Duration.ofMinutes(8),
            Duration.ofMinutes(20),
            Duration.ofMinutes(12)),
        summary);
  }

  @Test
  void ignoreFuture() {
    var today = LocalDate.now();
    var tomorrow = today.plusDays(1);
    var activities =
        List.of(
            new Activity(LocalDateTime.of(today, LocalTime.of(9, 0)), Duration.ofMinutes(5), "a"),
            new Activity(
                LocalDateTime.of(tomorrow, LocalTime.of(8, 0)), Duration.ofMinutes(10), "b"));

    var summary = TimeSummary.of(today, activities);

    assertEquals(
        new TimeSummary(
            Duration.ofMinutes(5), Duration.ZERO, Duration.ofMinutes(5), Duration.ofMinutes(5)),
        summary);
  }
}
