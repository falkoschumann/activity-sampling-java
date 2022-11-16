package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.*;

public class ActivitiesServiceImpl implements ActivitiesService {
  private final EventStore eventStore;
  private final Clock clock;

  public ActivitiesServiceImpl(EventStore eventStore) {
    this(eventStore, Clock.systemUTC());
  }

  public ActivitiesServiceImpl(EventStore eventStore, Clock clock) {
    this.eventStore = eventStore;
    this.clock = clock;
  }

  @Override
  public void logActivity(String description) {
    eventStore.record(new ActivityLoggedEvent(clock.instant(), description));
  }

  @Override
  public RecentActivities selectRecentActivities() {
    var startDate = LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault());
    startDate = startDate.minusDays(30).truncatedTo(ChronoUnit.DAYS);
    var startTimestamp = startDate.toInstant(ZoneOffset.UTC);
    var workingDays =
        eventStore
            .replay()
            .filter(e -> e.timestamp().isAfter(startTimestamp))
            .filter(e -> e instanceof ActivityLoggedEvent)
            .map(e -> (ActivityLoggedEvent) e)
            .map(
                e ->
                    new Activity(
                        LocalDateTime.ofInstant(e.timestamp(), ZoneId.systemDefault()),
                        e.description()))
            .collect(Collectors.groupingBy(e -> e.timestamp().toLocalDate()))
            .entrySet()
            .stream()
            .map(
                e ->
                    new WorkingDay(
                        e.getKey(),
                        List.copyOf(
                            e.getValue().stream()
                                .sorted((a1, a2) -> a2.timestamp().compareTo(a1.timestamp()))
                                .toList())))
            .sorted((w1, w2) -> w2.date().compareTo(w1.date()))
            .toList();
    return new RecentActivities(List.copyOf(workingDays));
  }
}
