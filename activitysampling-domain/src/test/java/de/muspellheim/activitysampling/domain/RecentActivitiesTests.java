/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class RecentActivitiesTests {
  @Test
  void getWorkingDays_NoActivity_ReturnsEmpty() {
    var sut = new RecentActivities();

    var workingDays = sut.getWorkingDays();

    assertFalse(workingDays.iterator().hasNext());
  }

  @Test
  void getWorkingDays_ReturnsWorkingDayWithActivity() {
    var sut = new RecentActivities();
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 17, 14, 23), Duration.ofMinutes(30), "foobar"));

    var workingDays = sut.getWorkingDays();

    assertEquals(
        List.of(
            new WorkingDay(
                LocalDate.of(2022, 12, 17),
                List.of(
                    new Activity(
                        LocalDateTime.of(2022, 12, 17, 14, 23),
                        Duration.ofMinutes(30),
                        "foobar")))),
        workingDays);
  }

  @Test
  void getWorkingDays_ReturnsActivitiesInReverseOrder() {
    var sut = new RecentActivities();
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 17, 14, 23), Duration.ofMinutes(30), "foobar"));
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 17, 14, 53), Duration.ofMinutes(30), "foobar"));

    var workingDays = sut.getWorkingDays();

    assertEquals(
        List.of(
            new WorkingDay(
                LocalDate.of(2022, 12, 17),
                List.of(
                    new Activity(
                        LocalDateTime.of(2022, 12, 17, 14, 53), Duration.ofMinutes(30), "foobar"),
                    new Activity(
                        LocalDateTime.of(2022, 12, 17, 14, 23),
                        Duration.ofMinutes(30),
                        "foobar")))),
        workingDays);
  }

  @Test
  void getWorkingDays_ReturnsWorkingDaysInReverseOrder() {
    var sut = new RecentActivities();
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 17, 14, 23), Duration.ofMinutes(30), "foobar"));
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 18, 14, 23), Duration.ofMinutes(30), "foobar"));

    var workingDays = sut.getWorkingDays();

    assertEquals(
        List.of(
            new WorkingDay(
                LocalDate.of(2022, 12, 18),
                List.of(
                    new Activity(
                        LocalDateTime.of(2022, 12, 18, 14, 23), Duration.ofMinutes(30), "foobar"))),
            new WorkingDay(
                LocalDate.of(2022, 12, 17),
                List.of(
                    new Activity(
                        LocalDateTime.of(2022, 12, 17, 14, 23),
                        Duration.ofMinutes(30),
                        "foobar")))),
        workingDays);
  }

  @Test
  void getWorkingDays_IgnoresFutureActivities() {
    var sut = new RecentActivities(LocalDate.of(2022, 12, 17));
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 17, 14, 23), Duration.ofMinutes(30), "foobar"));
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 18, 14, 23), Duration.ofMinutes(30), "foobar"));
    var workingDays = sut.getWorkingDays();

    assertEquals(
        List.of(
            new WorkingDay(
                LocalDate.of(2022, 12, 17),
                List.of(
                    new Activity(
                        LocalDateTime.of(2022, 12, 17, 14, 23),
                        Duration.ofMinutes(30),
                        "foobar")))),
        workingDays);
  }

  @Test
  void getTimeSummary_NoActivity_ReturnEmpty() {
    var sut = new RecentActivities();

    var timeSummary = sut.getTimeSummary();

    assertEquals(
        new TimeSummary(
            LocalDate.now(), Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO),
        timeSummary);
  }

  @Test
  void getTimeSummary_1Activity_InitializesTotals() {
    var today = LocalDate.of(2022, 12, 17);
    var sut = new RecentActivities(today);
    sut.apply(
        new Activity(
            LocalDateTime.of(today, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar"));

    var timeSummary = sut.getTimeSummary();

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(20),
            Duration.ofMinutes(0),
            Duration.ofMinutes(20),
            Duration.ofMinutes(20)),
        timeSummary);
  }

  @Test
  void getTimeSummary_SumsToday() {
    var today = LocalDate.of(2022, 12, 16);
    var sut = new RecentActivities(today);
    sut.apply(
        new Activity(
            LocalDateTime.of(today, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar"));
    sut.apply(
        new Activity(
            LocalDateTime.of(today, LocalTime.of(14, 43)), Duration.ofMinutes(20), "foobar"));

    var timeSummary = sut.getTimeSummary();

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(40),
            Duration.ofMinutes(0),
            Duration.ofMinutes(40),
            Duration.ofMinutes(40)),
        timeSummary);
  }

  @Test
  void getTimeSummary_SumsYesterday() {
    var today = LocalDate.of(2022, 12, 17);
    var yesterday = LocalDate.of(2022, 12, 16);
    var sut = new RecentActivities(today);
    sut.apply(
        new Activity(
            LocalDateTime.of(yesterday, LocalTime.of(14, 23)), Duration.ofMinutes(20), "foobar"));
    sut.apply(
        new Activity(
            LocalDateTime.of(yesterday, LocalTime.of(14, 43)), Duration.ofMinutes(20), "foobar"));

    var timeSummary = sut.getTimeSummary();

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(0),
            Duration.ofMinutes(40),
            Duration.ofMinutes(40),
            Duration.ofMinutes(40)),
        timeSummary);
  }

  @Test
  void getTimeSummary_SumsThisWeek() {
    var today = LocalDate.of(2022, 12, 17);
    var sut = new RecentActivities(today);
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 15, 14, 23), Duration.ofMinutes(20), "foobar"));
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 12, 14, 43), Duration.ofMinutes(20), "foobar"));

    var timeSummary = sut.getTimeSummary();

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(0),
            Duration.ofMinutes(0),
            Duration.ofMinutes(40),
            Duration.ofMinutes(40)),
        timeSummary);
  }

  @Test
  void getTimeSummary_SumsThisMonth() {
    var today = LocalDate.of(2022, 12, 17);
    var sut = new RecentActivities(today);
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 1, 14, 23), Duration.ofMinutes(20), "foobar"));
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 11, 14, 43), Duration.ofMinutes(20), "foobar"));

    var timeSummary = sut.getTimeSummary();

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(0),
            Duration.ofMinutes(0),
            Duration.ofMinutes(0),
            Duration.ofMinutes(40)),
        timeSummary);
  }

  @Test
  void getTimeSummary_DoesNotSumPreviousMonth() {
    var today = LocalDate.of(2022, 12, 17);
    var sut = new RecentActivities(today);
    sut.apply(
        new Activity(LocalDateTime.of(2022, 11, 30, 14, 23), Duration.ofMinutes(20), "foobar"));
    sut.apply(
        new Activity(LocalDateTime.of(2022, 12, 1, 14, 23), Duration.ofMinutes(20), "foobar"));

    var timeSummary = sut.getTimeSummary();

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(0),
            Duration.ofMinutes(0),
            Duration.ofMinutes(0),
            Duration.ofMinutes(20)),
        timeSummary);
  }

  @Test
  void getTimeSummary_DoesNotSumFuture() {
    var today = LocalDate.of(2022, 11, 17);
    var sut = new RecentActivities(today);
    sut.apply(
        new Activity(LocalDateTime.of(2022, 11, 17, 14, 23), Duration.ofMinutes(20), "foobar"));
    sut.apply(
        new Activity(LocalDateTime.of(2022, 11, 18, 14, 23), Duration.ofMinutes(20), "foobar"));

    var timeSummary = sut.getTimeSummary();

    assertEquals(
        new TimeSummary(
            today,
            Duration.ofMinutes(20),
            Duration.ofMinutes(0),
            Duration.ofMinutes(20),
            Duration.ofMinutes(20)),
        timeSummary);
  }
}
