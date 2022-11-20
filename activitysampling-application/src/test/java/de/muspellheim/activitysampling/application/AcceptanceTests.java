package de.muspellheim.activitysampling.application;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class AcceptanceTests {
  private static final Path STORE_FILE = Paths.get("build/activity-log.csv");

  @BeforeEach
  void init() throws IOException {
    Files.deleteIfExists(STORE_FILE);
  }

  @Test
  void mainScenario() {
    var sut = new SystemUnderTest(STORE_FILE);
    sut.now(Instant.parse("2022-11-16T17:05:00Z"));

    // Log first activity
    sut.activityText("Lorem ipsum");
    sut.logActivity();
    assertAll(
        "Log first activity",
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:05 - Lorem ipsum"),
                sut.recentActivities()),
        () -> assertEquals("00:20", sut.hoursToday()),
        () -> assertEquals("00:00", sut.hoursYesterday()),
        () -> assertEquals("00:20", sut.hoursThisWeek()),
        () -> assertEquals("00:20", sut.hoursThisMonth()));

    // Log second activity
    sut.tick(Duration.ofMinutes(20));
    sut.activityText("Foobar");
    sut.logActivity();
    assertAll(
        "Log second activity",
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:25 - Foobar", "18:05 - Lorem ipsum"),
                sut.recentActivities()),
        () -> assertEquals("00:40", sut.hoursToday()),
        () -> assertEquals("00:00", sut.hoursYesterday()),
        () -> assertEquals("00:40", sut.hoursThisWeek()),
        () -> assertEquals("00:40", sut.hoursThisMonth()));

    // Select first activity
    sut.selectActivity("18:05 - Lorem ipsum");
    assertAll("Select first activity", () -> assertEquals("Lorem ipsum", sut.activityText()));
  }
}
