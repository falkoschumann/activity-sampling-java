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

  @BeforeAll
  static void initAll() {
    Locale.setDefault(Locale.GERMANY);
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
  }

  @BeforeEach
  void init() throws IOException {
    Files.deleteIfExists(STORE_FILE);
  }

  @Test
  void mainScenario() {
    var eventStore = new CsvEventStore(STORE_FILE);
    var clock = new TickingClock(Instant.parse("2022-11-16T17:05:00Z"));
    var activitiesService = new ActivitiesServiceImpl(eventStore, clock);
    var fixture = new ActivitySamplingViewModel(activitiesService);
    fixture.run();

    //
    // Step 1 - Log first activity
    //

    fixture.activityTextProperty().set("Lorem ipsum");
    fixture.logActivity();

    assertEquals(
        List.of(
            new ActivityItem("Mittwoch, 16. November 2022"),
            new ActivityItem(
                "18:05 - Lorem ipsum",
                new Activity(LocalDateTime.of(2022, 11, 16, 18, 5), "Lorem ipsum"))),
        fixture.getRecentActivities(),
        "Step 1 - Recent activities");

    //
    // Step 2 - Log second activity
    //

    clock.tick(Duration.ofMinutes(20));
    fixture.activityTextProperty().set("Foobar");
    fixture.logActivity();

    assertEquals(
        List.of(
            new ActivityItem("Mittwoch, 16. November 2022"),
            new ActivityItem(
                "18:25 - Foobar", new Activity(LocalDateTime.of(2022, 11, 16, 18, 25), "Foobar")),
            new ActivityItem(
                "18:05 - Lorem ipsum",
                new Activity(LocalDateTime.of(2022, 11, 16, 18, 5), "Lorem ipsum"))),
        fixture.getRecentActivities(),
        "Step 2 - Recent activities");

    //
    // Step 2 - Select first activity
    //

    clock.tick(Duration.ofMinutes(20));
    fixture.setActivity(new Activity(LocalDateTime.of(2022, 11, 16, 18, 5), "Lorem ipsum"));

    assertEquals("Lorem ipsum", fixture.activityTextProperty().get(), "Step 3 - Activity text");
  }
}
