package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record Timesheet(List<TimesheetByDay> workingDays, Duration total) {}
