package de.muspellheim.activitysampling.application.uat;

import static org.junit.jupiter.api.Assertions.*;

import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class ActivitySamplingTests {
  @BeforeEach
  void init() {
    SystemUnderTest.INSTANCE.reset();
  }

  @Test
  void mainScenario() {
    var activitySamplingFixture = new ActivitySamplingFixture();
    var recentActivitiesFixture = new RecentActivitiesFixture();

    activitySamplingFixture.now(Instant.parse("2022-11-16T17:05:00Z"));

    activitySamplingFixture.startCountdown(20);
    assertAll(
        "Start countdown",
        () -> assertEquals("00:20:00", activitySamplingFixture.countdown(), "Countdown"),
        () -> assertEquals(0.0, activitySamplingFixture.countdownProgress(), "Countdown progress"),
        () -> assertEquals(List.of(), recentActivitiesFixture.query(), "Recent activities"),
        () -> assertEquals("00:00", activitySamplingFixture.hoursToday(), "Hours today"),
        () -> assertEquals("00:00", activitySamplingFixture.hoursYesterday(), "Hours yesterday"),
        () -> assertEquals("00:00", activitySamplingFixture.hoursThisWeek(), "Hours this week"),
        () -> assertEquals("00:00", activitySamplingFixture.hoursThisMonth(), "Hours this month"));

    activitySamplingFixture.tick(Duration.ofMinutes(12));
    assertAll(
        "Progress countdown",
        () -> assertEquals("00:08:00", activitySamplingFixture.countdown(), "Countdown"),
        () -> assertEquals(0.6, activitySamplingFixture.countdownProgress(), "Countdown progress"));

    activitySamplingFixture.tick(Duration.ofMinutes(8));
    activitySamplingFixture.activityText("Lorem ipsum");
    activitySamplingFixture.logActivity();
    assertAll(
        "Log first activity",
        () -> assertEquals("00:20:00", activitySamplingFixture.countdown(), "Countdown"),
        () -> assertEquals(0.0, activitySamplingFixture.countdownProgress(), "Countdown progress"),
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:25 - Lorem ipsum"),
                recentActivitiesFixture.query(),
                "Recent countdown"),
        () -> assertEquals("00:20", activitySamplingFixture.hoursToday(), "Hours today"),
        () -> assertEquals("00:00", activitySamplingFixture.hoursYesterday(), "Hours yesterday"),
        () -> assertEquals("00:20", activitySamplingFixture.hoursThisWeek(), "Hours this week"),
        () -> assertEquals("00:20", activitySamplingFixture.hoursThisMonth(), "Hours this month"));

    activitySamplingFixture.tick(Duration.ofMinutes(20));
    activitySamplingFixture.activityText("Foobar");
    activitySamplingFixture.logActivity();
    assertAll(
        "Log second activity",
        () -> assertEquals("00:20:00", activitySamplingFixture.countdown(), "Countdown"),
        () -> assertEquals(0.0, activitySamplingFixture.countdownProgress(), "Countdown progress"),
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:45 - Foobar", "18:25 - Lorem ipsum"),
                recentActivitiesFixture.query(),
                "Recent activities"),
        () -> assertEquals("00:40", activitySamplingFixture.hoursToday(), "Hours today"),
        () -> assertEquals("00:00", activitySamplingFixture.hoursYesterday(), "Hours yesterday"),
        () -> assertEquals("00:40", activitySamplingFixture.hoursThisWeek(), "Hours this week"),
        () -> assertEquals("00:40", activitySamplingFixture.hoursThisMonth(), "Hours this month"));

    activitySamplingFixture.selectActivity("18:25 - Lorem ipsum");
    assertAll(
        "Select first activity",
        () -> assertEquals("Lorem ipsum", activitySamplingFixture.activityText(), "Activity text"));
  }

  @Test
  void alternateScenario_DoNotStartCountdown() {
    var activitySamplingFixture = new ActivitySamplingFixture();
    var recentActivitiesFixture = new RecentActivitiesFixture();

    activitySamplingFixture.now(Instant.parse("2022-11-16T17:05:00Z"));

    activitySamplingFixture.activityText("Lorem ipsum");
    activitySamplingFixture.logActivity();
    assertAll(
        "Log first activity",
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:05 - Lorem ipsum"),
                recentActivitiesFixture.query(),
                "Recent activities"),
        () -> assertEquals("00:20", activitySamplingFixture.hoursToday(), "Hours today"),
        () -> assertEquals("00:00", activitySamplingFixture.hoursYesterday(), "Hours yesterday"),
        () -> assertEquals("00:20", activitySamplingFixture.hoursThisWeek(), "Hours this week"),
        () -> assertEquals("00:20", activitySamplingFixture.hoursThisMonth(), "Hours this month"));

    activitySamplingFixture.tick(Duration.ofMinutes(20));
    activitySamplingFixture.activityText("Foobar");
    activitySamplingFixture.logActivity();
    assertAll(
        "Log second activity",
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:25 - Foobar", "18:05 - Lorem ipsum"),
                recentActivitiesFixture.query(),
                "Recent activities"),
        () -> assertEquals("00:40", activitySamplingFixture.hoursToday(), "Hours today"),
        () -> assertEquals("00:00", activitySamplingFixture.hoursYesterday(), "Hours yesterday"),
        () -> assertEquals("00:40", activitySamplingFixture.hoursThisWeek(), "Hours this week"),
        () -> assertEquals("00:40", activitySamplingFixture.hoursThisMonth(), "Hours this month"));

    activitySamplingFixture.selectActivity("18:05 - Lorem ipsum");
    assertAll(
        "Select first activity",
        () -> assertEquals("Lorem ipsum", activitySamplingFixture.activityText(), "Activity text"));
  }
}
