package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record TimesheetByDay(LocalDate date, List<TimesheetEntry> activities, Duration total) {}
