/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.uat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

class TickingClock extends Clock {
  private Instant timestamp;
  private final ZoneId zone;

  TickingClock() {
    this(Instant.now());
  }

  TickingClock(Instant timestamp) {
    this(timestamp, ZoneId.systemDefault());
  }

  TickingClock(Instant timestamp, ZoneId zone) {
    this.timestamp = timestamp;
    this.zone = zone;
  }

  void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  void tick(Duration interval) {
    timestamp = timestamp.plus(interval);
  }

  @Override
  public ZoneId getZone() {
    return zone;
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return new TickingClock(timestamp, zone);
  }

  @Override
  public Instant instant() {
    return timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof TickingClock c) {
      return timestamp.equals(c.timestamp) && zone.equals(c.zone);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, zone);
  }

  @Override
  public String toString() {
    return "TickingClock{" + "instant=" + timestamp + ", zone=" + zone + '}';
  }
}
