package de.muspellheim.activitysampling.application.timesheet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.muspellheim.activitysampling.domain.*;
import java.time.*;
import java.time.temporal.*;
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

  private TimesheetViewModel sut;

  @BeforeEach
  void init() {
    var timesheet = new Timesheet();
    timesheet.apply(
        new Activity(LocalDateTime.parse("2022-11-20T12:00"), Duration.ofMinutes(10), "A1"));
    timesheet.apply(
        new Activity(LocalDateTime.parse("2022-11-20T12:00"), Duration.ofMinutes(5), "A2"));
    timesheet.apply(
        new Activity(LocalDateTime.parse("2022-11-21T12:00"), Duration.ofMinutes(5), "A1"));
    when(activitiesService.createTimesheet(any(), any())).thenReturn(timesheet);
    var clock = Clock.fixed(Instant.parse("2022-11-23T20:42:00Z"), ZoneId.systemDefault());
    sut = new TimesheetViewModel(activitiesService, clock);
    sut.setOnError(onError);
    sut.run();
  }

  @Test
  void create_DisplaysThisWeek() {
    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("21.11.2022 - 27.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"),
        () ->
            assertEquals(
                List.of(
                    new TimesheetItem("20.11.2022", "A1", "00:10"),
                    new TimesheetItem("20.11.2022", "A2", "00:05"),
                    new TimesheetItem("21.11.2022", "A1", "00:05")),
                sut.getTimesheetItems()),
        () -> assertEquals("00:20", sut.totalProperty().get()));
  }

  @Test
  void changePeriod_SetDay_DisplaysToday() {
    sut.periodProperty().set(ChronoUnit.DAYS);

    assertAll(
        () -> assertEquals("This Day: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("21.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.DAYS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void back_PeriodIsDay_DisplaysYesterday() {
    sut.periodProperty().set(ChronoUnit.DAYS);

    sut.back();

    assertAll(
        () -> assertEquals("This Day: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("20.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.DAYS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void forward_PeriodIsDay_DisplaysTomorrow() {
    sut.periodProperty().set(ChronoUnit.DAYS);

    sut.forward();

    assertAll(
        () -> assertEquals("This Day: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("22.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.DAYS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void changePeriod_SetWeek_DisplaysThisWeek() {
    sut.periodProperty().set(ChronoUnit.DAYS);

    sut.periodProperty().set(ChronoUnit.WEEKS);

    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("21.11.2022 - 27.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void back_PeriodIsWeek_DisplaysLastWeek() {
    sut.back();

    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("14.11.2022 - 20.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void forward_PeriodIsWeek_DisplaysNextWeek() {
    sut.forward();

    assertAll(
        () -> assertEquals("This Week: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("28.11.2022 - 04.12.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.WEEKS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void changePeriod_SetMonth_DisplaysThisMonth() {
    sut.periodProperty().set(ChronoUnit.MONTHS);

    assertAll(
        () -> assertEquals("This Month: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("01.11.2022 - 30.11.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.MONTHS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void back_PeriodIsMonth_DisplaysLastMonth() {
    sut.periodProperty().set(ChronoUnit.MONTHS);

    sut.back();

    assertAll(
        () -> assertEquals("This Month: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("01.10.2022 - 31.10.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.MONTHS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void forward_PeriodIsMonth_DisplaysNextMonth() {
    sut.periodProperty().set(ChronoUnit.MONTHS);

    sut.forward();

    assertAll(
        () -> assertEquals("This Month: ", sut.title1Property().get(), "Title 1"),
        () -> assertEquals("01.12.2022 - 31.12.2022", sut.title2Property().get(), "Title 2"),
        () -> assertEquals(ChronoUnit.MONTHS, sut.periodProperty().get(), "Period"));
  }

  @Test
  void load_Failed_NotifyError() {
    reset(activitiesService);
    doThrow(new IllegalStateException("Something went wrong."))
        .when(activitiesService)
        .createTimesheet(any(), any());

    sut.back();

    verify(onError).accept("Failed to load timesheet. Something went wrong.");
  }
}
