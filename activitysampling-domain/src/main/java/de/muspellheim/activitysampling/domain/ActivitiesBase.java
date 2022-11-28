package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public abstract class ActivitiesBase implements Activities {
  @Override
  public List<Activity> findInPeriod(LocalDate from, LocalDate to) {
    return findAll().stream()
        .filter(
            a -> {
              LocalDate date = a.timestamp().toLocalDate();
              return !date.isBefore(from) && !date.isAfter(to);
            })
        .toList();
  }
}
