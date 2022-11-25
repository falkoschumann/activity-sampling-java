package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record Timesheet(List<TimesheetEntry> entries, Duration total) {}
