/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.activitysampling.ui.timesheet.TimesheetItem;
import de.muspellheim.activitysampling.ui.timesheet.TimesheetViewModel;
import de.muspellheim.activitysampling.util.ConfigurableResponses;
import de.muspellheim.activitysampling.util.Exceptions;
import de.muspellheim.activitysampling.util.OutputTracker;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimesheetViewModelTests {
  private ActivitiesServiceStub activitiesService;
  private TimesheetViewModel sut;
  private OutputTracker<Throwable> errorOccurred;

  @BeforeEach
  void init() {
    activitiesService = new ActivitiesServiceStub();
    sut = new TimesheetViewModel(activitiesService, Locale.GERMANY);
    errorOccurred = sut.trackErrorOccurred();
  }

  @Test
  void new_InitializesState() {
    assertTimesheet(List.of(), "00:00");
  }

  @Test
  void load_Successfully_RefreshesTimesheet() {
    var timesheet = newTimesheet();
    activitiesService.initTimesheetResponses(ConfigurableResponses.always(timesheet));

    sut.load(null, null);

    assertTimesheet(
        List.of(
            new TimesheetItem("14.04.2023", "ACME Ltd.", "Foo", "Lorem ipsum", "00:20"),
            new TimesheetItem("14.04.2023", "ACME Ltd.", "Bar", "Lorem ipsum", "00:20")),
        "00:40");
    assertNoError();
  }

  @Test
  void load_Failed_NotifiesErrorOccurred() {
    activitiesService.initTimesheetResponses(
        ConfigurableResponses.sequence(new IllegalStateException("Something went wrong.")));

    sut.load(null, null);

    assertTimesheet(List.of(), "00:00");
    assertError("Failed to load timesheet. Something went wrong.");
  }

  private static Timesheet newTimesheet() {
    return new Timesheet(
        List.of(
            Timesheet.Entry.builder()
                .date(LocalDate.of(2023, 4, 14))
                .client("ACME Ltd.")
                .project("Foo")
                .task("Lorem ipsum")
                .hours(Duration.ofMinutes(20))
                .build(),
            Timesheet.Entry.builder()
                .date(LocalDate.of(2023, 4, 14))
                .client("ACME Ltd.")
                .project("Bar")
                .task("Lorem ipsum")
                .hours(Duration.ofMinutes(20))
                .build()));
  }

  private void assertTimesheet(List<TimesheetItem> items, String total) {
    assertEquals(items, sut.getTimesheetItems(), "Timesheet items");
    assertEquals(total, sut.getTotalLabelText(), "Timesheet total");
  }

  private void assertNoError() {
    assertEquals(List.of(), this.errorOccurred.data(), "Errors occurred");
  }

  private void assertError(String errorMessage) {
    var message = Exceptions.summarizeMessages(this.errorOccurred.data().get(0));
    assertEquals(errorMessage, message, "Error message");
  }
}
