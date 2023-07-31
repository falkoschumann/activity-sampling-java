# Contributing

Build with `./build.sh`. Other useful tasks:

- `./build clean` - Remove build artifacts.
- `./build format` - Format the source code according to the code style.

Used code style:
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

Build for macOS Intel and M1.

### Distribute for macOS

Replace `$MAC_SIGNING_USERNAME`, `$MAC_SIGNING_PASSWORD` and `{RequestUUID}`
with appropriate values.

```bash
xcrun notarytool submit \
  --team-id QC6EN37P56 \
  --apple-id $MAC_SIGNING_USERNAME \
  --password $MAC_SIGNING_PASSWORD \
  --wait \
  activity-sampling-1.0.0-m1.dmg
```
