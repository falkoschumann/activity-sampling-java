/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.WorkingDay;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class WorkingDayTests {
  @Test
  void from_MultipleActivitiesOnSameDay_SortsActivitiesByTimeDescending() {
    var today = LocalDate.now();
    var a1 = newActivity(today, LocalTime.of(11, 0));
    var a2 = newActivity(today, LocalTime.of(12, 0));

    var days = WorkingDay.from(List.of(a1, a2));

    assertEquals(days, List.of(new WorkingDay(today, List.of(a2, a1))));
  }

  @Test
  void from_MultipleActivitiesOnDifferentDays_SortsDatesDescending() {
    var today = LocalDate.now();
    var yesterday = today.minusDays(1);
    var a1 = newActivity(yesterday, LocalTime.of(12, 0));
    var a2 = newActivity(today, LocalTime.of(12, 0));

    var days = WorkingDay.from(List.of(a1, a2));

    assertEquals(
        days, List.of(new WorkingDay(today, List.of(a2)), new WorkingDay(yesterday, List.of(a1))));
  }

  private static Activity newActivity(LocalDate date, LocalTime time) {
    return Activity.builder()
        .timestamp(LocalDateTime.of(date, time))
        .duration(Duration.ofMinutes(20))
        .client("client")
        .project("project")
        .task("task")
        .build();
  }
}
