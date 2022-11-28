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
  void findAll_FileDoesNotExist_ReturnsEmptyList() {
    var activities = sut.findAll();

    assertEquals(List.of(), activities);
  }

  @Test
  void findAll_FileExists_ReturnsAppendedEvents() {
    sut.append(
        new Activity(LocalDateTime.parse("2022-11-16T13:04:00"), Duration.ofMinutes(5), "A1"));
    sut.append(
        new Activity(LocalDateTime.parse("2022-11-16T13:24:00"), Duration.ofMinutes(5), "A2"));
    sut.append(
        new Activity(LocalDateTime.parse("2022-11-16T13:44:00"), Duration.ofMinutes(5), "A3"));

    var activities = sut.findAll();

    assertEquals(
        List.of(
            new Activity(LocalDateTime.parse("2022-11-16T13:04:00"), Duration.ofMinutes(5), "A1"),
            new Activity(LocalDateTime.parse("2022-11-16T13:24:00"), Duration.ofMinutes(5), "A2"),
            new Activity(LocalDateTime.parse("2022-11-16T13:44:00"), Duration.ofMinutes(5), "A3")),
        activities);
  }
}
