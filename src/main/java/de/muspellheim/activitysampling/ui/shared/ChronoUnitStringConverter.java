/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.shared;

import java.time.temporal.ChronoUnit;
import javafx.util.StringConverter;

public class ChronoUnitStringConverter extends StringConverter<ChronoUnit> {
  @Override
  public String toString(ChronoUnit object) {
    return switch (object) {
      case DAYS -> "Day";
      case WEEKS -> "Week";
      case MONTHS -> "Month";
      default -> throw new IllegalArgumentException("Unsupported period: %s.".formatted(object));
    };
  }

  @Override
  public ChronoUnit fromString(String string) {
    throw new UnsupportedOperationException();
  }
}
