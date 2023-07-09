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
  public record Entry(LocalDate date, String client, String project, String notes, Duration hours)
      implements Comparable<Entry> {
    public static class EntryBuilder {
      // TODO remove n/a as default
      private String client = "n/a";
      private String project = "n/a";
    }

    public Entry {
      Objects.requireNonNull(date, "The date is null.");
      Objects.requireNonNull(client, "The client is null.");
      Strings.requireNonBlank(client, "The client is blank.");
      Objects.requireNonNull(project, "The project is null.");
      Strings.requireNonBlank(project, "The project is blank.");
      Objects.requireNonNull(notes, "The notes is null.");
      Objects.requireNonNull(hours, "The hours is null.");
    }

    @Override
    public int compareTo(Entry other) {
      return Comparator.comparing(Entry::date)
          .thenComparing(Entry::client)
          .thenComparing(Entry::project)
          .thenComparing(Entry::notes)
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
                      && e.notes().equals(a.notes()));
      if (index == -1) {
        var entry =
            Entry.builder()
                .date(date)
                .client(a.client())
                .project(a.project())
                .notes(a.notes())
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
                .notes(entry.notes())
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
