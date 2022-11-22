package de.muspellheim.activitysampling.domain;

import java.time.*;

public record TimesheetEntry(LocalDate date, String activity, Duration duration) {}
