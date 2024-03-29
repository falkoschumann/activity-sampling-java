/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.infrastructure.CsvActivities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CsvActivitiesTests {
  private static final Path FILE = Paths.get("build/activities.csv");

  private CsvActivities sut;

  @BeforeEach
  void init() throws IOException {
    Files.deleteIfExists(FILE);
    sut = new CsvActivities(FILE);
  }

  @Test
  void findInPeriod_FileDoesNotExist_ReturnsEmptyList() throws Exception {
    var activities = sut.findInPeriod(LocalDate.ofEpochDay(0), LocalDate.now());

    assertEquals(List.of(), activities);
  }

  @Test
  void findInPeriod_FileExists_ReturnsEvents() throws Exception {
    sut.append(createActivity1());
    sut.append(createActivity2());
    sut.append(createActivity3());

    var activities = sut.findInPeriod(LocalDate.ofEpochDay(0), LocalDate.now());

    assertEquals(List.of(createActivity1(), createActivity2(), createActivity3()), activities);
  }

  @Test
  void findInPeriod_ReturnsActivitiesOnlyInPeriod() throws Exception {
    sut.append(createActivity1());
    sut.append(createActivity2());
    sut.append(createActivity3());

    var activities = sut.findInPeriod(LocalDate.parse("2022-11-16"), LocalDate.parse("2022-11-16"));

    assertEquals(List.of(createActivity2()), activities);
  }

  private static Activity createActivity1() {
    return Activity.builder()
        .timestamp(LocalDateTime.parse("2022-11-15T13:04:00"))
        .duration(Duration.ofMinutes(5))
        .client("c1")
        .project("p1")
        .task("t1")
        .notes("n1")
        .build();
  }

  private static Activity createActivity2() {
    return Activity.builder()
        .timestamp(LocalDateTime.parse("2022-11-16T13:24:00"))
        .duration(Duration.ofMinutes(5))
        .client("c2")
        .project("p2")
        .task("t2")
        .notes("n2")
        .build();
  }

  private static Activity createActivity3() {
    return Activity.builder()
        .timestamp(LocalDateTime.parse("2022-11-17T13:44:00"))
        .duration(Duration.ofMinutes(5))
        .client("c3")
        .project("p3")
        .task("t3")
        .notes("n3")
        .build();
  }
}
