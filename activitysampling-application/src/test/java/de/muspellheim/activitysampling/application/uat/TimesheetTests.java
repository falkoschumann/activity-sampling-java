/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.uat;

import org.junit.jupiter.api.*;

@Disabled
class TimesheetTests {
  @BeforeEach
  void init() {
    SystemUnderTest.INSTANCE.reset();
  }

  @Test
  void mainScenario() {
    // TODO implement acceptance test for timesheet
    /*
    var timesheetFixture = new TimesheetFixture();
    var timesheetEntriesFixture = new TimesheetEntriesFixture();

    activitySamplingFixture.now(Instant.parse("2022-11-16T17:05:00Z"));
    */
  }
}
