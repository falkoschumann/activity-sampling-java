package de.muspellheim.activitysampling.application;

import java.time.*;
import java.util.*;

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
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TickingClock that = (TickingClock) o;
    return timestamp.equals(that.timestamp) && zone.equals(that.zone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), timestamp, zone);
  }

  @Override
  public String toString() {
    return "TickingClock{" + "instant=" + timestamp + ", zone=" + zone + '}';
  }
}
