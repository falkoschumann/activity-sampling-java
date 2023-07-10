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
  private enum Field {
    Timestamp,
    Duration,
    Client,
    Project,
    Task,
    Notes,
  }

  private final Path file;

  public CsvActivities(Path file) {
    this.file = file;
  }

  @Override
  public void append(Activity activity) throws Exception {
    try (var printer = newPrinter()) {
      printer.printRecord(
          activity.timestamp().truncatedTo(ChronoUnit.SECONDS),
          activity.duration(),
          activity.client(),
          activity.project(),
          activity.task(),
          activity.notes());
    } catch (Exception e) {
      throw new IOException("Failed to append activity to file " + file, e);
    }
  }

  @Override
  public List<Activity> findInPeriod(LocalDate from, LocalDate to) throws Exception {
    try (var parser = newParser()) {
      return parser.stream().map(this::parseActivity).filter(a -> isBetween(a, from, to)).toList();
    } catch (NoSuchFileException e) {
      return List.of();
    } catch (Exception e) {
      throw new IOException("Failed to find activities in period from file " + file, e);
    }
  }

  private CSVPrinter newPrinter() throws IOException {
    var format = newFormat();
    return new CSVPrinter(
        Files.newBufferedWriter(
            file, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND),
        format);
  }

  private CSVParser newParser() throws IOException {
    var format = newFormat();
    return new CSVParser(Files.newBufferedReader(file), format);
  }

  private Activity parseActivity(CSVRecord csvRecord) {
    return Activity.builder()
        .timestamp(LocalDateTime.parse(csvRecord.get(Field.Timestamp)))
        .duration(Duration.parse(csvRecord.get(Field.Duration)))
        .client(csvRecord.get(Field.Client))
        .project(csvRecord.get(Field.Project))
        .task(csvRecord.get(Field.Task))
        .notes(csvRecord.get(Field.Notes))
        .build();
  }

  private boolean isBetween(Activity activity, LocalDate from, LocalDate to) {
    var date = activity.timestamp().toLocalDate();
    return !date.isBefore(from) && !date.isAfter(to);
  }

  private CSVFormat newFormat() {
    var builder = CSVFormat.Builder.create(CSVFormat.RFC4180).setHeader(Field.class);
    if (Files.exists(file)) {
      builder.setSkipHeaderRecord(true);
    }
    return builder.build();
  }
}
