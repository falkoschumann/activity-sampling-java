package de.muspellheim.activitysampling.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.muspellheim.activitysampling.domain.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class ActivitySamplingViewModelTests {
  private ActivitiesService activitiesService;
  private ActivitySamplingViewModel sut;

  @BeforeEach
  void init() {
    activitiesService = mock(ActivitiesService.class);
    when(activitiesService.selectRecentActivities())
        .thenReturn(
            new RecentActivities(
                List.of(
                    new WorkingDay(
                        LocalDate.of(2022, 11, 16),
                        List.of(
                            new Activity(
                                LocalDateTime.of(2022, 11, 16, 16, 16), "Lorem ipsum"))))));

    sut = new ActivitySamplingViewModel(activitiesService);
    sut.onCountdownElapsed = mock(Runnable.class);
    sut.run();
  }

  @Test
  void run_ViewIsInitialized() {
    assertAll(
        () -> assertEquals("", sut.activityTextProperty().get(), "Activity text"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                List.of(
                    new ActivityItem("Mittwoch, 16. November 2022"),
                    new ActivityItem(
                        "16:16 - Lorem ipsum",
                        new Activity(LocalDateTime.of(2022, 11, 16, 16, 16), "Lorem ipsum"))),
                sut.getRecentActivities(),
                "Recent activities"));
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
  void logActivity_LogsAndRefreshes() {
    sut.activityTextProperty().set("foobar");

    sut.logActivity();

    var inOrder = inOrder(activitiesService);
    inOrder.verify(activitiesService).logActivity("foobar");
    inOrder.verify(activitiesService).selectRecentActivities();
    assertEquals(2, sut.getRecentActivities().size(), "Recent activities size");
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
    sut.startCountdown(Duration.ofMinutes(20));

    assertAll(
        () ->
            assertEquals(
                "00:20:00", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(sut.onCountdownElapsed, times(0)).run());
  }

  @Test
  void progressCountdown_FirstTick_UpdatesCountdown() {
    sut.startCountdown(Duration.ofMinutes(1));

    tickCountdown(1);

    assertAll(
        () ->
            assertEquals(
                "00:00:59", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(1.0 / 60.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(sut.onCountdownElapsed, times(0)).run());
  }

  @Test
  void progressCountdown_SecondTick_UpdatesCountdown() {
    sut.startCountdown(Duration.ofMinutes(1));

    tickCountdown(2);

    assertAll(
        () ->
            assertEquals(
                "00:00:58", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(2.0 / 60.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(sut.onCountdownElapsed, times(0)).run());
  }

  @Test
  void progressCountdown_LastTick_UpdatesCountdown() {
    sut.startCountdown(Duration.ofMinutes(1));

    tickCountdown(59);

    assertAll(
        () ->
            assertEquals(
                "00:00:01", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () ->
            assertEquals(0.983, sut.countdownProgressProperty().get(), 0.001, "Countdown progress"),
        () -> verify(sut.onCountdownElapsed, times(0)).run());
  }

  @Test
  void progressCountdown_CountdownElapsed_UpdatesCountdownAndNotifies() {
    sut.startCountdown(Duration.ofMinutes(1));

    tickCountdown(60);

    assertAll(
        () ->
            assertEquals(
                "00:01:00", sut.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.0, sut.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(sut.onCountdownElapsed, times(1)).run());
  }

  private void tickCountdown(int count) {
    for (var i = 0; i < count; i++) {
      sut.progressCountdown();
    }
  }
}
