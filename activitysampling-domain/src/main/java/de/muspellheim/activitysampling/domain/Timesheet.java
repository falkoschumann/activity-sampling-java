package de.muspellheim.activitysampling.domain;

import java.time.*;
import java.util.*;

public record Timesheet(List<WorkingDay> workingDays, Duration total) {}
