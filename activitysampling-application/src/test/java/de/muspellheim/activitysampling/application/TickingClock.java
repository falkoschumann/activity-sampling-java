package de.muspellheim.activitysampling.application;

import java.time.*;
import java.util.*;

class TickingClock extends Clock {
  private Instant instant;
  private final ZoneId zone;

  public TickingClock(Instant start) {
    this(start, ZoneId.systemDefault());
  }

  public TickingClock(Instant start, ZoneId zone) {
    this.instant = start;
    this.zone = zone;
  }

  public void tick(Duration interval) {
    instant = instant.plus(interval);
  }

  @Override
  public ZoneId getZone() {
    return zone;
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return new TickingClock(instant, zone);
  }

  @Override
  public Instant instant() {
    return instant;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TickingClock that = (TickingClock) o;
    return instant.equals(that.instant) && zone.equals(that.zone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), instant, zone);
  }

  @Override
  public String toString() {
    return "TickingClock{" + "instant=" + instant + ", zone=" + zone + '}';
  }
}
