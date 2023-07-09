/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.activitysampling;

import de.muspellheim.activitysampling.util.EventEmitter;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javafx.application.Platform;

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
