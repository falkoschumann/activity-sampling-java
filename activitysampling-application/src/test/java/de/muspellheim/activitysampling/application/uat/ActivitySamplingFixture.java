/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.uat;

import de.muspellheim.activitysampling.application.activitysampling.ActivitySamplingViewModel;
import java.time.Duration;
import java.time.Instant;

public class ActivitySamplingFixture {
  private final TickingClock clock = SystemUnderTest.INSTANCE.getClock();
  private final ActivitySamplingViewModel viewModel =
      SystemUnderTest.INSTANCE.getActivitySamplingViewModel();

  void enterTimestamp(Instant timestamp) {
    clock.setTimestamp(timestamp);
  }

  void enterStartCountdown(int minutes) {
    viewModel.startCountdown(Duration.ofMinutes(minutes));
  }

  void enterTick(Duration duration) {
    clock.tick(duration);
    viewModel.progressCountdown(duration);
  }

  String checkCountdown() {
    return viewModel.countdownLabelTextProperty().get();
  }

  double checkCountdownProgress() {
    return viewModel.countdownProgressProperty().get();
  }

  void enterActivityText(String text) {
    viewModel.activityTextProperty().set(text);
  }

  void pressLogActivity() {
    viewModel.logActivity();
  }

  void enterSelectActivity(String text) {
    var activity =
        viewModel.getRecentActivities().stream()
            .filter(a -> a.text().equals(text))
            .findFirst()
            .orElseThrow()
            .description();
    viewModel.setActivity(activity);
  }

  String checkActivityText() {
    return viewModel.activityTextProperty().get();
  }

  String checkHoursToday() {
    return viewModel.hoursTodayLabelTextProperty().get();
  }

  String checkHoursYesterday() {
    return viewModel.hoursYesterdayLabelTextProperty().get();
  }

  String checkHoursThisWeek() {
    return viewModel.hoursThisWeekLabelTextProperty().get();
  }

  String checkHoursThisMonth() {
    return viewModel.hoursThisMonthLabelTextProperty().get();
  }
}
