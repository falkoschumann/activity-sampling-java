package de.muspellheim.activitysampling.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.muspellheim.activitysampling.domain.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class ActivitySamplingViewModelTests {
  private ActivitiesService activitiesService;
  private ActivitySamplingViewModel fixture;

  @BeforeAll
  static void initAll() {
    Locale.setDefault(Locale.GERMANY);
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
  }

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

    fixture = new ActivitySamplingViewModel(activitiesService);
    fixture.onCountdownElapsed = mock(Runnable.class);
    fixture.run();
  }

  @Test
  void run_ViewIsInitialized() {
    assertAll(
        () -> assertEquals("", fixture.activityTextProperty().get(), "Activity text"),
        () -> assertTrue(fixture.logButtonDisableProperty().get(), "Log button disable"),
        () ->
            assertEquals(
                List.of(
                    new ActivityItem("Mittwoch, 16. November 2022"),
                    new ActivityItem(
                        "16:16 - Lorem ipsum",
                        new Activity(LocalDateTime.of(2022, 11, 16, 16, 16), "Lorem ipsum"))),
                fixture.getRecentActivities(),
                "Recent activities"));
  }

  @Test
  void changeActivityText_IsNotEmpty_LogButtonIsEnabled() {
    fixture.activityTextProperty().set("foobar");

    assertFalse(fixture.logButtonDisableProperty().get());
  }

  @Test
  void changeActivityText_IsEmpty_LogButtonIsDisabled() {
    fixture.activityTextProperty().set("");

    assertTrue(fixture.logButtonDisableProperty().get());
  }

  @Test
  void changeActivityText_IsBlank_LogButtonIsDisabled() {
    fixture.activityTextProperty().set("  ");

    assertTrue(fixture.logButtonDisableProperty().get());
  }

  @Test
  void logActivity_LogAndRefresh() {
    fixture.activityTextProperty().set("foobar");

    fixture.logActivity();

    var inOrder = inOrder(activitiesService);
    inOrder.verify(activitiesService).logActivity("foobar");
    inOrder.verify(activitiesService).selectRecentActivities();
    assertEquals(2, fixture.getRecentActivities().size(), "Recent activities size");
  }

  @Test
  void setActivity_UpdatesForm() {
    var activity = new Activity(LocalDateTime.of(2022, 11, 16, 16, 16), "Lorem ipsum");
    fixture.setActivity(activity);

    assertAll(
        () -> assertEquals("Lorem ipsum", fixture.activityTextProperty().get(), "Activity text"),
        () -> assertFalse(fixture.logButtonDisableProperty().get(), "Log button disable"));
  }

  @Test
  void startCountdown_InitializeCountdown() {
    fixture.startCountdown(Duration.ofMinutes(20));

    assertAll(
        () ->
            assertEquals(
                "00:20:00", fixture.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.0, fixture.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(fixture.onCountdownElapsed, times(0)).run());
  }

  @Test
  void progressCountdown_FirstTick() {
    fixture.startCountdown(Duration.ofMinutes(1));

    tickCountdown(1);

    assertAll(
        () ->
            assertEquals(
                "00:00:59", fixture.countdownLabelTextProperty().get(), "Countdown label text"),
        () ->
            assertEquals(
                1.0 / 60.0, fixture.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(fixture.onCountdownElapsed, times(0)).run());
  }

  @Test
  void progressCountdown_SecondTick() {
    fixture.startCountdown(Duration.ofMinutes(1));

    tickCountdown(2);

    assertAll(
        () ->
            assertEquals(
                "00:00:58", fixture.countdownLabelTextProperty().get(), "Countdown label text"),
        () ->
            assertEquals(
                2.0 / 60.0, fixture.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(fixture.onCountdownElapsed, times(0)).run());
  }

  @Test
  void progressCountdown_LastTick() {
    fixture.startCountdown(Duration.ofMinutes(1));

    tickCountdown(59);

    assertAll(
        () ->
            assertEquals(
                "00:00:01", fixture.countdownLabelTextProperty().get(), "Countdown label text"),
        () ->
            assertEquals(
                0.983, fixture.countdownProgressProperty().get(), 0.001, "Countdown progress"),
        () -> verify(fixture.onCountdownElapsed, times(0)).run());
  }

  @Test
  void progressCountdown_CountdownElapsed() {
    fixture.startCountdown(Duration.ofMinutes(1));

    tickCountdown(60);

    assertAll(
        () ->
            assertEquals(
                "00:01:00", fixture.countdownLabelTextProperty().get(), "Countdown label text"),
        () -> assertEquals(0.0, fixture.countdownProgressProperty().get(), "Countdown progress"),
        () -> verify(fixture.onCountdownElapsed, times(1)).run());
  }

  private void tickCountdown(int count) {
    for (var i = 0; i < count; i++) {
      fixture.progressCountdown();
    }
  }
}
