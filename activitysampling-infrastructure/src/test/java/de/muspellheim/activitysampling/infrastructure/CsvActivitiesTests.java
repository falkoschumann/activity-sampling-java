/*
 * Activity Sampling - Infrastructure
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import de.muspellheim.activitysampling.domain.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class CsvActivitiesTests {
  private static final Path FILE = Paths.get("build/activities.csv");

  private CsvActivities sut;

  @BeforeEach
  void init() throws IOException {
    Files.deleteIfExists(FILE);
    sut = new CsvActivities(FILE);
  }

  @Test
  void findInPeriod_FileDoesNotExist_ReturnsEmptyList() {
    var activities = sut.findInPeriod(LocalDate.ofEpochDay(0), LocalDate.now());

    assertEquals(List.of(), activities);
  }

  @Test
  void findInPeriod_FileExists_ReturnsAppendedEvents() {
    sut.append(createA1());
    sut.append(createA2());
    sut.append(createA3());

    var activities = sut.findInPeriod(LocalDate.ofEpochDay(0), LocalDate.now());

    assertEquals(List.of(createA1(), createA2(), createA3()), activities);
  }

  @Test
  void findInPeriod_ReturnsActivitiesInPeriod() {
    sut.append(createA1());
    sut.append(createA2());
    sut.append(createA3());

    var activities = sut.findInPeriod(LocalDate.parse("2022-11-16"), LocalDate.parse("2022-11-16"));

    assertEquals(List.of(createA2()), activities);
  }

  private static Activity createA1() {
    return new Activity(LocalDateTime.parse("2022-11-15T13:04:00"), Duration.ofMinutes(5), "A1");
  }

  private static Activity createA2() {
    return new Activity(LocalDateTime.parse("2022-11-16T13:24:00"), Duration.ofMinutes(5), "A2");
  }

  private static Activity createA3() {
    return new Activity(LocalDateTime.parse("2022-11-17T13:44:00"), Duration.ofMinutes(5), "A3");
  }
}
