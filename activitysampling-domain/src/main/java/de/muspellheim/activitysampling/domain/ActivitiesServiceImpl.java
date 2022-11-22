package de.muspellheim.activitysampling.domain;

import java.time.*;

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
    eventStore.record(
        new ActivityLoggedEvent(clock.instant(), Duration.ofMinutes(20), description.trim()));
  }

  @Override
  public RecentActivities selectRecentActivities() {
    var today = LocalDate.ofInstant(clock.instant(), ZoneId.systemDefault());
    var workingDaysProjection = new WorkingDaysProjection(today);
    var timeSummaryProjection = new TimeSummaryProjection(today);
    eventStore
        .replay()
        .forEach(
            e -> {
              workingDaysProjection.apply(e);
              timeSummaryProjection.apply(e);
            });
    return new RecentActivities(workingDaysProjection.get(), timeSummaryProjection.get());
  }
}
