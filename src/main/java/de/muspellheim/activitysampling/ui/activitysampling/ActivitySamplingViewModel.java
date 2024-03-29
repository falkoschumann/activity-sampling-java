/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.activitysampling;

import de.muspellheim.activitysampling.application.ActivitiesService;
import de.muspellheim.activitysampling.domain.Activity;
import de.muspellheim.activitysampling.domain.RecentActivities;
import de.muspellheim.activitysampling.util.Durations;
import de.muspellheim.activitysampling.util.EventEmitter;
import de.muspellheim.activitysampling.util.OutputTracker;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ActivitySamplingViewModel {
  private final ActivitiesService activitiesService;
  private final Locale locale;
  private final Clock clock;

  // State
  private Duration interval = Duration.ofMinutes(20);
  private final ObjectProperty<Duration> countdown = new SimpleObjectProperty<>(interval);
  private final BooleanProperty countdownActive = new SimpleBooleanProperty(false);
  private final BooleanProperty intervalLogged = new SimpleBooleanProperty(false);

  /* *************************************************************************
   *                                                                         *
   * Constructors                                                            *
   *                                                                         *
   **************************************************************************/

  public ActivitySamplingViewModel(ActivitiesService activitiesService) {
    this(activitiesService, Locale.getDefault(), Clock.systemDefaultZone());
  }

  public ActivitySamplingViewModel(
      ActivitiesService activitiesService, Locale locale, Clock clock) {
    this.activitiesService = activitiesService;
    this.locale = locale;
    this.clock = clock;
    // TODO Make default interval configurable; use when countdown off
  }

  /* *************************************************************************
   *                                                                         *
   * Events                                                                  *
   *                                                                         *
   **************************************************************************/

  private final EventEmitter<LocalDateTime> countdownElapsed = new EventEmitter<>();

  public void addCountdownElapsedListener(Consumer<LocalDateTime> listener) {
    countdownElapsed.addListener(listener);
  }

  public void removeCountdownElapsedListener(Consumer<LocalDateTime> listener) {
    countdownElapsed.removeListener(listener);
  }

  public OutputTracker<LocalDateTime> trackCountdownElapsed() {
    return new OutputTracker<>(countdownElapsed);
  }

  private final EventEmitter<Throwable> errorOccurred = new EventEmitter<>();

  public void addErrorOccurredListener(Consumer<Throwable> listener) {
    errorOccurred.addListener(listener);
  }

  public void removeErrorOccurredListener(Consumer<Throwable> listener) {
    errorOccurred.removeListener(listener);
  }

  public OutputTracker<Throwable> trackErrorOccurred() {
    return new OutputTracker<>(errorOccurred);
  }

  /* *************************************************************************
   *                                                                         *
   * Properties                                                              *
   *                                                                         *
   **************************************************************************/

  // --- stopMenuItemDisable

  private final ObservableBooleanValue stopMenuItemDisable =
      Bindings.createBooleanBinding(() -> countdownActive.not().get(), countdownActive);

  public ObservableBooleanValue stopMenuItemDisableProperty() {
    return stopMenuItemDisable;
  }

  public final boolean isStopMenuItemDisable() {
    return stopMenuItemDisable.get();
  }

  // --- formDisable

  private final ObservableBooleanValue formDisable = countdownActive.and(intervalLogged);

  public ObservableBooleanValue formDisableProperty() {
    return formDisable;
  }

  public final boolean isFormDisable() {
    return formDisable.get();
  }

  // --- clientText

  private final StringProperty clientText = new SimpleStringProperty("");

  public StringProperty clientTextProperty() {
    return clientText;
  }

  public final String getClientText() {
    return clientText.get();
  }

  public final void setClientText(String value) {
    clientTextProperty().set(value);
  }

  // --- projectText

  private final StringProperty projectText = new SimpleStringProperty("");

  public StringProperty projectTextProperty() {
    return projectText;
  }

  public final String getProjectText() {
    return projectText.get();
  }

  public final void setProjectText(String value) {
    projectTextProperty().set(value);
  }

  // --- taskText

  private final StringProperty taskText = new SimpleStringProperty("");

  public StringProperty taskTextProperty() {
    return taskText;
  }

  public final String getTaskText() {
    return taskText.get();
  }

  public final void setTaskText(String value) {
    taskTextProperty().set(value);
  }

  // --- notesText

  private final StringProperty notesText = new SimpleStringProperty("");

  public StringProperty notesTextProperty() {
    return notesText;
  }

  public final String getNotesText() {
    return notesText.get();
  }

  public final void setNotesText(String value) {
    notesTextProperty().set(value);
  }

  // --- logButtonDisable

  private final ObservableBooleanValue logButtonDisable =
      Bindings.createBooleanBinding(() -> clientText.get().isBlank(), clientText)
          .or(Bindings.createBooleanBinding(() -> projectText.get().isBlank(), projectText))
          .or(Bindings.createBooleanBinding(() -> taskText.get().isBlank(), taskText));

  public ObservableBooleanValue logButtonDisableProperty() {
    return logButtonDisable;
  }

  public final boolean isLogButtonDisable() {
    return logButtonDisable.get();
  }

  // --- countdownLabelText

  private final ObservableStringValue countdownLabelText =
      Bindings.createStringBinding(
          () -> Durations.format(countdown.get(), FormatStyle.MEDIUM), countdown);

  public ObservableStringValue countdownLabelTextProperty() {
    return countdownLabelText;
  }

  public final String getCountdownLabelText() {
    return countdownLabelText.get();
  }

  // --- countdownProgress

  private final ObservableDoubleValue countdownProgress =
      Bindings.createDoubleBinding(
          () -> {
            if (interval.isZero()) {
              return 0.0;
            }

            var elapsedSeconds = (double) interval.minus(countdown.get()).getSeconds();
            return elapsedSeconds / interval.toSeconds();
          },
          countdown);

  public ObservableDoubleValue countdownProgressProperty() {
    return countdownProgress;
  }

  public final double getCountdownProgress() {
    return countdownProgress.get();
  }

  // --- recentActivityItems

  private final ObservableList<ActivityItem> recentActivityItems =
      FXCollections.observableArrayList();

  public final ObservableList<ActivityItem> getRecentActivities() {
    return recentActivityItems;
  }

  // --- hoursTodayLabelText

  private final ReadOnlyStringWrapper hoursTodayLabelText = new ReadOnlyStringWrapper("00:00");

  public ObservableStringValue hoursTodayTextProperty() {
    return hoursTodayLabelText.getReadOnlyProperty();
  }

  public final String getHoursTodayLabelText() {
    return hoursTodayLabelText.get();
  }

  // --- hoursYesterdayLabelText

  private final ReadOnlyStringWrapper hoursYesterdayLabelText = new ReadOnlyStringWrapper("00:00");

  public ObservableStringValue hoursYesterdayTextProperty() {
    return hoursYesterdayLabelText.getReadOnlyProperty();
  }

  public final String getHoursYesterdayLabelText() {
    return hoursYesterdayLabelText.get();
  }

  // --- hoursThisWeekLabelText

  private final ReadOnlyStringWrapper hoursThisWeekLabelText = new ReadOnlyStringWrapper("00:00");

  public ObservableStringValue hoursThisWeekTextProperty() {
    return hoursThisWeekLabelText.getReadOnlyProperty();
  }

  public final String getHoursThisWeekLabelText() {
    return hoursThisWeekLabelText.get();
  }

  // --- hoursThisMonthLabelText

  private final ReadOnlyStringWrapper hoursThisMonthLabelText = new ReadOnlyStringWrapper("00:00");

  public ObservableStringValue hoursThisMonthTextProperty() {
    return hoursThisMonthLabelText.getReadOnlyProperty();
  }

  public final String getHoursThisMonthLabelText() {
    return hoursThisMonthLabelText.get();
  }

  /* *************************************************************************
   *                                                                         *
   *  API                                                                    *
   *                                                                         *
   **************************************************************************/

  public void load() {
    try {
      var recentActivities = activitiesService.getRecentActivities();
      updateActivityItems(recentActivities);
      updateTimeSummary(recentActivities);
    } catch (Exception e) {
      errorOccurred.emit(new Exception("Failed to load activities.", e));
    }
  }

  private void updateActivityItems(RecentActivities recentActivities) {
    var items = new ArrayList<ActivityItem>();
    for (var day : recentActivities.workingDays()) {
      items.add(ActivityItem.newHeader(day, locale));
      for (var activity : day.activities()) {
        items.add(ActivityItem.newItem(activity, locale));
      }
    }
    recentActivityItems.setAll(items);
  }

  private void updateTimeSummary(RecentActivities recentActivities) {
    var timeSummary = recentActivities.timeSummary();
    hoursTodayLabelText.set(Durations.format(timeSummary.hoursToday(), FormatStyle.SHORT));
    hoursYesterdayLabelText.set(Durations.format(timeSummary.hoursYesterday(), FormatStyle.SHORT));
    hoursThisWeekLabelText.set(Durations.format(timeSummary.hoursThisWeek(), FormatStyle.SHORT));
    hoursThisMonthLabelText.set(Durations.format(timeSummary.hoursThisMonth(), FormatStyle.SHORT));
  }

  public void logActivity() {
    // TODO log the timestamp when the countdown elapsed
    // TODO log the current timestamp when the countdown is not active
    try {
      activitiesService.logActivity(
          Activity.builder()
              .timestamp(LocalDateTime.now(clock))
              .duration(interval)
              .client(clientText.get())
              .project(projectText.get())
              .task(taskText.get())
              .notes(notesText.get())
              .build());
      intervalLogged.set(true);
      load();
    } catch (Exception e) {
      errorOccurred.emit(new Exception("Failed to log activity.", e));
    }
  }

  public void setActivity(ActivityItem item) {
    setClientText(item.client());
    setProjectText(item.project());
    setTaskText(item.task());
    setNotesText(item.notes());
  }

  public void startCountdown(Duration interval) {
    this.interval = interval;
    countdown.set(interval);
    countdownActive.set(true);
    intervalLogged.set(true);
  }

  public void progressCountdown(Duration duration) {
    if (!countdownActive.get()) {
      return;
    }

    countdown.set(countdown.get().minus(duration));
    if (countdown.get().isZero() || countdown.get().isNegative()) {
      intervalLogged.set(false);
      countdownElapsed.emit(LocalDateTime.now(clock));
      countdown.set(interval);
    }
  }

  public void stopCountdown() {
    countdownActive.set(false);
  }
}
