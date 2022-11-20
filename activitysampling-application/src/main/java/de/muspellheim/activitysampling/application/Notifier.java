package de.muspellheim.activitysampling.application;

import java.awt.*;

class Notifier {
  private TrayIcon trayIcon;

  Notifier() {
    if (SystemTray.isSupported()) {
      EventQueue.invokeLater(
          () -> {
            String imageUrl = getImageUrl();
            var url = getClass().getResource(imageUrl);
            var image = Toolkit.getDefaultToolkit().getImage(url);
            var tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(image, "Activity Sampling");
            trayIcon.setImageAutoSize(true);
            try {
              tray.add(trayIcon);
            } catch (AWTException ignore) {
              trayIcon = null;
            }
          });
    }
  }

  private static String getImageUrl() {
    var trayIconSize = SystemTray.getSystemTray().getTrayIconSize();
    var isRetina = isRetina();
    String imageUrl;
    if (trayIconSize.height == 16) {
      // Windows 10
      imageUrl = isRetina ? "/icons/punch-clock-32.png" : "/icons/punch-clock-16.png";
    } else if (trayIconSize.height == 20) {
      // macOS 12
      imageUrl = isRetina ? "/icons/punch-clock-40.png" : "/icons/punch-clock-20.png";
    } else {
      // Other OS
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

  void showNotification() {
    if (trayIcon == null) {
      return;
    }

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
