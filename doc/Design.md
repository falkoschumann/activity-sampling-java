# Activity Sampling

Define slices with 4 patterns:

- Command pattern: trigger &rarr; command &rarr; event(s)
- View pattern: event(s) &rarr; view
- Automation pattern: event(s) &rarr; view &rarr; automated trigger &rarr;
  command &rarr; event(s)
- Translation pattern: event(s) (source system) &rarr; view &rarr; automated
  trigger &rarr; command &rarr; event(s) (other systems)

For more details, see [Event Modeling](https://eventmodeling.org).

## Commands

- UI &rarr; Log activity &rarr; Activity logged
- UI &rarr; Change duration &rarr; Duration changed
- UI &rarr; Change capacity &rarr; Capacity changed
- UI &rarr; Update holidays &rarr; Holidays updated

## Views

- Activity logged, capacity changed, holidays updated &rarr; Recent activities
  with
  time summary and capacity
- Activity logged &rarr; Timesheet
- Duration changed &rarr; Duration
- Capacity changed &rarr; User's capacity
- Holidays updated &rarr; Holidays
