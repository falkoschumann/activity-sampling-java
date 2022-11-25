package de.muspellheim.activitysampling.application.uat;

import de.muspellheim.activitysampling.application.activitysampling.*;
import java.time.*;

public class ActivitySamplingFixture {
  private final TickingClock clock = SystemUnderTest.INSTANCE.getClock();
  private final ActivitySamplingViewModel viewModel =
      SystemUnderTest.INSTANCE.getActivitySamplingViewModel();

  void now(Instant timestamp) {
    clock.setTimestamp(timestamp);
  }

  void startCountdown(int minutes) {
    viewModel.startCountdown(Duration.ofMinutes(minutes));
  }

  void tick(Duration duration) {
    clock.tick(duration);
    viewModel.progressCountdown(duration);
  }

  String countdown() {
    return viewModel.countdownLabelTextProperty().get();
  }

  double countdownProgress() {
    return viewModel.countdownProgressProperty().get();
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
