package de.muspellheim.activitysampling.application.shared;

import de.muspellheim.activitysampling.domain.*;
import de.muspellheim.activitysampling.infrastructure.*;
import java.nio.file.*;

public class Registry {

  private Registry() {
    // do not instantiate static class
  }

  public static ActivitiesService getActivitiesService() {
    Configuration configuration = Configuration.INSTANCE;
    var file = Paths.get(configuration.getLogFile());
    var clock = configuration.getClock();
    var eventStore = new CsvEventStore(file);
    return new ActivitiesServiceImpl(eventStore, clock);
  }
}
