package de.muspellheim.activitysampling.domain;

import java.time.*;

public record TimesheetEntry(String activity, Duration duration) {}
