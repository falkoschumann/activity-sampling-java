package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

class WorkingDaysProjection {
  private final Instant startTimestamp;

  private final Map<LocalDate, List<Activity>> workingDays = new LinkedHashMap<>();

  WorkingDaysProjection(LocalDate today) {
    var startDate = LocalDateTime.of(today, LocalTime.MIDNIGHT);
    startDate = startDate.minusDays(30).truncatedTo(ChronoUnit.DAYS);
    startTimestamp = startDate.toInstant(ZoneOffset.UTC);
  }

  void apply(Event event) {
    if (event instanceof ActivityLoggedEvent e) {
      apply(e);
    }
  }

  private void apply(ActivityLoggedEvent event) {
    if (event.timestamp().isBefore(startTimestamp)) {
      return;
    }

    LocalDateTime timestamp = LocalDateTime.ofInstant(event.timestamp(), ZoneId.systemDefault());
    var activity = new Activity(timestamp.toLocalTime(), event.description());
    LocalDate date = timestamp.toLocalDate();
    var activities = workingDays.getOrDefault(date, new ArrayList<>());
    activities.add(activity);
    workingDays.put(date, activities);
  }

  List<WorkingDay> get() {
    return workingDays.entrySet().stream()
        .map(
            d ->
                new WorkingDay(
                    d.getKey(),
                    List.copyOf(
                        d.getValue().stream()
                            .sorted((a1, a2) -> a2.time().compareTo(a1.time()))
                            .toList())))
        .sorted((w1, w2) -> w2.date().compareTo(w1.date()))
        .toList();
  }
}
