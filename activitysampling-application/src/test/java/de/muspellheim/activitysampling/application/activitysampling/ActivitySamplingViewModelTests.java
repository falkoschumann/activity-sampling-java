package de.muspellheim.activitysampling.application.activitysampling;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.muspellheim.activitysampling.domain.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

@ExtendWith(MockitoExtension.class)
class ActivitySamplingViewModelTests {
  @Mock private ActivitiesService activitiesService;
  @Mock private Runnable onCountdownElapsed;
  @Mock private Consumer<String> onError;
  @InjectMocks private ActivitySamplingViewModel sut;

  @BeforeEach
  void init() {
    var recentActivities = new RecentActivities(LocalDate.parse("2022-11-16"));
    recentActivities.apply(
        new Activity(
            LocalDateTime.parse("2022-11-16T16:16"), Duration.ofMinutes(5), "Lorem ipsum"));
    recentActivities.apply(
        new Activity(
            LocalDateTime.parse("2022-11-15T15:15"), Duration.ofMinutes(5), "Lorem ipsum"));
    recentActivities.apply(
        new Activity(
            LocalDateTime.parse("2022-11-14T14:14"), Duration.ofMinutes(5), "Lorem ipsum"));
    recentActivities.apply(
        new Activity(
            LocalDateTime.parse("2022-11-07T07:07"), Duration.ofMinutes(5), "Lorem ipsum"));
    when(activitiesService.getRecentActivities()).thenReturn(recentActivities);
    sut.addOnCountdownElapsedListener(v -> onCountdownElapsed.run());
    sut.addOnErrorListener(onError);
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
    doThrow(new IllegalStateException("Something went wrong."))
        .when(activitiesService)
        .getRecentActivities();

    sut.load();

    verify(onError).accept("Failed to load activities. Something went wrong.");
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
    sut.activityTextProperty().set("foobar");
    sut.logActivity();

    assertAll(
        () -> {
          var inOrder = inOrder(activitiesService);
          inOrder.verify(activitiesService).logActivity("foobar", Duration.ofMinutes(20));
          inOrder.verify(activitiesService).getRecentActivities();
        },
        () -> assertFalse(sut.formDisableProperty().get(), "Form disable"),
        () -> assertFalse(sut.logButtonDisableProperty().get(), "Log button disable"));
  }

  @Test
  void logActivity_CountdownActive_DisablesForm() {
    sut.startCountdown(Duration.ofMinutes(1));
    sut.progressCountdown(Duration.ofSeconds(61));

    sut.activityTextProperty().set("foobar");
    sut.logActivity();

    assertAll(
        () -> {
          var inOrder = inOrder(activitiesService);
          inOrder.verify(activitiesService).logActivity("foobar", Duration.ofMinutes(1));
          inOrder.verify(activitiesService).getRecentActivities();
        },
        () -> assertTrue(sut.formDisableProperty().get(), "Form disable"),
        () -> assertTrue(sut.logButtonDisableProperty().get(), "Log button disable"));
  }

  @Test
  void logActivity_Failed_NotifyError() {
    doThrow(new IllegalStateException("Something went wrong."))
        .when(activitiesService)
        .logActivity(any(), any());
    sut.activityTextProperty().set("foobar");

    sut.logActivity();

    verify(onError).accept("Failed to log activity. Something went wrong.");
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
        () -> verify(onCountdownElapsed, never()).run());
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
        () -> verify(onCountdownElapsed, never()).run());
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
        () -> verify(onCountdownElapsed, never()).run());
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
        () -> verify(onCountdownElapsed, never()).run());
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
        () -> verify(onCountdownElapsed, times(1)).run());
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
        () -> verify(onCountdownElapsed, never()).run());
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
        () -> verify(onCountdownElapsed, never()).run());
  }
}
