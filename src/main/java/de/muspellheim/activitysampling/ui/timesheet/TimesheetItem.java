/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.timesheet;

import java.util.Objects;
import lombok.Builder;

@Builder
public record TimesheetItem(String date, String client, String project, String task, String hours) {
  public TimesheetItem {
    Objects.requireNonNull(date, "The date must not be null.");
    Objects.requireNonNull(client, "The client must not be null.");
    Objects.requireNonNull(project, "The project must not be null.");
    Objects.requireNonNull(task, "The task must not be null.");
    Objects.requireNonNull(hours, "The hours must not be null.");
  }
}
