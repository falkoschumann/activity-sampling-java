# Contributing

- Code Style
  [Google Java Style Guide][https://google.github.io/styleguide/javaguide.html]
  wird beim Build geprüft und mit `./build.sh format` formatiert
- Release erstellen mit `./build.sh`
- Build for macOS Intel and M1

### Distribute for macOS

Im Folgenden müssen `$MAC_SIGNING_USERNAME`, `$MAC_SIGNING_PASSWORD`
und `{RequestUUID}` passend ersetzt werden.

    xcrun notarytool submit \
        --team-id QC6EN37P56 \
        --apple-id $MAC_SIGNING_USERNAME \
        --password $MAC_SIGNING_PASSWORD \
        activity-sampling-1.2.0-m1.dmg --wait
