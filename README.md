# Player Events

[ ![GitHub release](https://img.shields.io/github/v/release/ByMartrixx/player-events) ](https://github.com/ByMartrixx/player-events/releases/latest)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/ByMartrixx/player-events/build)

<a href='https://www.curseforge.com/minecraft/mc-mods/fabric-api'><img src='https://i.imgur.com/Ol1Tcf8.png' width="150"></a>

**Note: this mod is server side only and won't work on clients**

A Fabric mod that executes and sends configurable commands and messages respectively on certain
events triggered by a player, such as Dying, Joining a server, Killing another player, etc.

**Since 2.2.0** Datapacks can define functions that will be executed on an event, using the corresponding function tag `#player_events:<event>`

The config file is located in the config directory (`config/player_events.json`) and looks like this:

```JSON
{
  "first_death": {
    "actions": [
      "${player} died for the first time"
    ],
    "broadcast_to_everyone": true,
    "pick_message_randomly": false
  },
  "death": {
    "actions": [
      "${player} just died!"
    ],
    "broadcast_to_everyone": true,
    "pick_message_randomly": false
  },
  "first_join": {
    "actions": [
      "Welcome to the server ${player}! Remember to read the rules"
    ],
    "broadcast_to_everyone": false,
    "pick_message_randomly": false
  },
  "join": {
    "actions": [
      "Welcome ${player}",
      "/say Hello ${player}"
    ],
    "broadcast_to_everyone": true,
    "pick_message_randomly": false
  },
  "kill_entity": {
    "actions": [
      "${player} killed ${killedEntity}"
    ],
    "broadcast_to_everyone": true,
    "pick_message_randomly": false
  },
  "kill_player": {
    "actions": [
      "${player} killed ${killedPlayer}",
      "F ${killedPlayer}"
    ],
    "broadcast_to_everyone": true,
    "pick_message_randomly": true
  },
  "leave": {
    "actions": [
      "Goodbye ${player}!",
      "/say Hope to see you soon ${player}"
    ],
    "broadcast_to_everyone": true,
    "pick_message_randomly": false
  },
  "custom_commands": [
    {
      "command": "/plugins",
      "actions": [
        "Hey! We don't use plugins"
      ],
      "broadcast_to_everyone": false,
      "pick_message_randomly": false
    },
    {
      "command": "/spawn",
      "actions": [
        "/tp ${player} 0 64 0 0 0"
      ],
      "broadcast_to_everyone": true,
      "pick_message_randomly": false
    }
  ]
}
```

On the JSON file you can declare, under the `actions` array on each `<event>` object, what is going
to be sent and/or executed on that event. You can also set these messages to be sent only to the
player by setting `broadcast_to_everyone` to `false`, but this won't work with events like `leave`
(because the player isn't in the server anymore).

**Since 2.2.0** You can choose to randomly send one of the defined messages for a given event by setting `pick_message_randomly` to `true`.

Every event has a `${player}` token, and each instance of this token will be replaced with the player
that triggers the event. Other events have extra tokens that work the same way.
Most (if not all) tokens have properties that can be accessed with something like `${player.name}`.
Here is a list of all the properties:
- `display`
  Entity's display name, the one you see in the player list/chat. Example: "[Team blue] Tom421"
- `entityName`
  Entity's name, for players it's the player's name, for other entities it's the entity's uuid
- `x` `y` `z`
  Entity coordinates
- More coming soon™

~~Supports [color codes](https://minecraft.gamepedia.com/Formatting_codes#Color_codes) too!~~ Formatting with color
codes breaks if you use a placeholder, use [Simple Text Format](https://placeholders.pb4.eu/user/text-format/) instead,
which is far more complete than vanilla color codes.

Use `/pe reload` or `/player_events reload` to reload the mod config.

You can use `/pe test <event>` or `/player_events test <event>` to test the actions on a specific
event, or use `/pe test *` to test every event.

### 2.2.1 supported events
* `first_death` - Executed when a player dies for first time.
* `death` - Executed when a player dies.
* `first_join` - Executed when a player joins for first time.
* `join` - Executed when a player joins.
* `kill_entity` - Executed when a player kills an entity. Extra tokens:
    * `${killedEntity}` - the killed entity.
* `kill_player` - Executed when a player kills another player. Extra tokens:
    * `${killedPlayer}` - the killed player.
* `leave` - Executed when a player leaves.
* `custom_commands` - Custom defined events triggered by using a defined command. **Note: This event does not support datapack functions**

Additionally, you can create simple commands (if you want a more complex command, this mod isn't what you
are looking for) or listen to existing ones.

### Troubleshooting
If you get an error when initializing the server or when an action should be executed, here are
steps on how to solve it.
- "Invalid JSON syntax in the config file": This is most likely caused by having a command with
  unescaped double quotes. To fix it:
  1. Check what the exception says in the line below. It will indicate exactly where the error is.
  2. Escape any double quotes inside the action with backslashes like `"` -> `\"`
- "Invalid escape sequence"
  This is caused because of an improperly escaped escape sequence like `\n` or `\u`. To fix it,
  just add a backslash before the escape sequence backslash, for example `\n` -> `\\n` or `\\u` -> `\\\\u`

## Developing
This part is for mod developers that would like to use the mod api.

### Compiling

1. Clone or download the repository
2. On a command prompt run `gradlew build` to compile the mod. (If you only need the API files,
   you can run `gradlew api:build` instead) . You'll find the compiled `.jar` files under
   `<repository>/build/libs` and `<repository>/api/build/libs`

### API
#### Adding the API as a dependency of your mod
Add the following snippet to your `build.gradle`:
```groovy
repositories {
    maven {
        url 'https://maven.bymartrixx.me'    
    }
}

dependencies {
    // Using the version from the gradle.properties
    modImplementation "me.bymartrixx.player-events:api:${project.player_events_api_version}"

    // Directly setting the version (replace 2.1.3 with the latest release available)
    modImplementation "me.bymartrixx.player-events:api:2.1.3"
}
```
Add this snippet to your `gradle.properties` if you aren't directly setting the version to the build.gradle file:
```properties
# Replace 2.1.3 with the latest release available
player_events_api_version = 2.1.3
```

Also, add this snippet to your `fabric.mod.json` if you want your mod to depend on the api:
```json
{
  "depends": {
    "player_events_api": ">=2.0.0"
  }
}
```

#### Events
* `first_death` - `me.bymartrixx.playerevents.api.event.PlayerFirstDeathCallback.EVENT`
* `death` - `me.bymartrixx.playerevents.api.event.PlayerDeathCallback.EVENT`
* `first_join` - `me.bymartrixx.playerevents.api.event.PlayerFirstJoinCallback.EVENT`
* `join` - `me.bymartrixx.playerevents.api.event.PlayerJoinCallback.EVENT`
* `kill_entity` - `me.bymartrixx.playerevents.api.event.PlayerKillEntityCallback.EVENT`
* `kill_player` - `me.bymartrixx.playerevents.api.event.PlayerKillPlayerCallback.EVENT`
* `leave` - `me.bymartrixx.playerevents.api.event.PlayerLeaveCallback.EVENT`
* Command executed - `me.bymartrixx.playerevents.api.event.CommandExecutionCallback.EVENT`

#### Using the events
Example snippet
```java
public class FooMod implements DedicatedServerModInitializer {
    public void onInitializeServer() {
        PlayerDeathCallback.EVENT.register((player, source) -> {
            // Do something
        });

        PlayerKillEntityCallback.EVENT.register((player, killedEntity) -> {
            // Do something
        });
    }
}
```
