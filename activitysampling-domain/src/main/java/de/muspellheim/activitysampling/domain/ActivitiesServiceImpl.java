/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class ActivitiesServiceImpl implements ActivitiesService {
  private final ActivitiesRepository activitiesRepository;
  @Getter @Setter private Clock clock = Clock.systemDefaultZone();

  public ActivitiesServiceImpl(ActivitiesRepository activitiesRepository) {
    this.activitiesRepository = activitiesRepository;
  }

  @Override
  public void logActivity(String description, Duration duration) {
    var activity = new Activity(LocalDateTime.now(clock), duration, description.trim());
    activitiesRepository.append(activity);
  }

  @Override
  public RecentActivities getRecentActivities() {
    var today = LocalDate.now(clock);
    var from = today.minusDays(30);
    var builder = new RecentActivitiesBuilder(today);
    activitiesRepository.findInPeriod(from, today).forEach(builder::add);
    return builder.build();
  }

  @Override
  public Timesheet getTimesheet(LocalDate from, LocalDate to) {
    var builder = new TimesheetBuilder();
    activitiesRepository.findInPeriod(from, to).forEach(builder::add);
    return builder.build();
  }
}
