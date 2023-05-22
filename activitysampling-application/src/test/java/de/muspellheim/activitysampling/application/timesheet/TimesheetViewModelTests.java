/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.timesheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.muspellheim.activitysampling.application.ActivitiesServiceStub;
import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.common.util.ConfigurableResponses;
import de.muspellheim.common.util.Exceptions;
import de.muspellheim.common.util.OutputTracker;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
    var clock = Clock.fixed(Instant.parse("2023-04-16T14:05:00Z"), ZoneId.systemDefault());
    sut = new TimesheetViewModel(activitiesService, Locale.GERMANY, clock);
    errorOccurred = sut.getErrorOccurredTracker();
  }

  @Test
  void load_InitializesWithCurrentWeek() {
    var timesheet =
        new Timesheet(
            List.of(
                new Timesheet.Entry(
                    LocalDate.of(2023, 4, 14), "Lorem ipsum", Duration.ofMinutes(20))),
            Duration.ofMinutes(20));
    activitiesService.initTimesheetResponses(ConfigurableResponses.always(timesheet));

    sut.load();

    assertTitleAndPeriod("This Week: ", "10.04.2023 - 16.04.2023", ChronoUnit.WEEKS);
    assertEquals(
        List.of(new TimesheetItem("14.04.2023", "Lorem ipsum", "00:20")),
        sut.getTimesheetItems(),
        "Timesheet items");
    assertEquals("00:20", sut.totalProperty().get(), "Total");
    assertNoError();
  }

  @Test
  void load_Failed_NotifyErrors() {
    activitiesService.initTimesheetResponses(
        ConfigurableResponses.sequence(
            Timesheet.EMPTY, new IllegalStateException("Something went wrong.")));
    sut.load();

    sut.load();

    assertError("Failed to load timesheet. Something went wrong.");
  }

  @Test
  void changePeriod_FromWeekToDay_DisplaysMonday() {
    sut.load();

    sut.setPeriod(ChronoUnit.DAYS);

    assertTitleAndPeriod("This Day: ", "10.04.2023", ChronoUnit.DAYS);
  }

  @Test
  void changePeriod_FromWeekToMonth_DisplaysCurrentMonth() {
    sut.load();

    sut.setPeriod(ChronoUnit.MONTHS);

    assertTitleAndPeriod("This Month: ", "01.04.2023 - 30.04.2023", ChronoUnit.MONTHS);
  }

  @Test
  void changePeriod_FromDayToWeek_DisplaysCurrentWeek() {
    sut.load();
    sut.setPeriod(ChronoUnit.DAYS);

    sut.setPeriod(ChronoUnit.WEEKS);

    assertTitleAndPeriod("This Week: ", "10.04.2023 - 16.04.2023", ChronoUnit.WEEKS);
  }

  @Test
  void changePeriod_UnsupportedPeriod_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> sut.setPeriod(ChronoUnit.HOURS));
  }

  @Test
  void back_PeriodIsDay_DisplaysYesterday() {
    sut.load();
    sut.setPeriod(ChronoUnit.DAYS);

    sut.back();

    assertTitleAndPeriod("This Day: ", "09.04.2023", ChronoUnit.DAYS);
  }

  @Test
  void back_PeriodIsWeek_DisplaysLastWeek() {
    sut.load();

    sut.back();

    assertTitleAndPeriod("This Week: ", "03.04.2023 - 09.04.2023", ChronoUnit.WEEKS);
  }

  @Test
  void back_PeriodIsMonth_DisplaysLastMonth() {
    sut.load();
    sut.setPeriod(ChronoUnit.MONTHS);

    sut.back();

    assertTitleAndPeriod("This Month: ", "01.03.2023 - 31.03.2023", ChronoUnit.MONTHS);
  }

  @Test
  void forward_PeriodIsDay_DisplaysTomorrow() {
    sut.setPeriod(ChronoUnit.DAYS);

    sut.forward();

    assertTitleAndPeriod("This Day: ", "11.04.2023", ChronoUnit.DAYS);
  }

  @Test
  void forward_PeriodIsWeek_DisplaysNextWeek() {
    sut.forward();

    assertTitleAndPeriod("This Week: ", "17.04.2023 - 23.04.2023", ChronoUnit.WEEKS);
  }

  @Test
  void forward_PeriodIsMonth_DisplaysNextMonth() {
    sut.setPeriod(ChronoUnit.MONTHS);

    sut.forward();

    assertTitleAndPeriod("This Month: ", "01.05.2023 - 31.05.2023", ChronoUnit.MONTHS);
  }

  private void assertTitleAndPeriod(String title1, String title2, ChronoUnit period) {
    assertEquals(title1, sut.title1Property().get(), "Title 1");
    assertEquals(title2, sut.title2Property().get(), "Title 2");
    assertEquals(period, sut.periodProperty().get(), "Period");
  }

  private void assertNoError() {
    assertEquals(List.of(), this.errorOccurred.data(), "Errors occurred");
  }

  private void assertError(String errorMessage) {
    var message = Exceptions.summarizeMessages(this.errorOccurred.data().get(0));
    assertEquals(errorMessage, message, "Error message");
  }
}
