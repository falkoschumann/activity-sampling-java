# Contributing

- Code Style [Google Java Style Guide][https://google.github.io/styleguide/javaguide.html] wird beim Build geprüft und
  mit `./gradlew spotlessApply` formatiert
- Release erstellen mit `./gradlew jpackage`
- Build for macOS Intel and M1

### Distribute for macOS

Im Folgenden müssen `$MAC_SIGNING_USERNAME`, `$MAC_SIGNING_PASSWORD`
und `{RequestUUID}` passend ersetzt werden.

1. Notarisierung der App-Distribution beantragen:

   `xcrun altool --notarize-app --primary-bundle-id de.muspellheim.activitysampling --username $MAC_SIGNING_USERNAME --password $MAC_SIGNING_PASSWORD --file activity-sampling/build/jpackage/activity-sampling-1.1.0.dmg`

2. Status der Notarisierung prüfen:

   `xcrun altool --notarization-info {RequestUUID} --username $MAC_SIGNING_USERNAME --password $MAC_SIGNING_PASSWORD`

3. Wenn Notarisierung beglaubigt, App-Distribution um Information zur
   Notarisierung ergänzen:

   `xcrun stapler staple activity-sampling/build/jpackage/activity-sampling-1.1.0.dmg`
