package de.muspellheim.activitysampling.application;

import de.muspellheim.activitysampling.domain.*;
import de.muspellheim.activitysampling.infrastructure.*;
import java.nio.file.*;
import java.util.function.*;

class ViewModels {
  private ViewModels() {
    // do not instantiate static class
  }

  static ActivitySamplingViewModel newActivitySampling(
      Runnable onCountdownElapsed, Consumer<String> onError) {
    var file = Paths.get(Configuration.INSTANCE.getLogFile());
    CsvEventStore eventStore = new CsvEventStore(file);
    var service = new ActivitiesServiceImpl(eventStore);
    return new ActivitySamplingViewModel(service, onCountdownElapsed, onError);
  }
}
