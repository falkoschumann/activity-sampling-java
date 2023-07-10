/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.domain;

import de.muspellheim.activitysampling.util.Lists;
import de.muspellheim.activitysampling.util.Strings;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

public record Timesheet(List<Entry> entries) {
  @Builder
  public record Entry(LocalDate date, String client, String project, String task, Duration hours)
      implements Comparable<Entry> {

    public Entry {
      Objects.requireNonNull(date, "The date cannot be null.");
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
      return Comparator.comparing(Entry::date)
          .thenComparing(Entry::client)
          .thenComparing(Entry::project)
          .thenComparing(Entry::task)
          .compare(this, other);
    }
  }

  public Timesheet {
    Objects.requireNonNull(entries, "The entries must not be null.");
  }

  public static Timesheet from(List<Activity> activities) {
    var entries = new ArrayList<Entry>();
    for (var a : activities) {
      var date = a.timestamp().toLocalDate();
      var index =
          Lists.indexOf(
              entries,
              e ->
                  e.date().equals(date)
                      && e.client().equals(a.client())
                      && e.project().equals(a.project())
                      && e.task().equals(a.task()));
      if (index == -1) {
        var entry =
            Entry.builder()
                .date(date)
                .client(a.client())
                .project(a.project())
                .task(a.task())
                .hours(a.duration())
                .build();
        entries.add(entry);
        Collections.sort(entries);
      } else {
        var entry = entries.get(index);
        var accumulatedHours = entry.hours().plus(a.duration());
        entry =
            Entry.builder()
                .date(entry.date())
                .client(entry.client())
                .project(entry.project())
                .task(entry.task())
                .hours(accumulatedHours)
                .build();
        entries.set(index, entry);
      }
    }
    return new Timesheet(entries);
  }

  public Duration total() {
    var hours = entries.stream().map(Entry::hours).toList();
    var total = Duration.ZERO;
    for (var e : hours) {
      total = total.plus(e);
    }
    return total;
  }
}
