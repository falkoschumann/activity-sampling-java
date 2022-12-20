/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class TimesheetTests {
  @Test
  void getEntries_NoActivity_ReturnsEmpty() {
    var sut = new Timesheet();

    var entries = sut.getEntries();

    assertEquals(List.of(), entries);
  }

  @Test
  void getEntries_FirstActivity_Returns1Entry() {
    var sut = new Timesheet();
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 14, 39), Duration.ofMinutes(30), "foo"));

    var entries = sut.getEntries();

    assertEquals(
        List.of(new TimesheetEntry(LocalDate.of(2022, 12, 18), "foo", Duration.ofMinutes(30))),
        entries);
  }

  @Test
  void getEntries_2SameActivities_Returns1SummedEntry() {
    var sut = new Timesheet();
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 14, 19), Duration.ofMinutes(30), "foo"));
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 14, 39), Duration.ofMinutes(20), "foo"));

    var entries = sut.getEntries();

    assertEquals(
        List.of(new TimesheetEntry(LocalDate.of(2022, 12, 18), "foo", Duration.ofMinutes(50))),
        entries);
  }

  @Test
  void getEntries_2DifferentActivities_Returns2Entries() {
    var sut = new Timesheet();
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 14, 19), Duration.ofMinutes(30), "foo"));
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 14, 39), Duration.ofMinutes(20), "bar"));

    var entries = sut.getEntries();

    assertEquals(
        List.of(
            new TimesheetEntry(LocalDate.of(2022, 12, 18), "bar", Duration.ofMinutes(20)),
            new TimesheetEntry(LocalDate.of(2022, 12, 18), "foo", Duration.ofMinutes(30))),
        entries);
  }

  @Test
  void getEntries_MultipleDifferentActivitiesOnSameDay_ReturnsSummedEntryPerActivity() {
    var sut = new Timesheet();
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 14, 19), Duration.ofMinutes(30), "foo"));
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 14, 39), Duration.ofMinutes(20), "bar"));
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 15, 19), Duration.ofMinutes(30), "foo"));
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 15, 39), Duration.ofMinutes(20), "bar"));
    var entries = sut.getEntries();

    assertEquals(
        List.of(
            new TimesheetEntry(LocalDate.of(2022, 12, 18), "bar", Duration.ofMinutes(40)),
            new TimesheetEntry(LocalDate.of(2022, 12, 18), "foo", Duration.ofMinutes(60))),
        entries);
  }

  @Test
  void getEntries_MultipleDifferentActivitiesOnDifferentDays_ReturnsSummedEntryPerActivityAndDay() {
    var sut = new Timesheet();
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 17, 14, 19), Duration.ofMinutes(30), "foo"));
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 17, 14, 39), Duration.ofMinutes(20), "bar"));
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 15, 19), Duration.ofMinutes(30), "bar"));
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 15, 39), Duration.ofMinutes(30), "bar"));

    var entries = sut.getEntries();

    assertEquals(
        List.of(
            new TimesheetEntry(LocalDate.of(2022, 12, 17), "bar", Duration.ofMinutes(20)),
            new TimesheetEntry(LocalDate.of(2022, 12, 17), "foo", Duration.ofMinutes(30)),
            new TimesheetEntry(LocalDate.of(2022, 12, 18), "bar", Duration.ofMinutes(60))),
        entries);
  }

  @Test
  void getTotal_NoActivity_ReturnsZero() {
    var sut = new Timesheet();

    var total = sut.getTotal();

    assertEquals(Duration.ZERO, total);
  }

  @Test
  void getTotal_1Activity_ReturnsDuration() {
    var sut = new Timesheet();
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 14, 39), Duration.ofMinutes(30), "foo"));

    var total = sut.getTotal();

    assertEquals(Duration.ofMinutes(30), total);
  }

  @Test
  void getTotal_2Activities_ReturnsSum() {
    var sut = new Timesheet();
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 17, 14, 19), Duration.ofMinutes(30), "foo"));
    sut.apply(new Activity(LocalDateTime.of(2022, 12, 18, 14, 39), Duration.ofMinutes(20), "foo"));

    var total = sut.getTotal();

    assertEquals(Duration.ofMinutes(50), total);
  }
}
