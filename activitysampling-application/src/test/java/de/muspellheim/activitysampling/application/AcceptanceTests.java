package de.muspellheim.activitysampling.application;

import static org.junit.jupiter.api.Assertions.*;

import de.muspellheim.activitysampling.domain.*;
import de.muspellheim.activitysampling.infrastructure.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class AcceptanceTests {
  private static final Path STORE_FILE = Paths.get("build/event-store.csv");

  @BeforeEach
  void init() throws IOException {
    Files.deleteIfExists(STORE_FILE);
  }

  @Test
  void mainScenario() {
    var eventStore = new CsvEventStore(STORE_FILE);
    Clock clock = Clock.fixed(Instant.parse("2022-11-16T17:05:00Z"), ZoneId.systemDefault());
    var activitiesService = new ActivitiesServiceImpl(eventStore, clock);
    var fixture = new ActivitySamplingViewModel(activitiesService);
    fixture.run();

    fixture.activityTextProperty().set("Lorem ipsum");
    fixture.logActivity();

    assertEquals(
        List.of(
            new ActivityItem("Mittwoch, 16. November 2022", true),
            new ActivityItem("18:05 - Lorem ipsum", false)),
        fixture.getRecentActivities());
  }
}
