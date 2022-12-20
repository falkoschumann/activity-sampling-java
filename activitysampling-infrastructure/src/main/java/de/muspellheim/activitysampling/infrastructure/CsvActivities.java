/*
 * Activity Sampling - Infrastructure
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.infrastructure;

import de.muspellheim.activitysampling.domain.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import org.apache.commons.csv.*;

public class CsvActivities implements Activities {
  private static final String COLUMN_TIMESTAMP = "Timestamp";
  private static final String COLUMN_DURATION = "Duration";
  private static final String COLUMN_DESCRIPTION = "Description";

  private final Path file;

  public CsvActivities(Path file) {
    this.file = file;
  }

  @Override
  public List<Activity> findInPeriod(LocalDate from, LocalDate to) {
    try (var parser = newParser()) {
      return parser.stream()
          .map(
              record ->
                  new Activity(
                      LocalDateTime.parse(record.get(COLUMN_TIMESTAMP)),
                      Duration.parse(record.get(COLUMN_DURATION)),
                      record.get(COLUMN_DESCRIPTION)))
          .filter(
              a -> {
                var date = a.timestamp().toLocalDate();
                return !date.isBefore(from) && !date.isAfter(to);
              })
          .toList();
    } catch (NoSuchFileException e) {
      return List.of();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to find all activities from file " + file, e);
    }
  }

  private CSVParser newParser() throws IOException {
    var format = newFormat();
    return new CSVParser(Files.newBufferedReader(file), format);
  }

  @Override
  public void append(Activity activity) {
    try (var printer = newPrinter()) {
      printer.printRecord(
          activity.timestamp().truncatedTo(ChronoUnit.SECONDS),
          activity.duration(),
          activity.description());
    } catch (Exception e) {
      throw new IllegalStateException("Failed to append activity to file " + file, e);
    }
  }

  private CSVPrinter newPrinter() throws IOException {
    var format = newFormat();
    return new CSVPrinter(
        Files.newBufferedWriter(
            file, StandardOpenOption.APPEND, StandardOpenOption.APPEND, StandardOpenOption.CREATE),
        format);
  }

  private CSVFormat newFormat() {
    var builder =
        CSVFormat.Builder.create(CSVFormat.RFC4180)
            .setHeader(COLUMN_TIMESTAMP, COLUMN_DURATION, COLUMN_DESCRIPTION);
    if (Files.exists(file)) {
      builder.setSkipHeaderRecord(true);
    }
    return builder.build();
  }
}
