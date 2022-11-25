package de.muspellheim.activitysampling.domain;

import java.time.*;

public record TimesheetEntry(LocalDate date, String notes, Duration hours) {}
