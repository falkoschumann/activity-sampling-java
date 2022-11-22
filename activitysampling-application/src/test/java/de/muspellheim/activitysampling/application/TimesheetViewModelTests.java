package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

@ExtendWith(MockitoExtension.class)
class TimesheetViewModelTests {
  @Mock private ActivitiesService activitiesService;

  @InjectMocks private TimesheetViewModel sut;

  @Test
  @Disabled("Not implemented yet")
  void getTimesheet_ReturnEntries() {
    /*
    when(activitiesService.createTimesheet(any(), any()))
        .thenReturn(
            new Timesheet(
                List.of(
                    new WorkingDay(
                        LocalDate.of(2022, 11, 20),
                        List.of(
                            new TimesheetEntry(
                                LocalDate.of(2022, 11, 20), "A1", Duration.ofMinutes(10)),
                            new TimesheetEntry(
                                LocalDate.of(2022, 11, 20), "A2", Duration.ofMinutes(5)))),
                    new WorkingDay<>(
                        LocalDate.of(2022, 11, 21),
                        List.of(
                            new TimesheetEntry(
                                LocalDate.of(2022, 11, 21), "A1", Duration.ofMinutes(5))))),
                Duration.ofMinutes(20)));
    sut.createTimesheet(LocalDate.now(), LocalDate.now());

    assertEquals(
        List.of(
            new TimesheetItem("20.11.2022", "", "00:15"),
            new TimesheetItem("", "A1", "00:10"),
            new TimesheetItem("", "A2", "00:05"),
            new TimesheetItem("21.11.2022", "", "00:05"),
            new TimesheetItem("", "A1", "00:05")),
        sut.getTimesheetItems());
    */
  }

  @Test
  @Disabled
  void logActivity_Failed_NotifyError() {
    /*
    doThrow(new IllegalStateException("Something went wrong."))
      .when(activitiesService)
      .logActivity(any());
    sut.activityTextProperty().set("foobar");

    sut.logActivity();

    verify(onError).accept("Failed to log activity. Something went wrong.");
     */
  }
}
