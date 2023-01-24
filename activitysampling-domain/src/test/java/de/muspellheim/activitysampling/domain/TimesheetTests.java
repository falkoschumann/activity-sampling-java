/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class TimesheetTests {
  @Test
  void whenIfNoActivitiesAreAdded_ThenThereAreNoEntries() {
    var sut = new Timesheet();

    assertEquals(List.of(), sut.getEntries(), "entries");
    assertEquals(Duration.ZERO, sut.getTotal(), "total");
  }

  @Test
  void when1ActivityAdded_ThenThereIs1Entry() {
    var date = LocalDate.of(2022, 12, 18);
    var sut = new Timesheet();

    sut.add(new Activity(date.atTime(14, 39), Duration.ofMinutes(30), "foo"));

    assertEquals(
        List.of(new Timesheet.Entry(date, "foo", Duration.ofMinutes(30))),
        sut.getEntries(),
        "entries");
    assertEquals(Duration.ofMinutes(30), sut.getTotal(), "total");
  }

  @Test
  void whenMultipleSameActivitiesAdded_ThenThereSummedAs1Entry() {
    var date = LocalDate.of(2022, 12, 18);
    var sut = new Timesheet();

    sut.addAll(
        List.of(
            new Activity(date.atTime(14, 19), Duration.ofMinutes(30), "foo"),
            new Activity(date.atTime(14, 39), Duration.ofMinutes(20), "foo")));

    assertEquals(
        List.of(new Timesheet.Entry(date, "foo", Duration.ofMinutes(50))),
        sut.getEntries(),
        "entries");
    assertEquals(Duration.ofMinutes(50), sut.getTotal(), "total");
  }

  @Test
  void whenMultipleDifferentActivitiesAdded_ThenReturns2Entries() {
    var date = LocalDate.of(2022, 12, 18);
    var sut = new Timesheet();

    sut.addAll(
        List.of(
            new Activity(date.atTime(14, 19), Duration.ofMinutes(30), "foo"),
            new Activity(date.atTime(14, 39), Duration.ofMinutes(20), "bar")));

    assertEquals(
        List.of(
            new Timesheet.Entry(date, "bar", Duration.ofMinutes(20)),
            new Timesheet.Entry(date, "foo", Duration.ofMinutes(30))),
        sut.getEntries(),
        "entries");
    assertEquals(Duration.ofMinutes(50), sut.getTotal(), "total");
  }

  @Test
  void whenMultipleDifferentActivitiesAddedOnSameDay_ThenEntriesAreSummedPerActivity() {
    var date = LocalDate.of(2022, 12, 18);
    var sut = new Timesheet();

    sut.addAll(
        List.of(
            new Activity(date.atTime(14, 19), Duration.ofMinutes(30), "foo"),
            new Activity(date.atTime(14, 39), Duration.ofMinutes(20), "bar"),
            new Activity(date.atTime(15, 19), Duration.ofMinutes(30), "foo"),
            new Activity(date.atTime(15, 39), Duration.ofMinutes(20), "bar")));

    assertEquals(
        List.of(
            new Timesheet.Entry(date, "bar", Duration.ofMinutes(40)),
            new Timesheet.Entry(date, "foo", Duration.ofMinutes(60))),
        sut.getEntries(),
        "entries");
    assertEquals(Duration.ofMinutes(100), sut.getTotal(), "total");
  }

  @Test
  void whenMultipleDifferentActivitiesAddedOnDifferentDays_ThenEntriesAreSummedPerActivityAndDay() {
    var date1 = LocalDate.of(2022, 12, 17);
    var date2 = LocalDate.of(2022, 12, 18);
    var sut = new Timesheet();

    sut.addAll(
        List.of(
            new Activity(date1.atTime(14, 19), Duration.ofMinutes(30), "foo"),
            new Activity(date1.atTime(14, 39), Duration.ofMinutes(20), "bar"),
            new Activity(date2.atTime(15, 19), Duration.ofMinutes(30), "bar"),
            new Activity(date2.atTime(15, 39), Duration.ofMinutes(30), "bar")));

    assertEquals(
        List.of(
            new Timesheet.Entry(date1, "bar", Duration.ofMinutes(20)),
            new Timesheet.Entry(date1, "foo", Duration.ofMinutes(30)),
            new Timesheet.Entry(date2, "bar", Duration.ofMinutes(60))),
        sut.getEntries(),
        "entries");
    assertEquals(Duration.ofMinutes(110), sut.getTotal(), "total");
  }
}
