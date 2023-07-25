/*
 * Activity Sampling
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import de.muspellheim.activitysampling.util.Lists;
import de.muspellheim.activitysampling.util.Strings;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

public record TimeReport(List<Entry> entries) {

  @Builder
  public record Entry(String client, String project, String task, Duration hours)
      implements Comparable<Entry> {

    public Entry {
      Objects.requireNonNull(client, "The client cannot be null.");
      Strings.requireNonBlank(client, "The client cannot be blank.");
      Objects.requireNonNull(project, "The project cannot be null.");
      Strings.requireNonBlank(project, "The project cannot be blank.");
      Objects.requireNonNull(task, "The task cannot be null.");
      Strings.requireNonBlank(task, "The task cannot be blank.");
      Objects.requireNonNull(hours, "The hours cannot be null.");
    }

    @Override
    public int compareTo(Entry other) {
      return Comparator.comparing(Entry::client)
          .thenComparing(Entry::project)
          .thenComparing(Entry::task)
          .compare(this, other);
    }
  }

  public static TimeReport from(List<Activity> activities) {
    var groups = new ArrayList<Entry>();
    for (var entry : activities) {
      var index =
          Lists.indexOf(
              groups,
              g ->
                  g.client().equals(entry.client())
                      && g.project().equals(entry.project())
                      && g.task().equals(entry.task()));
      if (index == -1) {
        var group =
            Entry.builder()
                .client(entry.client())
                .project(entry.project())
                .task(entry.task())
                .hours(entry.duration())
                .build();
        groups.add(group);
        Collections.sort(groups);
      } else {
        var group = groups.get(index);
        var accumulatedHours = group.hours().plus(entry.duration());
        group =
            Entry.builder()
                .client(group.client())
                .project(group.project())
                .task(group.task())
                .hours(accumulatedHours)
                .build();
        groups.set(index, group);
      }
    }
    return new TimeReport(List.copyOf(groups));
  }

  public Duration total() {
    var hours = entries().stream().map(Entry::hours).toList();
    var total = Duration.ZERO;
    for (var e : hours) {
      total = total.plus(e);
    }
    return total;
  }

  public TimeReport groupByClient() {
    var groups = new ArrayList<Entry>();
    for (var entry : entries()) {
      var index = Lists.indexOf(groups, g -> g.client().equals(entry.client()));
      if (index == -1) {
        var group =
            Entry.builder()
                .client(entry.client())
                .project("N/A")
                .task("N/A")
                .hours(entry.hours())
                .build();
        groups.add(group);
        Collections.sort(groups);
      } else {
        var group = groups.get(index);
        var accumulatedHours = group.hours().plus(entry.hours());
        group =
            Entry.builder()
                .client(entry.client())
                .project("N/A")
                .task("N/A")
                .hours(accumulatedHours)
                .build();
        groups.set(index, group);
      }
    }
    return new TimeReport(List.copyOf(groups));
  }

  public TimeReport groupByProject() {
    var groups = new ArrayList<Entry>();
    for (var entry : entries()) {
      var index =
          Lists.indexOf(
              groups,
              g -> g.client().equals(entry.client()) && g.project().equals(entry.project()));
      if (index == -1) {
        var group =
            Entry.builder()
                .client(entry.client())
                .project(entry.project())
                .task("N/A")
                .hours(entry.hours())
                .build();
        groups.add(group);
        Collections.sort(groups);
      } else {
        var group = groups.get(index);
        var accumulatedHours = group.hours().plus(entry.hours());
        group =
            Entry.builder()
                .client(entry.client())
                .project(entry.project())
                .task("N/A")
                .hours(accumulatedHours)
                .build();
        groups.set(index, group);
      }
    }
    return new TimeReport(List.copyOf(groups));
  }
}
