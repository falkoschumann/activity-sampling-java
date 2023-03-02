/*
 * Activity Sampling - Domain
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class ActivitiesServiceImpl implements ActivitiesService {
  private final ActivitiesRepository activitiesRepository;

  public ActivitiesServiceImpl(ActivitiesRepository activitiesRepository) {
    this.activitiesRepository = activitiesRepository;
  }

  @Override
  public void logActivity(LocalDateTime timestamp, Duration duration, String description) {
    var activity = new Activity(timestamp, duration, description.trim());
    activitiesRepository.append(activity);
  }

  @Override
  public RecentActivities getRecentActivities(LocalDate date, Period period) {
    var from = date.minus(period);
    var builder = new RecentActivitiesBuilder(date);
    activitiesRepository.findInPeriod(from, date).forEach(builder::add);
    return builder.build();
  }

  @Override
  public Timesheet getTimesheet(LocalDate from, LocalDate to) {
    var builder = new TimesheetBuilder();
    activitiesRepository.findInPeriod(from, to).forEach(builder::add);
    return builder.build();
  }
}
