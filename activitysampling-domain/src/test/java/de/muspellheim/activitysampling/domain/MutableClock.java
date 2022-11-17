package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

class MutableClock extends Clock {
  private Instant timestamp;
  private final ZoneId zone;

  MutableClock() {
    this(Instant.now());
  }

  MutableClock(Instant timestamp) {
    this(timestamp, ZoneId.systemDefault());
  }

  MutableClock(Instant timestamp, ZoneId zone) {
    this.timestamp = timestamp;
    this.zone = zone;
  }

  void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public ZoneId getZone() {
    return zone;
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return new MutableClock(timestamp, zone);
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
    MutableClock that = (MutableClock) o;
    return timestamp.equals(that.timestamp) && zone.equals(that.zone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), timestamp, zone);
  }

  @Override
  public String toString() {
    return "MutableClock{" + "timestamp=" + timestamp + ", zone=" + zone + '}';
  }
}
