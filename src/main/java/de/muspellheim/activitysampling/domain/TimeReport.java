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
  public record Entry(String client, Duration hours) implements Comparable<Entry> {

    public Entry {
      Objects.requireNonNull(client, "The client cannot be null.");
      Strings.requireNonBlank(client, "The client cannot be blank.");
      Objects.requireNonNull(hours, "The hours cannot be null.");
    }

    @Override
    public int compareTo(Entry other) {
      return Comparator.comparing(Entry::client).compare(this, other);
    }
  }

  public static TimeReport from(List<Activity> activities) {
    var groups = new ArrayList<Entry>();
    for (var entry : activities) {
      var index = Lists.indexOf(groups, g -> g.client().equals(entry.client()));
      if (index == -1) {
        var group = Entry.builder().client(entry.client()).hours(entry.duration()).build();
        groups.add(group);
        Collections.sort(groups);
      } else {
        var group = groups.get(index);
        var accumulatedHours = group.hours().plus(entry.duration());
        group = Entry.builder().client(group.client()).hours(accumulatedHours).build();
        groups.set(index, group);
      }
    }
    return new TimeReport(List.copyOf(groups));
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
