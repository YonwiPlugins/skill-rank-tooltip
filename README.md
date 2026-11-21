# Skill Ranks

A RuneLite Plugin Hub plugin that adds your Old School RuneScape hiscore rank to the tooltip shown when you hover over a skill in the Skills tab.

## Features

- Shows the selected skill's global hiscore rank.
- Shows overall rank when hovering Total level.
- Displays `Unranked` when the hiscores do not provide a rank.
- Uses the current account's matching hiscores automatically.
- Shows the detected account mode by name and lets players switch to all-player ranks.
- Supports Hardcore Main accounts, using the Hardcore Main hiscores.
- Names Group Ironman, Hardcore Group Ironman and Unranked Group Ironman accounts. These use the normal hiscores, because Jagex's group hiscores rank whole groups rather than players.
- Optional 1 Defence Pure and Level 3 Skiller rank categories. If the account isn't on that build's hiscores, the account mode's rank is shown instead.
- "All skills" and "All 99s" boxes at the top of the list tick every skill, or every skill at 99 (including new 99s as they happen). Unticking a skill they cover turns them off. RuneLite only redraws the settings panel when it is reopened, so reopen it to see the ticks change.
- Provides one open "Show ranks for:" list with an enabled-by-default checkbox for every skill and Total level.
- Supports normal, Hardcore Main, seasonal, tournament, Deadman, and Fresh Start worlds through RuneLite's built-in hiscore client.

The plugin performs one hiscore lookup after login and caches the result for the session. The lookup sends the logged-in character name to Jagex's official Old School RuneScape hiscores service through RuneLite's built-in hiscore client.

## Development

Requires Java 11.

```text
./gradlew test
./gradlew run
```

When the development client is open, sign in, open the Skills tab, and hover over several skills. Confirm that the rank appears and the tooltip background grows to fit it. Untick individual skill or Total level checkboxes under Show ranks for, and confirm the relevant tooltip rows no longer appear.

## Licence

BSD 2-Clause. See `LICENSE`.
