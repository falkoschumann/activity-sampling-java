# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## Added

- Create time report.
- Group time report grouped by client, project or task.

## [1.2.0] - 2023-07-16

## Added

- Save activity with client, project, task and notes.
- Create timesheet for client, project and task.

### Changed

- Save timestamp with local timezone instead of UTC.

### Fixed

- Duration was always logged with 20 minutes.

## [1.1.0] - 2022-11-17

### Added

- Show time summary for today, yesterday, this week and this month.

### Changed

- Add column Duration to log file.

## [1.0.0] - 2022-11-16

### Added

- Archive timestamp and description of activity.
- Show recent activities.
- Select one of the recent activities as the current one.
- Ask at a fixed intervall like 15, 20 30 or 60 minutes.
- Notify when interval expired.

[Unreleased]: https://github.com/falkoschumann/activity-sampling-java/compare/v1.2.0...HEAD
[1.2.0]: https://github.com/falkoschumann/activity-sampling-java/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/falkoschumann/activity-sampling-java/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/falkoschumann/activity-sampling-java/releases/tag/v1.0.0
