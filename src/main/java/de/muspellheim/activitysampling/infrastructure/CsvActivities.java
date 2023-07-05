/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.infrastructure;

import de.muspellheim.activitysampling.domain.Activities;
import de.muspellheim.activitysampling.domain.Activity;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class CsvActivities implements Activities {
  private static final String COLUMN_TIMESTAMP = "Timestamp";
  private static final String COLUMN_DURATION = "Duration";
  private static final String COLUMN_CLIENT = "Client";
  private static final String COLUMN_PROJECT = "Project";
  private static final String COLUMN_NOTES = "Notes";

  private final Path file;

  public CsvActivities(Path file) {
    this.file = file;
  }

  @Override
  public List<Activity> findInPeriod(LocalDate from, LocalDate to) {
    try (var parser = newParser()) {
      return parser.stream().map(this::parseActivity).filter(a -> isBetween(a, from, to)).toList();
    } catch (NoSuchFileException e) {
      return List.of();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to find activities in period from file " + file, e);
    }
  }

  private Activity parseActivity(CSVRecord csvRecord) {
    return new Activity(
        LocalDateTime.parse(csvRecord.get(COLUMN_TIMESTAMP)),
        Duration.parse(csvRecord.get(COLUMN_DURATION)),
        csvRecord.get(COLUMN_CLIENT),
        csvRecord.get(COLUMN_PROJECT),
        csvRecord.get(COLUMN_NOTES));
  }

  private boolean isBetween(Activity activity, LocalDate from, LocalDate to) {
    var date = activity.timestamp().toLocalDate();
    return !date.isBefore(from) && !date.isAfter(to);
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
          activity.client(),
          activity.project(),
          activity.notes());
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
            .setHeader(
                COLUMN_TIMESTAMP, COLUMN_DURATION, COLUMN_CLIENT, COLUMN_PROJECT, COLUMN_NOTES);
    if (Files.exists(file)) {
      builder.setSkipHeaderRecord(true);
    }
    return builder.build();
  }
}
