/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import de.muspellheim.activitysampling.domain.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import javafx.application.*;

class SystemClock {
  private final EventEmitter<Duration> onTick = new EventEmitter<>();

  void addOnTickListener(Consumer<Duration> listener) {
    onTick.addListener(listener);
  }

  void removeOnTickListener(Consumer<Duration> listener) {
    onTick.removeListener(listener);
  }

  SystemClock() {
    var task =
        new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(() -> onTick.emit(Duration.ofSeconds(1)));
          }
        };
    var timer = new Timer("System Clock", true);
    timer.scheduleAtFixedRate(task, 0, TimeUnit.SECONDS.toMillis(1));
  }
}
