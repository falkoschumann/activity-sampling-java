/*
 * Activity Sampling
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.ui.shared;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class PeriodView {

  @FXML private Label periodOfLabel;
  @FXML private Label periodFromToLabel;
  @FXML private ChoiceBox<ChronoUnit> periodOfChoice;

  private final PeriodViewModel viewModel = new PeriodViewModel();

  @FXML
  private void initialize() {
    periodOfChoice.setConverter(new ChronoUnitStringConverter());
    periodOfChoice.getItems().addAll(ChronoUnit.DAYS, ChronoUnit.WEEKS, ChronoUnit.MONTHS);

    periodOfLabel.textProperty().bind(viewModel.periodOfLabelTextProperty());
    periodFromToLabel.textProperty().bind(viewModel.periodFromToLabelTextProperty());
    periodOfChoice.valueProperty().bindBidirectional(viewModel.periodOfChoiceValueProperty());
  }

  public final void addPeriodChangedListener(Consumer<Void> listener) {
    viewModel.addPeriodChangedListener(listener);
  }

  public final void removePeriodChangedListener(Consumer<Void> listener) {
    viewModel.removePeriodChangedListener(listener);
  }

  public void setPeriods(List<ChronoUnit> periods) {
    periodOfChoice.getItems().setAll(periods);
  }

  public void setPeriod(ChronoUnit period) {
    viewModel.setPeriodOfChoiceValue(period);
  }

  public final LocalDate getFrom() {
    return viewModel.getFrom();
  }

  public final LocalDate getTo() {
    return viewModel.getTo();
  }

  @FXML
  private void back() {
    viewModel.back();
  }

  @FXML
  private void forward() {
    viewModel.forward();
  }
}
