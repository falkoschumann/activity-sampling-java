/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.muspellheim.activitysampling.domain.Timesheet;
import de.muspellheim.activitysampling.ui.timesheet.TimesheetItem;
import de.muspellheim.activitysampling.ui.timesheet.TimesheetViewModel;
import de.muspellheim.activitysampling.util.ConfigurableResponses;
import de.muspellheim.activitysampling.util.Exceptions;
import de.muspellheim.activitysampling.util.OutputTracker;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class TimesheetViewModelTests {
  private ActivitiesServiceStub activitiesService;
  private TimesheetViewModel sut;
  private OutputTracker<Throwable> errorOccurred;

  @BeforeEach
  void init() {
    activitiesService = new ActivitiesServiceStub();
    var clock = Clock.fixed(Instant.parse("2023-04-12T12:00:00Z"), ZoneId.systemDefault());
    sut = new TimesheetViewModel(activitiesService, Locale.GERMANY, clock);
    errorOccurred = sut.trackErrorOccurred();
  }

  @Test
  void new_InitializesState() {
    assertTitleAndPeriod("This Week: ", "10.04.2023 - 16.04.2023", ChronoUnit.WEEKS);
    assertTimesheet(List.of(), "00:00");
  }

  @Test
  void load_Successfully_RefreshesTimesheet() {
    var timesheet =
        new Timesheet(
            List.of(
                Timesheet.Entry.builder()
                    .date(LocalDate.of(2023, 4, 14))
                    .task("Lorem ipsum")
                    .hours(Duration.ofMinutes(20))
                    .build()));
    activitiesService.initTimesheetResponses(ConfigurableResponses.always(timesheet));

    sut.load();

    assertTimesheet(List.of(new TimesheetItem("14.04.2023", "Lorem ipsum", "00:20")), "00:20");
    assertNoError();
  }

  @Test
  void load_Failed_NotifiesErrorOccurred() {
    activitiesService.initTimesheetResponses(
        ConfigurableResponses.sequence(new IllegalStateException("Something went wrong.")));

    sut.load();

    assertTimesheet(List.of(), "00:00");
    assertError("Failed to load timesheet. Something went wrong.");
  }

  @Test
  void setPeriod_FromWeekToDay_DisplaysMonday() {
    sut.load();

    sut.setPeriod(ChronoUnit.DAYS);

    assertTitleAndPeriod("This Day: ", "10.04.2023", ChronoUnit.DAYS);
  }

  @Test
  void setPeriod_FromWeekToMonth_DisplaysEntireMonth() {
    sut.load();

    sut.setPeriod(ChronoUnit.MONTHS);

    assertTitleAndPeriod("This Month: ", "01.04.2023 - 30.04.2023", ChronoUnit.MONTHS);
  }

  @Test
  void setPeriod_FromDayToWeek_DisplaysEntireWeek() {
    sut.load();
    sut.setPeriod(ChronoUnit.DAYS);

    sut.setPeriod(ChronoUnit.WEEKS);

    assertTitleAndPeriod("This Week: ", "10.04.2023 - 16.04.2023", ChronoUnit.WEEKS);
  }

  @Test
  void setPeriod_FromDayToMonth_DisplaysEntireMonth() {
    sut.load();
    sut.setPeriod(ChronoUnit.DAYS);

    sut.setPeriod(ChronoUnit.MONTHS);

    assertTitleAndPeriod("This Month: ", "01.04.2023 - 30.04.2023", ChronoUnit.MONTHS);
  }

  @Test
  void setPeriod_FromMonthToDay_DisplaysFirstDayOfMonth() {
    sut.load();
    sut.setPeriod(ChronoUnit.MONTHS);

    sut.setPeriod(ChronoUnit.DAYS);

    assertTitleAndPeriod("This Day: ", "01.04.2023", ChronoUnit.DAYS);
  }

  @Test
  void setPeriod_FromMonthToWeek_DisplaysFirstWeekOfMonth() {
    sut.load();
    sut.setPeriod(ChronoUnit.MONTHS);

    sut.setPeriod(ChronoUnit.WEEKS);

    assertTitleAndPeriod("This Week: ", "27.03.2023 - 02.04.2023", ChronoUnit.WEEKS);
  }

  @Test
  void setPeriod_UnsupportedPeriod_ThrowsException() {
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
    assertEquals(title1, sut.getTitle1(), "Title 1");
    assertEquals(title2, sut.getTitle2(), "Title 2");
    assertEquals(period, sut.getPeriod(), "Period");
  }

  private void assertTimesheet(List<TimesheetItem> items, String total) {
    assertEquals(items, sut.getTimesheetItems(), "Timesheet items");
    assertEquals(total, sut.getTotal(), "Timesheet total");
  }

  private void assertNoError() {
    assertEquals(List.of(), this.errorOccurred.data(), "Errors occurred");
  }

  private void assertError(String errorMessage) {
    var message = Exceptions.summarizeMessages(this.errorOccurred.data().get(0));
    assertEquals(errorMessage, message, "Error message");
  }
}
