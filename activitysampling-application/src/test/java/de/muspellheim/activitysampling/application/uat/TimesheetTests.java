/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.uat;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class TimesheetTests {
  @BeforeEach
  void init() {
    SystemUnderTest.INSTANCE.reset();
  }

  @Test
  @Disabled
  void mainScenario() {
    // TODO implement acceptance test for timesheet
    /*
    var timesheetFixture = new TimesheetFixture();
    var timesheetEntriesFixture = new TimesheetEntriesFixture();

    activitySamplingFixture.now(Instant.parse("2022-11-16T17:05:00Z"));
    */
    fail();
  }
}
