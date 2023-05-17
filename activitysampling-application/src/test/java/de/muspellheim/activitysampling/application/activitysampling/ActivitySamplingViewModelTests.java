/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.application.ActivitiesServiceStub;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.domain.WorkingDay;
import de.muspellheim.common.util.ConfigurableResponses;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivitySamplingViewModelTests {
  private ActivitiesServiceStub activitiesService;
  private Clock clock;
  private ActivitySamplingViewModel sut;
  private boolean countdownElapsed;
  private List<Throwable> errors;

  @BeforeEach
  void init() {
    activitiesService = new ActivitiesServiceStub();
    activitiesService.initLogActivityResponses(ConfigurableResponses.always(true));
    activitiesService.initRecentActivitiesResponses(
        ConfigurableResponses.always(newRecentActivities()));
    clock = Clock.fixed(Instant.parse("2022-11-16T17:17:17Z"), ZoneId.of("Europe/Berlin"));
    sut = new ActivitySamplingViewModel(activitiesService, Locale.GERMANY, clock);
    countdownElapsed = false;
    sut.addOnCountdownElapsedListener(x -> countdownElapsed = true);
    errors = new ArrayList<>();
    sut.addOnErrorListener(errors::add);
  }

  private static RecentActivities newRecentActivities() {
    return new RecentActivities(
        List.of(
            newWorkingDay(LocalDateTime.of(2022, 11, 16, 16, 16)),
            newWorkingDay(LocalDateTime.of(2022, 11, 15, 15, 15)),
            newWorkingDay(LocalDateTime.of(2022, 11, 14, 14, 14)),
            newWorkingDay(LocalDateTime.of(2022, 11, 7, 7, 7))));
  }

  private static WorkingDay newWorkingDay(LocalDateTime activityTimestamp) {
    return new WorkingDay(
        activityTimestamp.toLocalDate(),
        List.of(new Activity(activityTimestamp, Duration.ofMinutes(5), "Lorem ipsum")));
  }

  @Test
  void run_Loads() {
    sut.run();

    assertMenu(true);
    assertForm(false, "", true);
    assertCountdown("00:20:00", 0.0, false);
    assertActivities(
        List.of(
            new ActivityItem("Mittwoch, 16. November 2022"),
            new ActivityItem("16:16 - Lorem ipsum", "Lorem ipsum"),
            new ActivityItem("Dienstag, 15. November 2022"),
            new ActivityItem("15:15 - Lorem ipsum", "Lorem ipsum"),
            new ActivityItem("Montag, 14. November 2022"),
            new ActivityItem("14:14 - Lorem ipsum", "Lorem ipsum"),
            new ActivityItem("Montag, 7. November 2022"),
            new ActivityItem("07:07 - Lorem ipsum", "Lorem ipsum")));
    assertSummary("00:05", "00:05", "00:15", "00:20");
    assertErrors(List.of());
  }

  @Test
  void load_Failed_NotifyErrors() {
    activitiesService.initRecentActivitiesResponses(
        ConfigurableResponses.sequence(
            List.of(RecentActivities.EMPTY, new IllegalStateException("Something went wrong."))));
    sut.run();

    sut.load();

    assertErrors(List.of("Failed to load activities.", "Something went wrong."));
  }

  @Test
  void changeActivityText_IsNotEmpty_LogButtonIsEnabled() {
    sut.run();

    sut.activityTextProperty().set("foobar");

    assertForm(false, "foobar", false);
  }

  @Test
  void changeActivityText_IsEmpty_LogButtonIsDisabled() {
    sut.run();

    sut.activityTextProperty().set("");

    assertForm(false, "", true);
  }

  @Test
  void changeActivityText_IsBlank_LogButtonIsDisabled() {
    sut.run();

    sut.activityTextProperty().set("  ");

    assertForm(false, "  ", true);
  }

  @Test
  void logActivity_CountdownIsNotActive_LeavesFormEnabled() {
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    var loggedActivitiesTracker = activitiesService.getLoggedActivityTracker();
    sut.run();

    sut.activityTextProperty().set("foobar");
    sut.logActivity();

    assertForm(false, "foobar", false);
    assertEquals(
        List.of(new Activity(LocalDateTime.now(clock), Duration.ofMinutes(20), "foobar")),
        loggedActivitiesTracker.data(),
        "Logged activities");
  }

  @Test
  void logActivity_CountdownActive_DisablesForm() {
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    var activityLoggedTracker = activitiesService.getLoggedActivityTracker();
    sut.run();
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(61));

    sut.activityTextProperty().set("foobar");
    sut.logActivity();

    assertForm(true, "foobar", true);
    assertEquals(
        List.of(new Activity(LocalDateTime.now(clock), Duration.ofMinutes(1), "foobar")),
        activityLoggedTracker.data(),
        "Logged activities");
  }

  @Test
  void logActivity_Failed_NotifyError() {
    activitiesService.initLogActivityResponses(
        ConfigurableResponses.always(new IllegalStateException("Something went wrong.")));
    sut.run();

    sut.activityTextProperty().set("foobar");
    sut.logActivity();

    assertForm(false, "foobar", false);
    assertErrors(List.of("Failed to log activity.", "Something went wrong."));
  }

  @Test
  void startCountdown_DisablesFormAndInitializesCountdown() {
    sut.run();
    sut.activityTextProperty().set("foobar");

    sut.startCountdown(Duration.ofMinutes(20));

    assertMenu(false);
    assertForm(true, "foobar", true);
    assertCountdown("00:20:00", 0.0, false);
  }

  @Test
  void progressCountdown_FirstTick_UpdatesCountdown() {
    sut.run();
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));

    sut.progressCountdown(Duration.ofSeconds(3));

    assertMenu(false);
    assertForm(true, "foobar", true);
    assertCountdown("00:00:57", 0.05, false);
  }

  @Test
  void progressCountdown_SecondTick_UpdatesCountdown() {
    sut.run();
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(3));

    sut.progressCountdown(Duration.ofSeconds(3));

    assertMenu(false);
    assertForm(true, "foobar", true);
    assertCountdown("00:00:54", 0.1, false);
  }

  @Test
  void progressCountdown_LastTick_UpdatesCountdown() {
    sut.run();
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(54));

    sut.progressCountdown(Duration.ofSeconds(3));

    assertMenu(false);
    assertForm(true, "foobar", true);
    assertCountdown("00:00:03", 0.95, false);
  }

  @Test
  void progressCountdown_CountdownElapsed_UpdatesCountdownAndNotifies() {
    sut.run();
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(57));

    sut.progressCountdown(Duration.ofSeconds(3));

    assertMenu(false);
    assertForm(false, "foobar", false);
    assertCountdown("00:01:00", 0.0, true);
  }

  @Test
  void progressCountdown_CountdownIsNotActive_DoesNothing() {
    sut.run();
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));
    sut.stopCountdown();

    sut.progressCountdown(Duration.ofSeconds(10));

    assertMenu(true);
    assertForm(false, "foobar", false);
    assertCountdown("00:01:00", 0.0, false);
  }

  @Test
  void progressCountdown_CountdownIsZero_DoesnotCrash() {
    sut.run();
    sut.startCountdown(Duration.ZERO);
    sut.stopCountdown();

    sut.progressCountdown(Duration.ofSeconds(10));

    assertMenu(true);
    assertForm(false, "", true);
    assertCountdown("00:00:00", 0.0, false);
  }

  @Test
  void stopCountdown_EnablesForm() {
    sut.run();
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(15));

    sut.stopCountdown();

    assertMenu(true);
    assertForm(false, "", true);
    assertCountdown("00:00:45", 0.25, false);
  }

  private void assertMenu(boolean stopMenuItemDisable) {
    assertAll(
        "Menu",
        () ->
            assertEquals(
                stopMenuItemDisable,
                sut.stopMenuItemDisableProperty().get(),
                "Stop menu item disable"));
  }

  private void assertForm(boolean formDisable, String activityText, boolean logButtonDisable) {
    assertAll(
        "Form",
        () -> assertEquals(formDisable, sut.formDisableProperty().get(), "Form disable"),
        () -> assertEquals(activityText, sut.activityTextProperty().get(), "Activity text"),
        () ->
            assertEquals(
                logButtonDisable, sut.logButtonDisableProperty().get(), "Log button disable"));
  }

  private void assertCountdown(
      String countdownLabelText, double countdownProgress, boolean countdownElapsed) {
    assertAll(
        "Countdown",
        () ->
            assertEquals(
                countdownLabelText, sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () ->
            assertEquals(
                countdownProgress, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> assertEquals(countdownElapsed, this.countdownElapsed, "Countdown elapsed"));
  }

  private void assertActivities(List<ActivityItem> recentActivities) {
    assertAll(
        "Activities",
        () -> assertEquals(recentActivities, sut.getRecentActivities(), "Recent activities"));
  }

  private void assertSummary(
      String hoursToday, String hoursYesterday, String hoursThisWeek, String hoursThisMonth) {
    assertAll(
        "Summary",
        () -> assertEquals(hoursToday, sut.hoursTodayLabelTextProperty().get(), "Hours today"),
        () ->
            assertEquals(
                hoursYesterday, sut.hoursYesterdayLabelTextProperty().get(), "Hours yesterday"),
        () ->
            assertEquals(
                hoursThisWeek, sut.hoursThisWeekLabelTextProperty().get(), "Hours this week"),
        () ->
            assertEquals(
                hoursThisMonth, sut.hoursThisMonthLabelTextProperty().get(), "Hours this month"));
  }

  private void assertErrors(List<String> errors) {
    assertEquals(errors, this.errors, "Errors");
  }
}
