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

public record Timesheet(List<Entry> entries, Duration total) {
  public record Entry(LocalDate date, String client, String project, String notes, Duration hours)
      implements Comparable<Entry> {
    public Entry {
      Objects.requireNonNull(date, "The date is null.");
      Objects.requireNonNull(client, "The client is null.");
      Strings.requireNonBlank(client, "The client is blank.");
      Objects.requireNonNull(project, "The project is null.");
      Strings.requireNonBlank(project, "The project is blank.");
      Objects.requireNonNull(notes, "The notes is null.");
      Objects.requireNonNull(hours, "The hours is null.");
    }

    // @Deprecated
    public Entry(LocalDate date, String notes, Duration hours) {
      // TODO remove deprecated constructor
      this(date, "n/a", "n/a", notes, hours);
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

  public static final Timesheet EMPTY = new Timesheet(List.of(), Duration.ZERO);

  public Timesheet {
    Objects.requireNonNull(entries, "The entries must not be null.");
    Objects.requireNonNull(total, "The total must not be null.");
  }

  public static Timesheet of(List<Activity> activities) {
    var entries = new ArrayList<Entry>();
    var total = Duration.ZERO;
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
        var entry = new Entry(date, a.client(), a.project(), a.notes(), a.duration());
        entries.add(entry);
        Collections.sort(entries);
      } else {
        var entry = entries.get(index);
        var accumulatedHours = entry.hours().plus(a.duration());
        entry =
            new Entry(
                entry.date(), entry.client(), entry.project(), entry.notes(), accumulatedHours);
        entries.set(index, entry);
      }
      total = total.plus(a.duration());
    }
    return new Timesheet(entries, total);
  }
}
