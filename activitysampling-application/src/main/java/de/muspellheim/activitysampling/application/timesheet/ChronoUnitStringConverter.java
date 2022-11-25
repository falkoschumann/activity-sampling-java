package de.muspellheim.activitysampling.application.timesheet;

import java.time.temporal.*;
import javafx.util.*;

public class ChronoUnitStringConverter extends StringConverter<ChronoUnit> {
  @Override
  public String toString(ChronoUnit object) {
    return switch (object) {
      case DAYS -> "Day";
      case WEEKS -> "Week";
      case MONTHS -> "Month";
      default -> throw new IllegalArgumentException("Not handled: " + object);
    };
  }

  @Override
  public ChronoUnit fromString(String string) {
    throw new UnsupportedOperationException();
  }
}
