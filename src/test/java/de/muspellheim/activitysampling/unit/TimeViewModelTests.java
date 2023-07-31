/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.domain.TimeReport;
import de.muspellheim.activitysampling.ui.time.TimeItem;
import de.muspellheim.activitysampling.ui.time.TimeViewModel;
import de.muspellheim.activitysampling.util.ConfigurableResponses;
import de.muspellheim.activitysampling.util.Exceptions;
import de.muspellheim.activitysampling.util.OutputTracker;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimeViewModelTests {

  private ActivitiesServiceStub activitiesService;
  private TimeViewModel sut;
  private OutputTracker<Throwable> errorOccurred;

  @BeforeEach
  void init() {
    activitiesService = new ActivitiesServiceStub();
    sut = new TimeViewModel(activitiesService);
    errorOccurred = sut.trackErrorOccurred();
  }

  @Test
  void new_InitializesState() {
    assertReport(List.of(), "00:00");
  }

  @Test
  void load_Successfully_RefreshesReport() {
    var report = newReport();
    activitiesService.initReportResponses(ConfigurableResponses.always(report));

    sut.load(null, null, TimeViewModel.Scope.PROJECTS);

    assertReport(
        List.of(
            TimeItem.builder().name("Bar").client("F.O.W.L.").hours("00:15").build(),
            TimeItem.builder().name("Foo").client("ACME Ltd.").hours("00:15").build()),
        "00:30");
    assertNoError();
  }

  @Test
  void load_Failed_NotifiesErrorOccurred() {
    activitiesService.initReportResponses(
        ConfigurableResponses.sequence(new IllegalStateException("Something went wrong.")));

    sut.load(null, null, null);

    assertReport(List.of(), "00:00");
    assertError("Failed to load report. Something went wrong.");
  }

  private static TimeReport newReport() {
    return new TimeReport(
        List.of(
            TimeReport.Entry.builder()
                .client("ACME Ltd.")
                .project("Foo")
                .task("Lorem ipsum")
                .hours(Duration.ofMinutes(15))
                .build(),
            TimeReport.Entry.builder()
                .client("F.O.W.L.")
                .project("Bar")
                .task("Lorem ipsum")
                .hours(Duration.ofMinutes(15))
                .build()));
  }

  private void assertReport(List<TimeItem> items, String total) {
    assertEquals(items, sut.getTimeItems(), "Time items");
    assertEquals(total, sut.getTotalLabelText(), "Time total");
  }

  private void assertNoError() {
    assertEquals(List.of(), this.errorOccurred.data(), "Errors occurred");
  }

  private void assertError(String errorMessage) {
    var message = Exceptions.summarizeMessages(this.errorOccurred.data().get(0));
    assertEquals(errorMessage, message, "Error message");
  }
}
