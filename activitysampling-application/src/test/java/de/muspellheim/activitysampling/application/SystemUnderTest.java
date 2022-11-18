package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import de.muspellheim.activitysampling.infrastructure.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

class SystemUnderTest {
  private final TickingClock clock;
  private final ActivitySamplingViewModel viewModel;

  SystemUnderTest(Path activityLogFile) {
    var eventStore = new CsvEventStore(activityLogFile);
    clock = new TickingClock();
    var activitiesService = new ActivitiesServiceImpl(eventStore, clock);
    viewModel = new ActivitySamplingViewModel(activitiesService);
    viewModel.run();
  }

  /*
   * Row fixtures
   */

  List<String> recentActivities() {
    return viewModel.getRecentActivities().stream().map(ActivityItem::text).toList();
  }

  /*
   * Action fixtures
   */

  void now(Instant timestamp) {
    clock.setTimestamp(timestamp);
  }

  void tick(Duration duration) {
    clock.tick(duration);
  }

  void activityText(String text) {
    viewModel.activityTextProperty().set(text);
  }

  void logActivity() {
    viewModel.logActivity();
  }

  void selectActivity(String text) {
    var activity =
        viewModel.getRecentActivities().stream()
            .filter(a -> a.text().equals(text))
            .findFirst()
            .orElseThrow()
            .activity();
    viewModel.setActivity(activity);
  }

  String activityText() {
    return viewModel.activityTextProperty().get();
  }

  String hoursToday() {
    return viewModel.hoursTodayLabelTextProperty().get();
  }

  String hoursYesterday() {
    return viewModel.hoursYesterdayLabelTextProperty().get();
  }

  String hoursThisWeek() {
    return viewModel.hoursThisWeekLabelTextProperty().get();
  }

  String hoursThisMonth() {
    return viewModel.hoursThisMonthLabelTextProperty().get();
  }
}
