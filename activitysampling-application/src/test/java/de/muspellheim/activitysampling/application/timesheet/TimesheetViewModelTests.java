package de.muspellheim.activitysampling.application.timesheet;

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
class TimesheetViewModelTests {
  @Mock private ActivitiesService activitiesService;

  @Mock private Consumer<String> onError;

  @InjectMocks private TimesheetViewModel sut;

  @BeforeEach
  void init() {
    when(activitiesService.createTimesheet(any(), any()))
        .thenReturn(
            new Timesheet(
                List.of(
                    new TimesheetByDay(
                        LocalDate.of(2022, 11, 20),
                        List.of(
                            new TimesheetEntry("A1", Duration.ofMinutes(10)),
                            new TimesheetEntry("A2", Duration.ofMinutes(5))),
                        Duration.ofMinutes(15)),
                    new TimesheetByDay(
                        LocalDate.of(2022, 11, 21),
                        List.of(new TimesheetEntry("A1", Duration.ofMinutes(5))),
                        Duration.ofMinutes(5))),
                Duration.ofMinutes(20)));
  }

  @Test
  void update_UpdatesItemsAndTotal() {
    sut.update();

    assertAll(
        "Updates items and total",
        () ->
            assertEquals(
                List.of(
                    new TimesheetItem("20.11.2022", "Total", "00:15"),
                    new TimesheetItem("", "A1", "00:10"),
                    new TimesheetItem("", "A2", "00:05"),
                    new TimesheetItem("21.11.2022", "Total", "00:05"),
                    new TimesheetItem("", "A1", "00:05")),
                sut.getTimesheetItems()),
        () -> assertEquals("00:20", sut.totalProperty().get()));
  }

  @Test
  void update_Failed_NotifyError() {
    reset(activitiesService);
    doThrow(new IllegalStateException("Something went wrong."))
        .when(activitiesService)
        .createTimesheet(any(), any());

    sut.update();

    verify(onError).accept("Failed to update timesheet. Something went wrong.");
  }
}
