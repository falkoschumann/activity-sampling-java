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
  void noActivity() {
    var today = LocalDate.now();

    var summary = TimeSummary.of(today).add(List.of());

    assertEquals(
        new TimeSummary(today, Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO),
        summary);
  }

  @Test
  void oneActivityToday() {
    var today = LocalDate.of(2022, 12, 17);
    var activity =
        new Activity(
            LocalDateTime.of(today, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar");

    var summary = TimeSummary.of(today).add(List.of(activity));

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(20),
            Duration.ofMinutes(0),
            Duration.ofMinutes(20),
            Duration.ofMinutes(20)),
        summary);
  }

  @Test
  void sumsUpTodaysActivities() {
    var today = LocalDate.of(2022, 12, 16);
    var activity1 =
        new Activity(
            LocalDateTime.of(today, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar");
    var activity2 =
        new Activity(
            LocalDateTime.of(today, LocalTime.of(14, 43)), Duration.ofMinutes(20), "foobar");

    var summary = TimeSummary.of(today).add(List.of(activity1, activity2));

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(40),
            Duration.ofMinutes(0),
            Duration.ofMinutes(40),
            Duration.ofMinutes(40)),
        summary);
  }

  @Test
  void sumsUpYesterdaysActivities() {
    var today = LocalDate.of(2022, 12, 17);
    var yesterday = today.minusDays(1);
    var activity1 =
        new Activity(
            LocalDateTime.of(yesterday, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar");
    var activity2 =
        new Activity(
            LocalDateTime.of(yesterday, LocalTime.of(14, 43)), Duration.ofMinutes(20), "foobar");

    var summary = TimeSummary.of(today).add(List.of(activity1, activity2));

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(0),
            Duration.ofMinutes(40),
            Duration.ofMinutes(40),
            Duration.ofMinutes(40)),
        summary);
  }

  @Test
  void sumsUpActivitiesThisWeek() {
    var thursday = LocalDate.of(2022, 12, 15);
    var monday = LocalDate.of(2022, 12, 12);
    var saturday = LocalDate.of(2022, 12, 17);
    var thursdayActivity =
        new Activity(
            LocalDateTime.of(thursday, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar");
    var mondayActivity =
        new Activity(
            LocalDateTime.of(monday, LocalTime.of(14, 43)), Duration.ofMinutes(20), "foobar");

    var summary = TimeSummary.of(saturday).add(List.of(thursdayActivity, mondayActivity));

    assertEquals(
        new TimeSummary(
            saturday,
            Duration.ofMinutes(0),
            Duration.ofMinutes(0),
            Duration.ofMinutes(40),
            Duration.ofMinutes(40)),
        summary);
  }

  @Test
  void sumsUpActivitiesThisMonth() {
    var firstOfMonth = LocalDate.of(2022, 12, 1);
    var lastWeek = LocalDate.of(2022, 12, 11);
    var today = LocalDate.of(2022, 12, 17);
    var firstOfMonthActivity =
        new Activity(
            LocalDateTime.of(firstOfMonth, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar");
    var lastWeekActivity =
        new Activity(
            LocalDateTime.of(lastWeek, LocalTime.of(14, 43)), Duration.ofMinutes(20), "foobar");

    var summary = TimeSummary.of(today).add(List.of(firstOfMonthActivity, lastWeekActivity));

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(0),
            Duration.ofMinutes(0),
            Duration.ofMinutes(0),
            Duration.ofMinutes(40)),
        summary);
  }

  @Test
  void doesNotSumUpPreviousMonth() {
    var today = LocalDate.of(2022, 12, 17);
    var lastMonth = LocalDate.of(2022, 11, 30);
    var thisMonth = LocalDate.of(2022, 12, 1);
    var lastMonthActivity =
        new Activity(
            LocalDateTime.of(lastMonth, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar");
    var thisMonthActivity =
        new Activity(
            LocalDateTime.of(thisMonth, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar");

    var summary = TimeSummary.of(today).add(List.of(lastMonthActivity, thisMonthActivity));

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(0),
            Duration.ofMinutes(0),
            Duration.ofMinutes(0),
            Duration.ofMinutes(20)),
        summary);
  }

  @Test
  void getTimeSummary_DoesNotSumFuture() {
    var today = LocalDate.of(2022, 11, 17);
    LocalDate tomorrow = LocalDate.of(2022, 11, 18);
    var todaysActivity =
        new Activity(
            LocalDateTime.of(today, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar");
    var tomorrowsActivity =
        new Activity(
            LocalDateTime.of(tomorrow, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar");

    var summary = TimeSummary.of(today).add(List.of(todaysActivity, tomorrowsActivity));

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(20),
            Duration.ofMinutes(0),
            Duration.ofMinutes(20),
            Duration.ofMinutes(20)),
        summary);
  }
}
