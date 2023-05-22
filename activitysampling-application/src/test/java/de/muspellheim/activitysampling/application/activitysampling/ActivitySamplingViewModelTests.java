/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.application.ActivitiesServiceStub;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.common.util.ConfigurableResponses;
import de.muspellheim.common.util.Exceptions;
import de.muspellheim.common.util.OutputTracker;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivitySamplingViewModelTests {
  private ActivitiesServiceStub activitiesService;
  private Clock clock;
  private ActivitySamplingViewModel sut;
  private OutputTracker<LocalDateTime> countdownElapsed;
  private OutputTracker<Throwable> errorOccurred;

  @BeforeEach
  void init() {
    activitiesService = new ActivitiesServiceStub();
    clock = Clock.fixed(Instant.parse("2022-11-16T17:17:17Z"), ZoneId.of("Europe/Berlin"));
    sut = new ActivitySamplingViewModel(activitiesService, Locale.GERMANY, clock);
    countdownElapsed = sut.getCountdownElapsedTracker();
    errorOccurred = sut.getErrorOccurredTracker();
  }

  @Test
  void load_Successfully() {
    activitiesService.initRecentActivitiesResponses(
        ConfigurableResponses.sequence(
            RecentActivities.of(
                LocalDate.of(2022, 11, 16),
                List.of(
                    new Activity(
                        LocalDateTime.of(2022, 11, 16, 16, 16),
                        Duration.ofMinutes(5),
                        "Lorem ipsum"),
                    new Activity(
                        LocalDateTime.of(2022, 11, 15, 15, 15),
                        Duration.ofMinutes(10),
                        "Lorem ipsum"),
                    new Activity(
                        LocalDateTime.of(2022, 11, 14, 14, 14),
                        Duration.ofMinutes(15),
                        "Lorem ipsum"),
                    new Activity(
                        LocalDateTime.of(2022, 11, 7, 7, 7),
                        Duration.ofMinutes(20),
                        "Lorem ipsum")))));

    sut.load();

    assertMenu(true);
    assertForm(false, "", true);
    assertCountdown("00:20:00", 0.0, List.of());
    assertEquals(
        List.of(
            new ActivityItem("Mittwoch, 16. November 2022"),
            new ActivityItem("16:16 - Lorem ipsum", "Lorem ipsum"),
            new ActivityItem("Dienstag, 15. November 2022"),
            new ActivityItem("15:15 - Lorem ipsum", "Lorem ipsum"),
            new ActivityItem("Montag, 14. November 2022"),
            new ActivityItem("14:14 - Lorem ipsum", "Lorem ipsum"),
            new ActivityItem("Montag, 7. November 2022"),
            new ActivityItem("07:07 - Lorem ipsum", "Lorem ipsum")),
        sut.getRecentActivities(),
        "Recent activities");
    assertEquals("00:05", sut.hoursTodayTextProperty().get(), "Hours today");
    assertEquals("00:10", sut.hoursYesterdayTextProperty().get(), "Hours yesterday");
    assertEquals("00:30", sut.hoursThisWeekTextProperty().get(), "Hours this week");
    assertEquals("00:50", sut.hoursThisMonthTextProperty().get(), "Hours this month");
    assertNoError();
  }

  @Test
  void load_Failed_NotifyErrorOccurred() {
    activitiesService.initRecentActivitiesResponses(
        ConfigurableResponses.sequence(new IllegalStateException("Something went wrong.")));

    sut.load();

    assertError("Failed to load activities. Something went wrong.");
  }

  @Test
  void setActivityText_NotEmpty_LogButtonIsEnabled() {
    sut.load();

    sut.setActivityText("foobar");

    assertForm(false, "foobar", false);
  }

  @Test
  void setActivityText_Empty_LogButtonIsDisabled() {
    sut.load();

    sut.setActivityText("");

    assertForm(false, "", true);
  }

  @Test
  void setActivityText_Blank_LogButtonIsDisabled() {
    sut.load();

    sut.setActivityText("  ");

    assertForm(false, "  ", true);
  }

  @Test
  void logActivity_CountdownIsNotActive_LeavesFormEnabled() {
    activitiesService.initLogActivityResponses(ConfigurableResponses.always(true));
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    var loggedActivitiesTracker = activitiesService.getLoggedActivityTracker();
    sut.load();

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
    activitiesService.initLogActivityResponses(ConfigurableResponses.always(true));
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    var activityLoggedTracker = activitiesService.getLoggedActivityTracker();
    sut.load();
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(61));

    sut.activityTextProperty().set("foobar");
    sut.logActivity();

    assertForm(true, "foobar", false);
    assertEquals(
        List.of(new Activity(LocalDateTime.now(clock), Duration.ofMinutes(1), "foobar")),
        activityLoggedTracker.data(),
        "Logged activities");
  }

  @Test
  void logActivity_Failed_NotifyError() {
    activitiesService.initRecentActivitiesResponses(
        ConfigurableResponses.sequence(RecentActivities.EMPTY));
    activitiesService.initLogActivityResponses(ConfigurableResponses.always(true));
    activitiesService.initLogActivityResponses(
        ConfigurableResponses.always(new IllegalStateException("Something went wrong.")));
    sut.load();

    sut.activityTextProperty().set("foobar");
    sut.logActivity();

    assertForm(false, "foobar", false);
    assertError("Failed to log activity. Something went wrong.");
  }

  @Test
  void startCountdown_DisablesFormAndInitializesCountdown() {
    sut.load();
    sut.activityTextProperty().set("foobar");

    sut.startCountdown(Duration.ofMinutes(20));

    assertMenu(false);
    assertForm(true, "foobar", false);
    assertCountdown("00:20:00", 0.0, List.of());
  }

  @Test
  void progressCountdown_FirstTick_UpdatesCountdown() {
    sut.load();
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));

    sut.progressCountdown(Duration.ofSeconds(3));

    assertMenu(false);
    assertForm(true, "foobar", false);
    assertCountdown("00:00:57", 0.05, List.of());
  }

  @Test
  void progressCountdown_SecondTick_UpdatesCountdown() {
    sut.load();
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(3));

    sut.progressCountdown(Duration.ofSeconds(3));

    assertMenu(false);
    assertForm(true, "foobar", false);
    assertCountdown("00:00:54", 0.1, List.of());
  }

  @Test
  void progressCountdown_LastTick_UpdatesCountdown() {
    sut.load();
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(54));

    sut.progressCountdown(Duration.ofSeconds(3));

    assertMenu(false);
    assertForm(true, "foobar", false);
    assertCountdown("00:00:03", 0.95, List.of());
  }

  @Test
  void progressCountdown_CountdownElapsed_UpdatesCountdownAndNotifies() {
    sut.load();
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(57));

    sut.progressCountdown(Duration.ofSeconds(3));

    assertMenu(false);
    assertForm(false, "foobar", false);
    assertCountdown("00:01:00", 0.0, List.of(LocalDateTime.now(clock)));
  }

  @Test
  void progressCountdown_CountdownIsNotActive_DoesNothing() {
    sut.load();
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));
    sut.stopCountdown();

    sut.progressCountdown(Duration.ofSeconds(10));

    assertMenu(true);
    assertForm(false, "foobar", false);
    assertCountdown("00:01:00", 0.0, List.of());
  }

  @Test
  void progressCountdown_CountdownIsZero_DoesNotCrash() {
    sut.load();
    sut.startCountdown(Duration.ZERO);
    sut.stopCountdown();

    sut.progressCountdown(Duration.ofSeconds(10));

    assertMenu(true);
    assertForm(false, "", true);
    assertCountdown("00:00:00", 0.0, List.of());
  }

  @Test
  void stopCountdown_EnablesForm() {
    sut.load();
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(15));

    sut.stopCountdown();

    assertMenu(true);
    assertForm(false, "", true);
    assertCountdown("00:00:45", 0.25, List.of());
  }

  private void assertMenu(boolean stopMenuItemDisable) {
    assertEquals(
        stopMenuItemDisable, sut.stopMenuItemDisableProperty().get(), "Stop menu item disable");
  }

  private void assertForm(boolean formDisable, String activityText, boolean logButtonDisable) {
    assertEquals(formDisable, sut.formDisableProperty().get(), "Form disable");
    assertEquals(activityText, sut.activityTextProperty().get(), "Activity text");
    assertEquals(logButtonDisable, sut.logButtonDisableProperty().get(), "Log button disable");
  }

  private void assertCountdown(
      String countdownLabelText, double countdownProgress, List<LocalDateTime> countdownElapsed) {
    assertEquals(
        countdownLabelText, sut.countdownLabelTextProperty().get(), "Countdown label text");
    assertEquals(countdownProgress, sut.countdownProgressProperty().get(), "Countdown progress");
    assertEquals(countdownElapsed, this.countdownElapsed.data(), "Countdown elapsed");
  }

  private void assertNoError() {
    assertEquals(List.of(), this.errorOccurred.data(), "Errors occurred");
  }

  private void assertError(String errorMessage) {
    var message = Exceptions.summarizeMessages(this.errorOccurred.data().get(0));
    assertEquals(errorMessage, message, "Error message");
  }
}
