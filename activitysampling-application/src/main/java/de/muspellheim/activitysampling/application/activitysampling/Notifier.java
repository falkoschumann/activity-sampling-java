/*
 * Activity Sampling - Application
 * Copyright (c) 2022 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.application.activitysampling;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.time.Duration;
import javafx.application.Platform;

class Notifier {
  private static final int TRAY_ICON_SIZE_WINDOWS10 = 16;
  private static final int TRAY_ICON_SIZE_MACOS12 = 20;

  private final ActivitySamplingViewModel viewModel;
  private TrayIcon trayIcon;

  Notifier(ActivitySamplingViewModel viewModel) {
    this.viewModel = viewModel;
    if (SystemTray.isSupported()) {
      viewModel.addOnCountdownElapsedListener(v -> showNotification());
      EventQueue.invokeLater(
          () -> {
            var imageUrl = getImageUrl();
            var url = getClass().getResource(imageUrl);
            var image = Toolkit.getDefaultToolkit().getImage(url);
            var tray = SystemTray.getSystemTray();
            var menu = createPopupMenu();
            trayIcon = new TrayIcon(image, "Activity Sampling", menu);
            trayIcon.setImageAutoSize(true);
            try {
              tray.add(trayIcon);
            } catch (AWTException ignored) {
              trayIcon = null;
            }
          });
    }
  }

  private static String getImageUrl() {
    var trayIconSize = SystemTray.getSystemTray().getTrayIconSize();
    var isRetina = isRetina();
    String imageUrl;
    if (trayIconSize.height == TRAY_ICON_SIZE_WINDOWS10) {
      imageUrl = isRetina ? "/icons/punch-clock-32.png" : "/icons/punch-clock-16.png";
    } else if (trayIconSize.height == TRAY_ICON_SIZE_MACOS12) {
      imageUrl = isRetina ? "/icons/punch-clock-40.png" : "/icons/punch-clock-20.png";
    } else {
      imageUrl = "/icons/punch-clock-16.png";
    }
    return imageUrl;
  }

  private static boolean isRetina() {
    return !GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .getDefaultConfiguration()
        .getDefaultTransform()
        .isIdentity();
  }

  private PopupMenu createPopupMenu() {
    var menu = new PopupMenu();

    var startMenu = new Menu("Start");
    menu.add(startMenu);

    var start5minMenuItem = new MenuItem("5 min");
    start5minMenuItem.addActionListener(e -> start(Duration.ofMinutes(5)));
    startMenu.add(start5minMenuItem);

    var start10minMenuItem = new MenuItem("10 min");
    start10minMenuItem.addActionListener(e -> start(Duration.ofMinutes(10)));
    startMenu.add(start10minMenuItem);

    var start15minMenuItem = new MenuItem("15 min");
    start15minMenuItem.addActionListener(e -> start(Duration.ofMinutes(15)));
    startMenu.add(start15minMenuItem);

    var start20MinMenuItem = new MenuItem("20 min");
    start20MinMenuItem.addActionListener(e -> start(Duration.ofMinutes(20)));
    startMenu.add(start20MinMenuItem);

    var start30minMenuItem = new MenuItem("30 min");
    start30minMenuItem.addActionListener(e -> start(Duration.ofMinutes(30)));
    startMenu.add(start30minMenuItem);

    var start60minMenuItem = new MenuItem("60 min");
    start60minMenuItem.addActionListener(e -> start(Duration.ofMinutes(60)));
    startMenu.add(start60minMenuItem);

    var start1minMenuItem = new MenuItem("1 min");
    start1minMenuItem.addActionListener(e -> start(Duration.ofMinutes(1)));
    startMenu.add(start1minMenuItem);

    var stopMenuItem = new MenuItem("Stop");
    stopMenuItem.addActionListener(e -> stop());
    menu.add(stopMenuItem);

    return menu;
  }

  private void start(Duration duration) {
    Platform.runLater(() -> viewModel.startCountdown(duration));
  }

  private void stop() {
    Platform.runLater(viewModel::stopCountdown);
  }

  private void showNotification() {
    EventQueue.invokeLater(
        () -> trayIcon.displayMessage("What are you working on?", null, TrayIcon.MessageType.NONE));
  }

  void dispose() {
    if (trayIcon == null) {
      return;
    }

    EventQueue.invokeLater(
        () -> {
          var tray = SystemTray.getSystemTray();
          tray.remove(trayIcon);
        });
  }
}
