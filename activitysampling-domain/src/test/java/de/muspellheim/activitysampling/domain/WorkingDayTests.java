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
  void noActivity() {
    var workingDays = WorkingDay.from(List.of());

    assertEquals(List.of(), workingDays);
  }

  @Test
  void oneActivity() {
    var activity =
        new Activity(LocalDateTime.of(2022, 12, 17, 14, 23), Duration.ofMinutes(30), "foobar");

    var workingDays = WorkingDay.from(List.of(activity));

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
  void activitiesOfWorkingDayAreSortedInDescendingOrder() {
    var day = LocalDate.of(2022, 12, 17);
    var activity1 =
        new Activity(LocalDateTime.of(day, LocalTime.of(14, 23)), Duration.ofMinutes(30), "foobar");
    var activity2 =
        new Activity(LocalDateTime.of(day, LocalTime.of(14, 53)), Duration.ofMinutes(30), "foobar");

    var workingDays = WorkingDay.from(List.of(activity1, activity2));

    assertEquals(
        List.of(
            new WorkingDay(
                day,
                List.of(
                    new Activity(
                        LocalDateTime.of(day, LocalTime.of(14, 53)),
                        Duration.ofMinutes(30),
                        "foobar"),
                    new Activity(
                        LocalDateTime.of(day, LocalTime.of(14, 23)),
                        Duration.ofMinutes(30),
                        "foobar")))),
        workingDays);
  }

  @Test
  void workingDaysAreSortedInDescendingOrder() {
    var day1 = LocalDate.of(2022, 12, 17);
    var day2 = LocalDate.of(2022, 12, 18);
    var activity1 =
        new Activity(
            LocalDateTime.of(day1, LocalTime.of(14, 23)), Duration.ofMinutes(30), "foobar");
    var activity2 =
        new Activity(
            LocalDateTime.of(day2, LocalTime.of(14, 23)), Duration.ofMinutes(30), "foobar");

    var workingDays = WorkingDay.from(List.of(activity1, activity2));

    assertEquals(
        List.of(
            new WorkingDay(
                day2,
                List.of(
                    new Activity(
                        LocalDateTime.of(day2, LocalTime.of(14, 23)),
                        Duration.ofMinutes(30),
                        "foobar"))),
            new WorkingDay(
                day1,
                List.of(
                    new Activity(
                        LocalDateTime.of(day1, LocalTime.of(14, 23)),
                        Duration.ofMinutes(30),
                        "foobar")))),
        workingDays);
  }
}
