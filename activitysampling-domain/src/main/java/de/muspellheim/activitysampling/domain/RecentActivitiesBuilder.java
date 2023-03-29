/*
 * Activity Sampling - Domain
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class RecentActivitiesBuilder {
  private final LocalDate today;
  private final List<WorkingDay> workingDays = new ArrayList<>();
  private Duration hoursToday = Duration.ZERO;
  private Duration hoursYesterday = Duration.ZERO;
  private Duration hoursThisWeek = Duration.ZERO;
  private Duration hoursThisMonth = Duration.ZERO;

  RecentActivitiesBuilder(LocalDate today) {
    this.today = today;
  }

  void add(Activity activity) {
    addToWorkingDays(activity);
    addToTimeSummary(activity);
  }

  private void addToWorkingDays(Activity activity) {
    var index = -1;
    for (var i = 0; i < workingDays.size(); i++) {
      var e = workingDays.get(i);
      if (e.date().equals(activity.timestamp().toLocalDate())) {
        index = i;
        break;
      }
    }

    if (index != -1) {
      var day = workingDays.get(index);

      var activities = new ArrayList<>(day.activities());
      activities.add(activity);
      Collections.sort(activities);

      day = new WorkingDay(day.date(), activities);
      workingDays.set(index, day);
    } else {
      var day = new WorkingDay(activity.timestamp().toLocalDate(), List.of(activity));
      workingDays.add(day);
    }

    Collections.sort(workingDays);
  }

  private void addToTimeSummary(Activity activity) {
    var date = activity.timestamp().toLocalDate();
    if (date.isAfter(today)) {
      return;
    }

    if (date.equals(today)) {
      hoursToday = hoursToday.plus(activity.duration());
    }

    var yesterday = today.minusDays(1);
    if (date.equals(yesterday)) {
      hoursYesterday = hoursYesterday.plus(activity.duration());
    }

    var sameYear = (today.getDayOfYear() - date.getDayOfYear()) >= 0;
    var sameWeek =
        (today.getDayOfYear() - date.getDayOfYear()) < 7
            && date.getDayOfWeek().getValue() <= today.getDayOfWeek().getValue();
    if (sameYear && sameWeek) {
      hoursThisWeek = hoursThisWeek.plus(activity.duration());
    }

    var startOfMonth = today.withDayOfMonth(1);
    if (!date.isBefore(startOfMonth)) {
      hoursThisMonth = hoursThisMonth.plus(activity.duration());
    }
  }

  RecentActivities build() {
    return new RecentActivities(
        workingDays, new TimeSummary(hoursToday, hoursYesterday, hoursThisWeek, hoursThisMonth));
  }
}
