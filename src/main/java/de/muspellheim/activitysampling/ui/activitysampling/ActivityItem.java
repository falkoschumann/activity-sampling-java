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

record ActivityItem(String text, String description) {
  ActivityItem {
    Objects.requireNonNull(text, "The text must not be null.");
  }

  static ActivityItem header(WorkingDay workingDay, Locale locale) {
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale);
    return new ActivityItem(workingDay.date().format(dateFormatter), null);
  }

  static ActivityItem item(Activity activity, Locale locale) {
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale);
    return new ActivityItem(
        activity.timestamp().format(timeFormatter)
            + " - "
            + activity.project()
            + " ("
            + activity.client()
            + ") "
            + activity.notes(),
        activity.notes());
  }

  boolean isActivity() {
    return description != null;
  }
}
