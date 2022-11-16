package de.muspellheim.activitysampling.infrastructure;

import de.muspellheim.activitysampling.domain.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.temporal.*;
import java.util.stream.*;
import org.apache.commons.csv.*;

public class CsvEventStore implements EventStore {
  public static final String COLUMN_TIMESTAMP = "Timestamp";
  public static final String COLUMN_DESCRIPTION = "Description";

  private final Path file;

  public CsvEventStore() {
    file = Paths.get(System.getProperty("user.home"), "activity-log.csv");
  }

  public CsvEventStore(Path file) {
    this.file = file;
  }

  @Override
  public void record(Iterable<Event> events) {
    try (var printer = newPrinter()) {
      for (var event : events) {
        if (event instanceof ActivityLoggedEvent e) {
          printer.printRecord(e.timestamp().truncatedTo(ChronoUnit.SECONDS), e.description());
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to record event into store.", e);
    }
  }

  private CSVPrinter newPrinter() throws IOException {
    var format = newFormat();
    return new CSVPrinter(
        Files.newBufferedWriter(
            file, StandardOpenOption.APPEND, StandardOpenOption.APPEND, StandardOpenOption.CREATE),
        format);
  }

  @Override
  public Stream<Event> replay() {
    try {
      var parser = newParser();
      return parser.stream()
          .onClose(
              () -> {
                try {
                  ((Closeable) parser).close();
                } catch (IOException e) {
                  throw new RuntimeException("Failed to replay events from store.", e);
                }
              })
          .map(
              record ->
                  new ActivityLoggedEvent(
                      Instant.parse(record.get(COLUMN_TIMESTAMP)), record.get(COLUMN_DESCRIPTION)));
    } catch (NoSuchFileException e) {
      return Stream.of();
    } catch (IOException e) {
      throw new RuntimeException("Failed to replay events from store.", e);
    }
  }

  private CSVParser newParser() throws IOException {
    var format = newFormat();
    return new CSVParser(Files.newBufferedReader(file), format);
  }

  private CSVFormat newFormat() {
    var builder =
        CSVFormat.Builder.create(CSVFormat.RFC4180).setHeader(COLUMN_TIMESTAMP, COLUMN_DESCRIPTION);
    if (Files.exists(file)) {
      builder.setSkipHeaderRecord(true);
    }
    return builder.build();
  }
}
