# Skill Ranks

A RuneLite Plugin Hub plugin that adds your Old School RuneScape hiscore rank to the tooltip shown when you hover over a skill in the Skills tab.

## Features

- Shows the selected skill's global hiscore rank.
- Shows overall rank when hovering Total level.
- Displays `Unranked` when the hiscores do not provide a rank.
- Uses the current account's matching hiscores automatically.
- Lets you hide rank rows without disabling the plugin.
- Lets you hide ranks for selected skills in a collapsed per-skill settings section.
- Supports normal, seasonal, tournament, Deadman, and Fresh Start worlds through RuneLite's built-in hiscore client.

The plugin performs one hiscore lookup after login and caches the result for the session. The lookup sends the logged-in character name to Jagex's official Old School RuneScape hiscores service through RuneLite's built-in hiscore client.

## Development

Requires Java 11.

```text
./gradlew test
./gradlew run
```

When the development client is open, sign in, open the Skills tab, and hover over several skills. Confirm that the rank appears and the tooltip background grows to fit it. Disable Show ranks, then add skills under Hidden skills, and confirm the relevant tooltip rows no longer appear.

## Licence

BSD 2-Clause. See `LICENSE`.
