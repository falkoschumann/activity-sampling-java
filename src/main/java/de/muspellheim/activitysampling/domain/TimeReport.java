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
  public record Entry(String client, String project, String task, Duration hours) {

    public Entry {
      Objects.requireNonNull(client, "The client cannot be null.");
      Strings.requireNonBlank(client, "The client cannot be blank.");
      Objects.requireNonNull(project, "The project cannot be null.");
      Strings.requireNonBlank(project, "The project cannot be blank.");
      Objects.requireNonNull(task, "The task cannot be null.");
      Strings.requireNonBlank(task, "The task cannot be blank.");
      Objects.requireNonNull(hours, "The hours cannot be null.");
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
        groups.sort(
            Comparator.comparing(Entry::client)
                .thenComparing(Entry::project)
                .thenComparing(Entry::task));
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

  public TimeReport groupByClient() {
    var groups = new ArrayList<Entry>();
    for (var activity : entries()) {
      var index = Lists.indexOf(groups, g -> g.client().equals(activity.client()));
      if (index == -1) {
        var group =
            Entry.builder()
                .client(activity.client())
                .project("N/A")
                .task("N/A")
                .hours(activity.hours())
                .build();
        groups.add(group);
        groups.sort(Comparator.comparing(Entry::client));
      } else {
        var group = groups.get(index);
        var accumulatedHours = group.hours().plus(activity.hours());
        group =
            Entry.builder()
                .client(activity.client())
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
    for (var activity : entries()) {
      var index = Lists.indexOf(groups, g -> g.project().equals(activity.project()));
      if (index == -1) {
        var group =
            Entry.builder()
                .client(activity.client())
                .project(activity.project())
                .task("N/A")
                .hours(activity.hours())
                .build();
        groups.add(group);
        groups.sort(Comparator.comparing(Entry::project).thenComparing(Entry::client));
      } else {
        var group = groups.get(index);
        var clients = new ArrayList<>(List.of(group.client().split(", ")));
        if (!clients.contains(activity.client())) {
          clients.add(activity.client());
          Collections.sort(clients);
        }
        var accumulatedHours = group.hours().plus(activity.hours());
        group =
            Entry.builder()
                .client(String.join(", ", clients))
                .project(activity.project())
                .task("N/A")
                .hours(accumulatedHours)
                .build();
        groups.set(index, group);
      }
    }
    return new TimeReport(List.copyOf(groups));
  }

  public TimeReport groupByTask() {
    var groups = new ArrayList<Entry>();
    for (var entry : entries()) {
      var index = Lists.indexOf(groups, g -> g.task().equals(entry.task()));
      if (index == -1) {
        var group =
            Entry.builder()
                .client("N/A")
                .project("N/A")
                .task(entry.task())
                .hours(entry.hours())
                .build();
        groups.add(group);
        groups.sort(Comparator.comparing(Entry::task));
      } else {
        var group = groups.get(index);
        var accumulatedHours = group.hours().plus(entry.hours());
        group =
            Entry.builder()
                .client("N/A")
                .project("N/A")
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
}
