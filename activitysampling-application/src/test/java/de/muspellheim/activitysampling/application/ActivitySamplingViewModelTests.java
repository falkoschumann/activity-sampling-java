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
  }

  @Test
  void run_ViewIsInitialized() {
    fixture.run();

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
}
