package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public interface Activities {
  List<Activity> findAll();

  List<Activity> findInPeriod(LocalDate from, LocalDate to);

  void append(Activity activity);
}
