/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.uat;

import de.muspellheim.activitysampling.application.activitysampling.*;
import java.util.*;

class RecentActivitiesFixture {
  private final ActivitySamplingViewModel viewModel =
      SystemUnderTest.INSTANCE.getActivitySamplingViewModel();

  List<String> query() {
    return viewModel.getRecentActivities().stream().map(ActivityItem::text).toList();
  }
}
