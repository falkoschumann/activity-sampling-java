/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.activitysampling;

import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.WorkingDay;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;

public record ActivityItem(String text, String client, String project, String task, String notes) {
  public ActivityItem {
    Objects.requireNonNull(text, "The text cannot be null.");
  }

  static ActivityItem newHeader(WorkingDay workingDay, Locale locale) {
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale);
    return new ActivityItem(workingDay.date().format(dateFormatter), null, null, null, null);
  }

  static ActivityItem newItem(Activity activity, Locale locale) {
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale);
    return new ActivityItem(
        activity.timestamp().format(timeFormatter)
            + " - "
            + activity.project()
            + " ("
            + activity.client()
            + ") "
            + activity.task(),
        activity.client(),
        activity.project(),
        activity.task(),
        activity.notes());
  }

  public boolean isHeader() {
    return client == null;
  }
}
