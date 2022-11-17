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
  void replay_StoreDoesNotExist_ReturnsEmpty() {
    var result = sut.replay().toList();

    assertEquals(List.of(), result);
  }

  @Test
  void recordAndReplay_ReplaysRecordedEvents() {
    sut.record(
        List.of(
            new ActivityLoggedEvent(Instant.parse("2022-11-16T14:04:00Z"), "A1"),
            new ActivityLoggedEvent(Instant.parse("2022-11-16T14:24:00Z"), "A2")));

    assertEquals(
        List.of(
            new ActivityLoggedEvent(Instant.parse("2022-11-16T14:04:00Z"), "A1"),
            new ActivityLoggedEvent(Instant.parse("2022-11-16T14:24:00Z"), "A2")),
        sut.replay().toList());
  }
}
