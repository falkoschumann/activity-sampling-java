/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.uat;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivitySamplingTests {
  private ActivitySamplingFixture activitySamplingFixture;
  private RecentActivitiesFixture recentActivitiesFixture;

  @BeforeEach
  void init() {
    SystemUnderTest.INSTANCE.reset();
    activitySamplingFixture = new ActivitySamplingFixture();
    recentActivitiesFixture = new RecentActivitiesFixture();
  }

  @Test
  void mainScenario() {
    activitySamplingFixture.enterTimestamp(Instant.parse("2022-11-16T17:05:00Z"));

    activitySamplingFixture.enterStartCountdown(20);
    assertAll(
        "Start countdown",
        () -> assertEquals("00:20:00", activitySamplingFixture.checkCountdown(), "Countdown"),
        () ->
            assertEquals(
                0.0, activitySamplingFixture.checkCountdownProgress(), "Countdown progress"),
        () -> assertEquals(List.of(), recentActivitiesFixture.query(), "Recent activities"),
        () -> assertEquals("00:00", activitySamplingFixture.checkHoursToday(), "Hours today"),
        () ->
            assertEquals("00:00", activitySamplingFixture.checkHoursYesterday(), "Hours yesterday"),
        () ->
            assertEquals("00:00", activitySamplingFixture.checkHoursThisWeek(), "Hours this week"),
        () ->
            assertEquals(
                "00:00", activitySamplingFixture.checkHoursThisMonth(), "Hours this month"));

    activitySamplingFixture.enterTick(Duration.ofMinutes(12));
    assertAll(
        "Progress countdown",
        () -> assertEquals("00:08:00", activitySamplingFixture.checkCountdown(), "Countdown"),
        () ->
            assertEquals(
                0.6, activitySamplingFixture.checkCountdownProgress(), "Countdown progress"));

    activitySamplingFixture.enterTick(Duration.ofMinutes(8));
    activitySamplingFixture.enterActivityText("Lorem ipsum");
    activitySamplingFixture.pressLogActivity();
    assertAll(
        "Log first activity",
        () -> assertEquals("00:20:00", activitySamplingFixture.checkCountdown(), "Countdown"),
        () ->
            assertEquals(
                0.0, activitySamplingFixture.checkCountdownProgress(), "Countdown progress"),
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:25 - Lorem ipsum"),
                recentActivitiesFixture.query(),
                "Recent countdown"),
        () -> assertEquals("00:20", activitySamplingFixture.checkHoursToday(), "Hours today"),
        () ->
            assertEquals("00:00", activitySamplingFixture.checkHoursYesterday(), "Hours yesterday"),
        () ->
            assertEquals("00:20", activitySamplingFixture.checkHoursThisWeek(), "Hours this week"),
        () ->
            assertEquals(
                "00:20", activitySamplingFixture.checkHoursThisMonth(), "Hours this month"));

    activitySamplingFixture.enterTick(Duration.ofMinutes(20));
    activitySamplingFixture.enterActivityText("Foobar");
    activitySamplingFixture.pressLogActivity();
    assertAll(
        "Log second activity",
        () -> assertEquals("00:20:00", activitySamplingFixture.checkCountdown(), "Countdown"),
        () ->
            assertEquals(
                0.0, activitySamplingFixture.checkCountdownProgress(), "Countdown progress"),
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:45 - Foobar", "18:25 - Lorem ipsum"),
                recentActivitiesFixture.query(),
                "Recent activities"),
        () -> assertEquals("00:40", activitySamplingFixture.checkHoursToday(), "Hours today"),
        () ->
            assertEquals("00:00", activitySamplingFixture.checkHoursYesterday(), "Hours yesterday"),
        () ->
            assertEquals("00:40", activitySamplingFixture.checkHoursThisWeek(), "Hours this week"),
        () ->
            assertEquals(
                "00:40", activitySamplingFixture.checkHoursThisMonth(), "Hours this month"));

    activitySamplingFixture.enterSelectActivity("18:25 - Lorem ipsum");
    assertAll(
        "Select first activity",
        () ->
            assertEquals(
                "Lorem ipsum", activitySamplingFixture.checkActivityText(), "Activity text"));
  }

  @Test
  void alternateScenario_DoNotStartCountdown() {
    activitySamplingFixture.enterTimestamp(Instant.parse("2022-11-16T17:05:00Z"));

    activitySamplingFixture.enterActivityText("Lorem ipsum");
    activitySamplingFixture.pressLogActivity();
    assertAll(
        "Log first activity",
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:05 - Lorem ipsum"),
                recentActivitiesFixture.query(),
                "Recent activities"),
        () -> assertEquals("00:20", activitySamplingFixture.checkHoursToday(), "Hours today"),
        () ->
            assertEquals("00:00", activitySamplingFixture.checkHoursYesterday(), "Hours yesterday"),
        () ->
            assertEquals("00:20", activitySamplingFixture.checkHoursThisWeek(), "Hours this week"),
        () ->
            assertEquals(
                "00:20", activitySamplingFixture.checkHoursThisMonth(), "Hours this month"));

    activitySamplingFixture.enterTick(Duration.ofMinutes(20));
    activitySamplingFixture.enterActivityText("Foobar");
    activitySamplingFixture.pressLogActivity();
    assertAll(
        "Log second activity",
        () ->
            assertEquals(
                List.of("Mittwoch, 16. November 2022", "18:25 - Foobar", "18:05 - Lorem ipsum"),
                recentActivitiesFixture.query(),
                "Recent activities"),
        () -> assertEquals("00:40", activitySamplingFixture.checkHoursToday(), "Hours today"),
        () ->
            assertEquals("00:00", activitySamplingFixture.checkHoursYesterday(), "Hours yesterday"),
        () ->
            assertEquals("00:40", activitySamplingFixture.checkHoursThisWeek(), "Hours this week"),
        () ->
            assertEquals(
                "00:40", activitySamplingFixture.checkHoursThisMonth(), "Hours this month"));

    activitySamplingFixture.enterSelectActivity("18:05 - Lorem ipsum");
    assertAll(
        "Select first activity",
        () ->
            assertEquals(
                "Lorem ipsum", activitySamplingFixture.checkActivityText(), "Activity text"));
  }
}
