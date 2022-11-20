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

    sut.startCountdown(20);
    assertAll(
        "Start countdown",
        () -> assertEquals("00:20:00", sut.countdown(), "Countdown"),
        () -> assertEquals(0.0, sut.countdownProgress(), "Countdown progress"),
        () -> assertEquals(List.of(), sut.recentActivities(), "Recent activities"),
        () -> assertEquals("00:00", sut.hoursToday(), "Hours today"),
        () -> assertEquals("00:00", sut.hoursYesterday(), "Hours yesterday"),
        () -> assertEquals("00:00", sut.hoursThisWeek(), "Hours this week"),
        () -> assertEquals("00:00", sut.hoursThisMonth(), "Hours this month"));

    sut.tick(Duration.ofMinutes(12));
    assertAll(
        "Progress countdown",
        () -> assertEquals("00:08:00", sut.countdown(), "Countdown"),
        () -> assertEquals(0.6, sut.countdownProgress(), "Countdown progress"));

    sut.tick(Duration.ofMinutes(8));
    sut.activityText("Lorem ipsum");
    sut.logActivity();
    assertAll(
        "Log first activity",
        () -> assertEquals("00:20:00", sut.countdown(), "Countdown"),
        () -> assertEquals(0.0, sut.countdownProgress(), "Countdown progress"),
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:25 - Lorem ipsum"),
                sut.recentActivities(),
                "Recent countdown"),
        () -> assertEquals("00:20", sut.hoursToday(), "Hours today"),
        () -> assertEquals("00:00", sut.hoursYesterday(), "Hours yesterday"),
        () -> assertEquals("00:20", sut.hoursThisWeek(), "Hours this week"),
        () -> assertEquals("00:20", sut.hoursThisMonth(), "Hours this month"));

    sut.tick(Duration.ofMinutes(20));
    sut.activityText("Foobar");
    sut.logActivity();
    assertAll(
        "Log second activity",
        () -> assertEquals("00:20:00", sut.countdown(), "Countdown"),
        () -> assertEquals(0.0, sut.countdownProgress(), "Countdown progress"),
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:45 - Foobar", "18:25 - Lorem ipsum"),
                sut.recentActivities(),
                "Recent activities"),
        () -> assertEquals("00:40", sut.hoursToday(), "Hours today"),
        () -> assertEquals("00:00", sut.hoursYesterday(), "Hours yesterday"),
        () -> assertEquals("00:40", sut.hoursThisWeek(), "Hours this week"),
        () -> assertEquals("00:40", sut.hoursThisMonth(), "Hours this month"));

    sut.selectActivity("18:25 - Lorem ipsum");
    assertAll(
        "Select first activity",
        () -> assertEquals("Lorem ipsum", sut.activityText(), "Activity text"));
  }

  @Test
  void alternateScenario_DoNotStartCountdown() {
    var sut = new SystemUnderTest(STORE_FILE);
    sut.now(Instant.parse("2022-11-16T17:05:00Z"));

    sut.activityText("Lorem ipsum");
    sut.logActivity();
    assertAll(
        "Log first activity",
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:05 - Lorem ipsum"),
                sut.recentActivities(),
                "Recent activities"),
        () -> assertEquals("00:20", sut.hoursToday(), "Hours today"),
        () -> assertEquals("00:00", sut.hoursYesterday(), "Hours yesterday"),
        () -> assertEquals("00:20", sut.hoursThisWeek(), "Hours this week"),
        () -> assertEquals("00:20", sut.hoursThisMonth(), "Hours this month"));

    sut.tick(Duration.ofMinutes(20));
    sut.activityText("Foobar");
    sut.logActivity();
    assertAll(
        "Log second activity",
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:25 - Foobar", "18:05 - Lorem ipsum"),
                sut.recentActivities(),
                "Recent activities"),
        () -> assertEquals("00:40", sut.hoursToday(), "Hours today"),
        () -> assertEquals("00:00", sut.hoursYesterday(), "Hours yesterday"),
        () -> assertEquals("00:40", sut.hoursThisWeek(), "Hours this week"),
        () -> assertEquals("00:40", sut.hoursThisMonth(), "Hours this month"));

    sut.selectActivity("18:05 - Lorem ipsum");
    assertAll(
        "Select first activity",
        () -> assertEquals("Lorem ipsum", sut.activityText(), "Activity text"));
  }
}
