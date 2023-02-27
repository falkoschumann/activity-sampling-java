/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muspellheim.activitysampling.application.ActivitiesServiceStub;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.ConfigurableResponses;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.TimeSummary;
import de.muspellheim.activitysampling.domain.WorkingDay;
import de.muspellheim.activitysampling.domain.WorkingDays;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivitySamplingViewModelTests {
  private ActivitiesServiceStub activitiesService;
  private boolean countdownElapsed;
  private List<String> errors;
  private ActivitySamplingViewModel sut;

  @BeforeAll
  static void initAll() {
    Locale.setDefault(Locale.GERMANY);
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
  }

  @BeforeEach
  void init() {
    errors = new ArrayList<>();

    var recentActivities =
        new RecentActivities(
            new WorkingDays(
                List.of(
                    new WorkingDay(
                        LocalDate.of(2022, 11, 16),
                        List.of(
                            new Activity(
                                LocalDateTime.of(2022, 11, 16, 16, 16),
                                Duration.ofMinutes(5),
                                "Lorem ipsum"))),
                    new WorkingDay(
                        LocalDate.of(2022, 11, 15),
                        List.of(
                            new Activity(
                                LocalDateTime.of(2022, 11, 15, 15, 15),
                                Duration.ofMinutes(5),
                                "Lorem ipsum"))),
                    new WorkingDay(
                        LocalDate.of(2022, 11, 14),
                        List.of(
                            new Activity(
                                LocalDateTime.of(2022, 11, 14, 14, 14),
                                Duration.ofMinutes(5),
                                "Lorem ipsum"))),
                    new WorkingDay(
                        LocalDate.of(2022, 11, 7),
                        List.of(
                            new Activity(
                                LocalDateTime.of(2022, 11, 7, 7, 7),
                                Duration.ofMinutes(5),
                                "Lorem ipsum"))))),
            new TimeSummary(
                LocalDate.of(2022, 11, 16),
                Duration.ofMinutes(5),
                Duration.ofMinutes(5),
                Duration.ofMinutes(15),
                Duration.ofMinutes(20)));
    activitiesService = new ActivitiesServiceStub();
    activitiesService.initRecentActivities(new ConfigurableResponses<>(recentActivities));
    sut = new ActivitySamplingViewModel(activitiesService);
    sut.addOnCountdownElapsedListener(v -> countdownElapsed = true);
    sut.addOnErrorListener(e -> errors.addAll(e));
    sut.run();
  }

  @Test
  void run_ViewIsInitialized() {
    assertAll(
        () -> assertTrue(sut.stopMenuItemDisableProperty().get(), "Stop menu item disable"),
        () -> assertEquals("", sut.activityTextProperty().get(), "Activity text"),
        () -> assertFalse(sut.formDisableProperty().get(), "Form disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:20:00", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () ->
            assertEquals(
                List.of(
                    new ActivityItem("Mittwoch, 16. November 2022"),
                    new ActivityItem(
                        "16:16 - Lorem ipsum",
                        new Activity(
                            LocalDateTime.parse("2022-11-16T16:16"),
                            Duration.ofMinutes(5),
                            "Lorem ipsum")),
                    new ActivityItem("Dienstag, 15. November 2022"),
                    new ActivityItem(
                        "15:15 - Lorem ipsum",
                        new Activity(
                            LocalDateTime.parse("2022-11-15T15:15"),
                            Duration.ofMinutes(5),
                            "Lorem ipsum")),
                    new ActivityItem("Montag, 14. November 2022"),
                    new ActivityItem(
                        "14:14 - Lorem ipsum",
                        new Activity(
                            LocalDateTime.parse("2022-11-14T14:14"),
                            Duration.ofMinutes(5),
                            "Lorem ipsum")),
                    new ActivityItem("Montag, 7. November 2022"),
                    new ActivityItem(
                        "07:07 - Lorem ipsum",
                        new Activity(
                            LocalDateTime.parse("2022-11-07T07:07"),
                            Duration.ofMinutes(5),
                            "Lorem ipsum"))),
                sut.getRecentActivities(),
                "Recent activities"),
        () -> assertEquals("00:05", sut.hoursTodayLabelTextProperty().get()),
        () -> assertEquals("00:05", sut.hoursYesterdayLabelTextProperty().get()),
        () -> assertEquals("00:15", sut.hoursThisWeekLabelTextProperty().get()),
        () -> assertEquals("00:20", sut.hoursThisMonthLabelTextProperty().get()));
  }

  @Test
  void load_Failed_NotifyError() {
    activitiesService.initRecentActivities(
        new ConfigurableResponses<>(new IllegalStateException("Something went wrong.")));

    sut.load();

    assertEquals(List.of("Failed to load activities.", "Something went wrong."), errors);
  }

  @Test
  void changeActivityText_IsNotEmpty_LogButtonIsEnabled() {
    sut.activityTextProperty().set("foobar");

    assertFalse(sut.logButtonDisableProperty().get());
  }

  @Test
  void changeActivityText_IsEmpty_LogButtonIsDisabled() {
    sut.activityTextProperty().set("");

    assertTrue(sut.logButtonDisableProperty().get());
  }

  @Test
  void changeActivityText_IsBlank_LogButtonIsDisabled() {
    sut.activityTextProperty().set("  ");

    assertTrue(sut.logButtonDisableProperty().get());
  }

  @Test
  void logActivity_CountdownIsNotActive_LeavesFormEnabled() {
    var loggedActivitiesTracker = activitiesService.getLoggedActivityTracker();

    sut.activityTextProperty().set("foobar");
    sut.logActivity();

    assertAll(
        () ->
            assertEquals(
                List.of(new ActivitiesServiceStub.ActivityLogged("foobar", Duration.ofMinutes(20))),
                loggedActivitiesTracker.data(),
                "Logged activities"),
        () -> assertFalse(sut.formDisableProperty().get(), "Form disable"),
        () -> assertFalse(sut.logButtonDisableProperty().get(), "Log button disable"));
  }

  @Test
  void logActivity_CountdownActive_DisablesForm() {
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(61));
    var activityLoggedTracker = activitiesService.getLoggedActivityTracker();

    sut.activityTextProperty().set("foobar");
    sut.logActivity();

    assertAll(
        () ->
            assertEquals(
                List.of(new ActivitiesServiceStub.ActivityLogged("foobar", Duration.ofMinutes(1))),
                activityLoggedTracker.data(),
                "Logged activities"),
        () -> assertTrue(sut.formDisableProperty().get(), "Form disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"));
  }

  @Test
  void logActivity_Failed_NotifyError() {
    activitiesService.initLogActivity(
        new ConfigurableResponses<>(new IllegalStateException("Something went wrong.")));
    sut.activityTextProperty().set("foobar");

    sut.logActivity();

    assertEquals(List.of("Failed to log activity.", "Something went wrong."), errors);
  }

  @Test
  void setActivity_UpdatesForm() {
    var activity =
        new Activity(LocalDateTime.of(2022, 11, 26, 16, 16), Duration.ofMinutes(20), "Lorem ipsum");
    sut.setActivity(activity);

    assertAll(
        () -> assertEquals("Lorem ipsum", sut.activityTextProperty().get(), "Activity text"),
        () -> assertFalse(sut.logButtonDisableProperty().get(), "Log button disable"));
  }

  @Test
  void startCountdown_DisablesFormAndInitializesCountdown() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(20));

    assertAll(
        () -> assertFalse(sut.stopMenuItemDisableProperty().get(), "Stop menu item disable"),
        () -> assertTrue(sut.formDisableProperty().get(), "Form disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:20:00", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> assertFalse(countdownElapsed, "Countdown elapsed"));
  }

  @Test
  void progressCountdown_FirstTick_UpdatesCountdown() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));

    sut.progressCountdown(Duration.ofSeconds(1));

    assertAll(
        () -> assertFalse(sut.stopMenuItemDisableProperty().get(), "Stop menu item disable"),
        () -> assertTrue(sut.formDisableProperty().get(), "Form disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:00:59", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(1.0 / 60.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> assertFalse(countdownElapsed, "Countdown elapsed"));
  }

  @Test
  void progressCountdown_SecondTick_UpdatesCountdown() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));

    sut.progressCountdown(Duration.ofSeconds(2));

    assertAll(
        () -> assertFalse(sut.stopMenuItemDisableProperty().get(), "Stop menu item disable"),
        () -> assertTrue(sut.formDisableProperty().get(), "Form disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:00:58", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(2.0 / 60.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> assertFalse(countdownElapsed, "Countdown elapsed"));
  }

  @Test
  void progressCountdown_LastTick_UpdatesCountdown() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));

    sut.progressCountdown(Duration.ofSeconds(59));

    assertAll(
        () -> assertFalse(sut.stopMenuItemDisableProperty().get(), "Stop menu item disable"),
        () -> assertTrue(sut.formDisableProperty().get(), "Form disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:00:01", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () ->
            assertEquals(0.983, sut.countdownProgressProperty().get(), 0.001, "Countdown progress"),
        () -> assertFalse(countdownElapsed, "Countdown elapsed"));
  }

  @Test
  void progressCountdown_CountdownElapsed_UpdatesCountdownAndNotifies() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));

    sut.progressCountdown(Duration.ofSeconds(60));

    assertAll(
        () -> assertFalse(sut.stopMenuItemDisableProperty().get(), "Stop menu item disable"),
        () -> assertFalse(sut.formDisableProperty().get(), "Form disable"),
        () -> assertFalse(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:01:00", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> assertTrue(countdownElapsed, "Countdown elapsed"));
  }

  @Test
  void progressCountdown_CountdownIsNotActive_DoesNothing() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));
    sut.stopCountdown();

    sut.progressCountdown(Duration.ofSeconds(10));

    assertAll(
        () -> assertTrue(sut.stopMenuItemDisableProperty().get(), "Stop menu item disable"),
        () -> assertFalse(sut.formDisableProperty().get(), "Form disable"),
        () -> assertFalse(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:01:00", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> assertFalse(countdownElapsed, "Countdown elapsed"));
  }

  @Test
  void stopCountdown_EnablesForm() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(15));

    sut.stopCountdown();

    assertAll(
        () -> assertTrue(sut.stopMenuItemDisableProperty().get(), "Stop menu item disable"),
        () -> assertFalse(sut.formDisableProperty().get(), "Form disable"),
        () -> assertFalse(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:00:45", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.25, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> assertFalse(countdownElapsed, "Countdown elapsed"));
  }
}
