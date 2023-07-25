/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.ui.activitysampling.ActivityItem;
import de.muspellheim.activitysampling.ui.activitysampling.ActivitySamplingViewModel;
import de.muspellheim.activitysampling.util.ConfigurableResponses;
import de.muspellheim.activitysampling.util.Exceptions;
import de.muspellheim.activitysampling.util.OutputTracker;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ActivitySamplingViewModelTests {

  public static final Locale LOCALE = Locale.GERMANY;
  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2022-11-16T17:17:17Z"), ZoneId.of("Europe/Berlin"));

  private ActivitiesServiceStub activitiesService;
  private ActivitySamplingViewModel sut;
  private OutputTracker<LocalDateTime> countdownElapsed;
  private OutputTracker<Throwable> errorOccurred;
  private OutputTracker<Activity> activityLogged;

  @BeforeEach
  void init() {
    activitiesService = new ActivitiesServiceStub();
    sut = new ActivitySamplingViewModel(activitiesService, LOCALE, CLOCK);
    countdownElapsed = sut.trackCountdownElapsed();
    activityLogged = activitiesService.trackLoggedActivity();
    errorOccurred = sut.trackErrorOccurred();
  }

  @Test
  void new_InitializesState() {
    assertMenu(true);
    assertForm(false, "", "", "", "", true);
    assertCountdown("00:20:00", 0.0, List.of());
    assertRecentActivities(List.of());
    assertTimeSummary("00:00", "00:00", "00:00", "00:00");
  }

  @Test
  void load_Successfully_RefreshesRecentActivitiesAndTimeSummary() {
    activitiesService.initRecentActivitiesResponses(
        ConfigurableResponses.sequence(
            RecentActivities.from(
                LocalDate.of(2022, 11, 16),
                List.of(
                    newActivity(LocalDateTime.of(2022, 11, 16, 16, 16), Duration.ofMinutes(5)),
                    newActivity(LocalDateTime.of(2022, 11, 15, 15, 15), Duration.ofMinutes(10)),
                    newActivity(LocalDateTime.of(2022, 11, 14, 14, 14), Duration.ofMinutes(15)),
                    newActivity(LocalDateTime.of(2022, 11, 7, 7, 7), Duration.ofMinutes(20))))));

    sut.load();

    assertNoError();
    assertRecentActivities(
        List.of(
            newActivityHeader("Mittwoch, 16. November 2022"),
            newActivityItem("16:16 - Foobar (ACME Ltd.) Task #1"),
            newActivityHeader("Dienstag, 15. November 2022"),
            newActivityItem("15:15 - Foobar (ACME Ltd.) Task #1"),
            newActivityHeader("Montag, 14. November 2022"),
            newActivityItem("14:14 - Foobar (ACME Ltd.) Task #1"),
            newActivityHeader("Montag, 7. November 2022"),
            newActivityItem("07:07 - Foobar (ACME Ltd.) Task #1")));
    assertTimeSummary("00:05", "00:10", "00:30", "00:50");
  }

  @Test
  void load_Failed_NotifiesErrorOccurred() {
    activitiesService.initRecentActivitiesResponses(
        ConfigurableResponses.sequence(new IllegalStateException("Something went wrong.")));

    sut.load();

    assertRecentActivities(List.of());
    assertTimeSummary("00:00", "00:00", "00:00", "00:00");
    assertError("Failed to load activities. Something went wrong.");
  }

  @ParameterizedTest
  @CsvSource(
      useHeadersInDisplayName = true,
      textBlock =
          """
          # client , project  , task     , notes    , logButtonDisable
          not empty, not empty, not empty, not empty, false
          ''       , not empty, not empty, not empty, true
          '  '     , not empty, not empty, not empty, true
          not empty, ''       , not empty, not empty, true
          not empty, '  '     , not empty, not empty, true
          not empty, not empty, ''       , not empty, true
          not empty, not empty, '  '     , not empty, true
          not empty, not empty, not empty, ''       , false
          not empty, not empty, not empty, '  '     , false
        """)
  void updateForm_UpdatesLogButtonDisable(
      String client, String project, String task, String notes, boolean logButtonDisable) {
    sut.setClientText(client);
    sut.setProjectText(project);
    sut.setTaskText(task);
    sut.setNotesText(notes);

    assertEquals(logButtonDisable, sut.isLogButtonDisable());
  }

  @Test
  void logActivity_CountdownIsNotActive_LogsActivityAndLeavesFormEnabled() {
    activitiesService.initRecentActivitiesResponses(
        ConfigurableResponses.sequence(RecentActivities.EMPTY, RecentActivities.EMPTY));
    activitiesService.initLogActivityResponses(ConfigurableResponses.always(true));
    sut.load();

    sut.setClientText("ACME Ltd.");
    sut.setProjectText("Foobar");
    sut.setTaskText("Task #1");
    sut.setNotesText("Lorem ipsum");
    sut.logActivity();

    assertNoError();
    assertLoggedActivities(List.of(newActivity(LocalDateTime.now(CLOCK), Duration.ofMinutes(20))));
    assertForm(false, "ACME Ltd.", "Foobar", "Task #1", "Lorem ipsum", false);
  }

  @Test
  void logActivity_CountdownIsActive_LogsActivityAndDisablesForm() {
    activitiesService.initRecentActivitiesResponses(
        ConfigurableResponses.sequence(RecentActivities.EMPTY, RecentActivities.EMPTY));
    activitiesService.initLogActivityResponses(ConfigurableResponses.always(true));
    sut.load();
    sut.startCountdown(Duration.ofMinutes(20));
    sut.progressCountdown(Duration.ofMinutes(20).plusSeconds(1));

    sut.setClientText("ACME Ltd.");
    sut.setProjectText("Foobar");
    sut.setTaskText("Task #1");
    sut.setNotesText("Lorem ipsum");
    sut.logActivity();

    assertNoError();
    assertLoggedActivities(List.of(newActivity(LocalDateTime.now(CLOCK), Duration.ofMinutes(20))));
    assertForm(true, "ACME Ltd.", "Foobar", "Task #1", "Lorem ipsum", false);
  }

  @Test
  void logActivity_Failed_DoesNotLogActivityAndNotifiesErrorOccurred() {
    activitiesService.initRecentActivitiesResponses(
        ConfigurableResponses.sequence(RecentActivities.EMPTY));
    activitiesService.initLogActivityResponses(ConfigurableResponses.always(true));
    activitiesService.initLogActivityResponses(
        ConfigurableResponses.always(new IllegalStateException("Something went wrong.")));
    sut.load();

    sut.setClientText("ACME Ltd.");
    sut.setProjectText("Foobar");
    sut.setTaskText("Task #1");
    sut.setNotesText("Lorem ipsum");
    sut.logActivity();

    assertLoggedActivities(List.of());
    assertForm(false, "ACME Ltd.", "Foobar", "Task #1", "Lorem ipsum", false);
    assertError("Failed to log activity. Something went wrong.");
  }

  @Test
  void startCountdown_InitializesCountdown() {
    sut.load();

    sut.startCountdown(Duration.ofMinutes(20));

    assertMenu(false);
    assertForm(true, "", "", "", "", true);
    assertCountdown("00:20:00", 0.0, List.of());
  }

  @Test
  void startCountdown_IntervalIsZero_DoesNotCrash() {
    sut.load();
    sut.startCountdown(Duration.ZERO);

    assertMenu(false);
    assertForm(true, "", "", "", "", true);
    assertCountdown("00:00:00", 0.0, List.of());
  }

  @Test
  void progressCountdown_Tick_UpdatesCountdown() {
    sut.load();
    sut.startCountdown(Duration.ofMinutes(20));

    sut.progressCountdown(Duration.ofMinutes(5));

    assertMenu(false);
    assertForm(true, "", "", "", "", true);
    assertCountdown("00:15:00", 0.25, List.of());
  }

  @Test
  void progressCountdown_CountdownElapsed_UpdatesCountdownAndNotifies() {
    sut.load();
    sut.startCountdown(Duration.ofMinutes(20));

    sut.progressCountdown(Duration.ofMinutes(20));

    assertMenu(false);
    assertForm(false, "", "", "", "", true);
    assertCountdown("00:20:00", 0.0, List.of(LocalDateTime.now(CLOCK)));
  }

  @Test
  void progressCountdown_CountdownIsNotActive_DoesNothing() {
    sut.load();
    sut.startCountdown(Duration.ofMinutes(20));
    sut.stopCountdown();

    sut.progressCountdown(Duration.ofSeconds(10));

    assertMenu(true);
    assertForm(false, "", "", "", "", true);
    assertCountdown("00:20:00", 0.0, List.of());
  }

  @Test
  void stopCountdown_EnablesForm() {
    sut.load();
    sut.startCountdown(Duration.ofMinutes(20));
    sut.progressCountdown(Duration.ofMinutes(15));

    sut.stopCountdown();

    assertMenu(true);
    assertForm(false, "", "", "", "", true);
    assertCountdown("00:05:00", 0.75, List.of());
  }

  @Test
  void setActivity_UpdatesForm() {
    sut.load();

    sut.setActivity(newActivityItem("N/A"));

    assertForm(false, "ACME Ltd.", "Foobar", "Task #1", "Lorem ipsum", false);
  }

  private static Activity newActivity(LocalDateTime timestamp, Duration duration) {
    return Activity.builder()
        .timestamp(timestamp)
        .duration(duration)
        .client("ACME Ltd.")
        .project("Foobar")
        .task("Task #1")
        .notes("Lorem ipsum")
        .build();
  }

  private static ActivityItem newActivityHeader(String header) {
    return new ActivityItem(header, null, null, null, null);
  }

  private static ActivityItem newActivityItem(String text) {
    return new ActivityItem(text, "ACME Ltd.", "Foobar", "Task #1", "Lorem ipsum");
  }

  private void assertMenu(boolean stopMenuItemDisable) {
    assertEquals(stopMenuItemDisable, sut.isStopMenuItemDisable(), "Stop menu item disable");
  }

  private void assertForm(
      boolean formDisable,
      String clientText,
      String projectText,
      String taskText,
      String notesText,
      boolean logButtonDisable) {
    assertEquals(formDisable, sut.isFormDisable(), "Form disable");
    assertEquals(clientText, sut.getClientText(), "Client text");
    assertEquals(projectText, sut.getProjectText(), "Project text");
    assertEquals(taskText, sut.getTaskText(), "Task text");
    assertEquals(notesText, sut.getNotesText(), "Notes text");
    assertEquals(logButtonDisable, sut.isLogButtonDisable(), "Log button disable");
  }

  private void assertCountdown(
      String countdownLabelText, double countdownProgress, List<LocalDateTime> countdownElapsed) {
    assertEquals(countdownLabelText, sut.getCountdownLabelText(), "Countdown label text");
    assertEquals(countdownProgress, sut.getCountdownProgress(), "Countdown progress");
    assertEquals(countdownElapsed, this.countdownElapsed.data(), "Countdown elapsed");
  }

  private void assertRecentActivities(List<ActivityItem> recentActivities) {
    assertEquals(recentActivities, sut.getRecentActivities(), "Recent activities");
  }

  private void assertTimeSummary(
      String hoursToday, String hoursYesterday, String hoursThisWeek, String hoursThisMonth) {
    assertEquals(hoursToday, sut.getHoursTodayLabelText(), "Hours today");
    assertEquals(hoursYesterday, sut.getHoursYesterdayLabelText(), "Hours yesterday");
    assertEquals(hoursThisWeek, sut.getHoursThisWeekLabelText(), "Hours this week");
    assertEquals(hoursThisMonth, sut.getHoursThisMonthLabelText(), "Hours this month");
  }

  private void assertLoggedActivities(List<Activity> activities) {
    assertEquals(activities, activityLogged.data(), "Logged activities");
  }

  private void assertNoError() {
    assertEquals(
        "",
        this.errorOccurred.data().stream()
            .map(Exceptions::summarizeMessages)
            .collect(Collectors.joining(" ")),
        "Errors occurred");
  }

  private void assertError(String errorMessage) {
    var message = Exceptions.summarizeMessages(this.errorOccurred.data().get(0));
    assertEquals(errorMessage, message, "Error message");
  }
}
