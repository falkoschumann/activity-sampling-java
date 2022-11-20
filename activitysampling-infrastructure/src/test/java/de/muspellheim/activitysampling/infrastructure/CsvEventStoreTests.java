package de.muspellheim.activitysampling.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import de.muspellheim.activitysampling.domain.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class CsvEventStoreTests {
  private static final Path STORE_FILE = Paths.get("build/event-store.csv");
  private EventStore sut;

  @BeforeEach
  void init() throws IOException {
    Files.deleteIfExists(STORE_FILE);
    sut = new CsvEventStore(STORE_FILE);
  }

  @Test
  void replay_FileDoesNotExist_ReturnsEmptyList() {
    var result = sut.replay().toList();

    assertEquals(List.of(), result);
  }

  @Test
  void replay_FileExists_ReplaysBlockRecordedEvents() {
    sut.record(
        List.of(
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T14:04:00Z"), Duration.ofMinutes(5), "A1"),
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T14:24:00Z"), Duration.ofMinutes(5), "A2"),
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T14:44:00Z"), Duration.ofMinutes(5), "A3")));

    List<Event> events = sut.replay().toList();

    assertEquals(
        List.of(
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T14:04:00Z"), Duration.ofMinutes(5), "A1"),
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T14:24:00Z"), Duration.ofMinutes(5), "A2"),
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T14:44:00Z"), Duration.ofMinutes(5), "A3")),
        events);
  }

  @Test
  void replay_FileExists_ReplaysSingleRecordedEvents() {
    sut.record(
        new ActivityLoggedEvent(
            Instant.parse("2022-11-16T14:04:00Z"), Duration.ofMinutes(5), "A1"));
    sut.record(
        new ActivityLoggedEvent(
            Instant.parse("2022-11-16T14:24:00Z"), Duration.ofMinutes(5), "A2"));
    sut.record(
        new ActivityLoggedEvent(
            Instant.parse("2022-11-16T14:44:00Z"), Duration.ofMinutes(5), "A3"));

    List<Event> events = sut.replay().toList();

    assertEquals(
        List.of(
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T14:04:00Z"), Duration.ofMinutes(5), "A1"),
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T14:24:00Z"), Duration.ofMinutes(5), "A2"),
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T14:44:00Z"), Duration.ofMinutes(5), "A3")),
        events);
  }
}
