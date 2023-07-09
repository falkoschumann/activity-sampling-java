/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.TimeSummary;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class TimeSummaryTests {
  @Test
  void from_MultipleActivitiesToday_SumsHours() {
    var today = LocalDate.now();
    var activities =
        List.of(
            newActivity(today, Duration.ofMinutes(5)), newActivity(today, Duration.ofMinutes(10)));

    var summary = TimeSummary.from(today, activities);

    assertEquals(
        TimeSummary.builder()
            .hoursToday(Duration.ofMinutes(15))
            .hoursYesterday(Duration.ZERO)
            .hoursThisWeek(Duration.ofMinutes(15))
            .hoursThisMonth(Duration.ofMinutes(15))
            .build(),
        summary);
  }

  @Test
  void from_MultipleActivitiesYesterday_SumsHours() {
    var today = LocalDate.of(2023, 4, 7);
    var yesterday = today.minusDays(1);
    var activities =
        List.of(
            newActivity(today, Duration.ofMinutes(5)),
            newActivity(yesterday, Duration.ofMinutes(12)),
            newActivity(yesterday, Duration.ofMinutes(20)));

    var summary = TimeSummary.from(today, activities);

    assertEquals(
        TimeSummary.builder()
            .hoursToday(Duration.ofMinutes(5))
            .hoursYesterday(Duration.ofMinutes(32))
            .hoursThisWeek(Duration.ofMinutes(37))
            .hoursThisMonth(Duration.ofMinutes(37))
            .build(),
        summary);
  }

  @Test
  void from_MultipleActivitiesThisWeek_SumsHours() {
    var thursday = LocalDate.of(2023, 4, 6);
    var wednesday = LocalDate.of(2023, 4, 5);
    var monday = LocalDate.of(2023, 4, 3);
    var activities =
        List.of(
            newActivity(monday, Duration.ofMinutes(20)),
            newActivity(wednesday, Duration.ofMinutes(12)),
            newActivity(thursday, Duration.ofMinutes(8)));

    var summary = TimeSummary.from(thursday, activities);

    assertEquals(
        TimeSummary.builder()
            .hoursToday(Duration.ofMinutes(8))
            .hoursYesterday(Duration.ofMinutes(12))
            .hoursThisWeek(Duration.ofMinutes(40))
            .hoursThisMonth(Duration.ofMinutes(40))
            .build(),
        summary);
  }

  @Test
  void from_MultipleActivitiesThisMonth_SumsHours() {
    var day24 = LocalDate.of(2023, 3, 24);
    var day23 = LocalDate.of(2023, 3, 23);
    var day22SameWeek = LocalDate.of(2023, 3, 22);
    var day1 = LocalDate.of(2023, 3, 1);
    var activities =
        List.of(
            newActivity(day1, Duration.ofMinutes(30)),
            newActivity(day22SameWeek, Duration.ofMinutes(15)),
            newActivity(day23, Duration.ofMinutes(8)),
            newActivity(day24, Duration.ofMinutes(12)));

    var summary = TimeSummary.from(day24, activities);

    assertEquals(
        TimeSummary.builder()
            .hoursToday(Duration.ofMinutes(12))
            .hoursYesterday(Duration.ofMinutes(8))
            .hoursThisWeek(Duration.ofMinutes(35))
            .hoursThisMonth(Duration.ofMinutes(65))
            .build(),
        summary);
  }

  @Test
  void from_IgnoresTheLastMonth() {
    var thisMonthDay1 = LocalDate.of(2023, 4, 1);
    var lastMonthDay31 = LocalDate.of(2023, 3, 31);
    var activities =
        List.of(
            newActivity(lastMonthDay31, Duration.ofMinutes(8)),
            newActivity(thisMonthDay1, Duration.ofMinutes(12)));

    var summary = TimeSummary.from(thisMonthDay1, activities);

    assertEquals(
        TimeSummary.builder()
            .hoursToday(Duration.ofMinutes(12))
            .hoursYesterday(Duration.ofMinutes(8))
            .hoursThisWeek(Duration.ofMinutes(20))
            .hoursThisMonth(Duration.ofMinutes(12))
            .build(),
        summary);
  }

  @Test
  void from_WeekOverlapsEndOfYear_SumsUpTheWholeWeek() {
    var monday = LocalDate.of(2022, 12, 26);
    var saturday = LocalDate.of(2022, 12, 31);
    var sunday = LocalDate.of(2023, 1, 1);
    var activities =
        List.of(
            newActivity(monday, Duration.ofMinutes(20)),
            newActivity(saturday, Duration.ofMinutes(12)),
            newActivity(sunday, Duration.ofMinutes(8)));

    var summary = TimeSummary.from(sunday, activities);

    assertEquals(
        TimeSummary.builder()
            .hoursToday(Duration.ofMinutes(8))
            .hoursYesterday(Duration.ofMinutes(12))
            .hoursThisWeek(Duration.ofMinutes(40))
            .hoursThisMonth(Duration.ofMinutes(8))
            .build(),
        summary);
  }

  @Test
  void from_IgnoresLastWeek() {
    var sunday = LocalDate.of(2023, 6, 4);
    var monday = LocalDate.of(2023, 6, 5);
    var activities =
        List.of(
            newActivity(sunday, Duration.ofMinutes(8)),
            newActivity(monday, Duration.ofMinutes(12)));

    var summary = TimeSummary.from(monday, activities);

    assertEquals(
        TimeSummary.builder()
            .hoursToday(Duration.ofMinutes(12))
            .hoursYesterday(Duration.ofMinutes(8))
            .hoursThisWeek(Duration.ofMinutes(12))
            .hoursThisMonth(Duration.ofMinutes(20))
            .build(),
        summary);
  }

  @Test
  void from_ActivityTomorrow_IgnoresTheFuture() {
    var today = LocalDate.now();
    var tomorrow = today.plusDays(1);
    var activities =
        List.of(
            newActivity(today, Duration.ofMinutes(5)),
            newActivity(tomorrow, Duration.ofMinutes(10)));

    var summary = TimeSummary.from(today, activities);

    assertEquals(
        TimeSummary.builder()
            .hoursToday(Duration.ofMinutes(5))
            .hoursYesterday(Duration.ZERO)
            .hoursThisWeek(Duration.ofMinutes(5))
            .hoursThisMonth(Duration.ofMinutes(5))
            .build(),
        summary);
  }

  private static Activity newActivity(LocalDate date, Duration duration) {
    return new Activity(
        LocalDateTime.of(date, LocalTime.of(12, 0)), duration, "client", "project", "notes");
  }
}
