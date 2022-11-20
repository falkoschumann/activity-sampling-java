package de.muspellheim.activitysampling.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.muspellheim.activitysampling.domain.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import org.junit.jupiter.api.*;

class ActivitySamplingViewModelTests {
  private ActivitiesService activitiesService;
  private ActivitySamplingViewModel sut;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void init() {
    activitiesService = mock(ActivitiesService.class);
    when(activitiesService.selectRecentActivities())
        .thenReturn(
            new RecentActivities(
                List.of(
                    new WorkingDay(
                        LocalDate.of(2022, 11, 16),
                        List.of(
                            new Activity(LocalDateTime.of(2022, 11, 16, 16, 16), "Lorem ipsum")))),
                new TimeSummary(
                    Duration.ofMinutes(5),
                    Duration.ofMinutes(10),
                    Duration.ofMinutes(15),
                    Duration.ofMinutes(20))));

    sut = new ActivitySamplingViewModel(activitiesService);
    sut.onCountdownElapsed = mock(Runnable.class);
    sut.onError = mock(Consumer.class);
    sut.run();
  }

  @Test
  void run_ViewIsInitialized() {
    assertAll(
        () -> assertEquals("", sut.activityTextProperty().get(), "Activity text"),
        () -> assertFalse(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                List.of(
                    new ActivityItem("Mittwoch, 16. November 2022"),
                    new ActivityItem(
                        "16:16 - Lorem ipsum",
                        new Activity(LocalDateTime.of(2022, 11, 16, 16, 16), "Lorem ipsum"))),
                sut.getRecentActivities(),
                "Recent activities"),
        () -> assertEquals("00:05", sut.hoursTodayLabelTextProperty().get()),
        () -> assertEquals("00:10", sut.hoursYesterdayLabelTextProperty().get()),
        () -> assertEquals("00:15", sut.hoursThisWeekLabelTextProperty().get()),
        () -> assertEquals("00:20", sut.hoursThisMonthLabelTextProperty().get()));
  }

  @Test
  void load_Failed_NotifyError() {
    doThrow(new IllegalStateException("Something went wrong."))
        .when(activitiesService)
        .selectRecentActivities();

    sut.load();

    verify(sut.onError).accept("Failed to load activities. Something went wrong.");
  }

  @Test
  void changeActivityText_IsNotEmpty_LogButtonIsEnabled() {
    sut.activityTextProperty().set("foobar");

    assertAll(
        () -> assertFalse(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertFalse(sut.logButtonDisableProperty().get()));
  }

  @Test
  void changeActivityText_IsEmpty_LogButtonIsDisabled() {
    sut.activityTextProperty().set("");

    assertAll(
        () -> assertFalse(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get()));
  }

  @Test
  void changeActivityText_IsBlank_LogButtonIsDisabled() {
    sut.activityTextProperty().set("  ");

    assertAll(
        () -> assertFalse(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get()));
  }

  @Test
  void logActivity_LogsAndRefreshes() {
    sut.activityTextProperty().set("foobar");
    sut.logActivity();

    assertAll(
        () -> {
          var inOrder = inOrder(activitiesService);
          inOrder.verify(activitiesService).logActivity("foobar");
          inOrder.verify(activitiesService).selectRecentActivities();
        },
        () -> assertEquals(2, sut.getRecentActivities().size(), "Recent activities size"),
        () -> assertFalse(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertFalse(sut.logButtonDisableProperty().get(), "Log button disable"));
  }

  @Test
  void logActivity_CountdownActive_DisableForm() {
    sut.startCountdown(Duration.ofMinutes(1));
    tickCountdown(61);

    sut.activityTextProperty().set("foobar");
    sut.logActivity();

    assertAll(
        () -> {
          var inOrder = inOrder(activitiesService);
          inOrder.verify(activitiesService).logActivity("foobar");
          inOrder.verify(activitiesService).selectRecentActivities();
        },
        () -> assertEquals(2, sut.getRecentActivities().size(), "Recent activities size"),
        () -> assertTrue(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"));
  }

  @Test
  void logActivity_Failed_NotifyError() {
    doThrow(new IllegalStateException("Something went wrong."))
        .when(activitiesService)
        .logActivity(any());
    sut.activityTextProperty().set("foobar");

    sut.logActivity();

    verify(sut.onError).accept("Failed to log activity. Something went wrong.");
  }

  @Test
  void setActivity_UpdatesForm() {
    var activity = new Activity(LocalDateTime.of(2022, 11, 16, 16, 16), "Lorem ipsum");
    sut.setActivity(activity);

    assertAll(
        () -> assertEquals("Lorem ipsum", sut.activityTextProperty().get(), "Activity text"),
        () -> assertFalse(sut.logButtonDisableProperty().get(), "Log button disable"));
  }

  @Test
  void startCountdown_InitializesCountdown() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(20));

    assertAll(
        () -> assertTrue(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:20:00", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(sut.onCountdownElapsed, never()).run());
  }

  @Test
  void progressCountdown_FirstTick_UpdatesCountdown() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));

    tickCountdown(1);

    assertAll(
        () -> assertTrue(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:00:59", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(1.0 / 60.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(sut.onCountdownElapsed, never()).run());
  }

  @Test
  void progressCountdown_SecondTick_UpdatesCountdown() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));

    tickCountdown(2);

    assertAll(
        () -> assertTrue(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:00:58", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(2.0 / 60.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(sut.onCountdownElapsed, never()).run());
  }

  @Test
  void progressCountdown_LastTick_UpdatesCountdown() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));

    tickCountdown(59);

    assertAll(
        () -> assertTrue(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:00:01", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () ->
            assertEquals(0.983, sut.countdownProgressProperty().get(), 0.001, "Countdown progress"),
        () -> verify(sut.onCountdownElapsed, never()).run());
  }

  @Test
  void progressCountdown_CountdownElapsed_UpdatesCountdownAndNotifies() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));

    tickCountdown(60);

    assertAll(
        () -> assertFalse(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertFalse(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:01:00", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(sut.onCountdownElapsed, times(1)).run());
  }

  @Test
  void stopCountdown_EnablesForm() {
    sut.activityTextProperty().set("foobar");
    sut.startCountdown(Duration.ofMinutes(1));
    tickCountdown(15);

    sut.stopCountdown();

    assertAll(
        () -> assertFalse(sut.activityDisableProperty().get(), "Activity disable"),
        () -> assertFalse(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                "00:00:45", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.25, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(sut.onCountdownElapsed, never()).run());
  }

  private void tickCountdown(int count) {
    for (var i = 0; i < count; i++) {
      sut.progressCountdown();
    }
  }
}
