# Player Events

[ ![Download](https://api.bintray.com/packages/bymartrixx/maven/player_events_api/images/download.svg) ](https://github.com/ByMartrixx/join-messages/releases/tag/1.0.0)
[ ![Build Status](https://travis-ci.com/ByMartrixx/player-events.svg?branch=master)](https://travis-ci.com/ByMartrixx/player-events)

<a href='https://www.curseforge.com/minecraft/mc-mods/fabric-api'><img src='https://i.imgur.com/Ol1Tcf8.png' width="150"></a>

**Note: this mod is server side only and won't work on clients**

A Fabric mod that executes and sends configurable commands and messages respectively on certain events triggered by a player, such as Dying, Joining a server, Killing another player, etc.

The config file is located in the config directory (`config/player_events.json`) and looks like this:

```JSON
{
  "death": {
    "actions": [
      "${player} just died! F"
    ],
    "broadcast_to_everyone": true
  },
  "join": {
    "actions": [
      "Welcome ${player}",
      "/say Hello ${player}"
    ],
    "broadcast_to_everyone": true
  },
  "kill_entity": {
    "actions": [
      "${player} killed ${killedEntity}"
    ],
    "broadcast_to_everyone": true
  },
  "kill_player": {
    "actions": [
      "${player} killed ${killedPlayer}",
      "F ${killedPlayer}"
    ],
    "broadcast_to_everyone": true
  },
  "leave": {
    "actions": [
      "Goodbye ${player}!",
      "/say Hope to see you soon ${player}"
    ],
    "broadcast_to_everyone": true
  }
}
```

On the JSON file you can declare, under the `actions` array on each `<event>` object, what is going to be sent and/or executed on that event. You can also set these messages to be sent only to the player by setting `broadcast_to_everyone` to `false`, but this won't work with events like `leave` (because the player isn't in the server anymore).

Every event has a `${player}` token, and each instance of this token will be replaced with the player that triggers the event. Other events have extra tokens that work the same way.
As of 2.0.0, commands remain unsupported for these tokens and only `${player}` works correctly. This functionality will be added on a future release.

**Supports [color codes](https://minecraft.gamepedia.com/Formatting_codes#Color_codes) too!**

Use `/pe reload` or `/player_events reload` to reload the mod config.

You can use `/pe test <event>` or `/player_events test <event>` to test the actions on a specific event, or use `/pe test *` to test every event.

### 2.0.0 supported events
* `death` - Executed when a player dies.
* `join` - Executed when a player joins.
* `kill_entity` - Executed when a player kills an entity. 
  * **Extra tokens:**
  * `${killedEntity}` - the killed entity.
* `kill_player` - Executed when a player kills another player.
  * **Extra tokens:**
  * `${killedPlayer}` - the killed player.
* `leave` - Executed when a player leaves.

## Developing
This part is for mod developers that would like to use the mod api.

### Compiling

1. Clone or download the repository
2. On a command prompt run `gradlew build` to compile the mod. (If you only need the API files, you can run `gradlew player-events-api:build` instead)
3. You'll find the compiled `.jar` files under `<repository>/build/libs` and `<repository>/player-events-api/build/libs`

### API
#### Adding the API as a dependency of your mod
Add the following text to your `build.gradle`:
##### Starting from May 1st you won't be able to use the api this way
```groovy
repositories {
    maven {
        url 'https://dl.bintray.com/bymartrixx/maven'    
    }
}

dependencies {
    // Using the version from the gradle.properties
    modImplementation "io.github.bymartrixx.playerevents:player-events-api:${project.player_events_api_version}"

    // Directly setting the version
    modImplementation "io.github.bymartrixx.playerevents:player-events-api:2.0.0"
}
```
Add this to your `gradle.properties` (Only if you aren't directly setting the version to the build.gradle file):
```properties
player_events_api_version = 2.0.0
```

Also, add this to your `fabric.mod.json`:
```json
{
  "depends": {
    "player_events_api": ">=2.0.0"
  }
}
```

#### Events
* `death` - `io.github.bymartrixx.playerevents.api.event.PlayerDeathCallback.EVENT`
* `join` - `io.github.bymartrixx.playerevents.api.event.PlayerJoinCallback.EVENT`
* `kill_entity` - `io.github.bymartrixx.playerevents.api.event.PlayerKillEntityCallback.EVENT`
* `kill_player` - `io.github.bymartrixx.playerevents.api.event.PlayerKillPlayerCallback.EVENT`
* `leave` - `io.github.bymartrixx.playerevents.api.event.PlayerLeaveCallback.EVENT`

**Note: The package `io.github.bymartrixx.player_events.api` has been moved to `io.github.bymartrixx.playerevents.api`. 1.0.0 classes still work but are now deprecated and will likely be removed in a next release. Please update your classes ASAP**

#### Using the events
```java
public class FooMod implements DedicatedServerModInitializer {
    public void onInitializeServer() {
        PlayerDeathCallback.EVENT.register((player, source) -> {
            // Do something
            return ActionResult.PASS;
        });

        PlayerKillEntityCallback.EVENT.register((player, killedEntity) -> {
            // Do something
            return ActionResult.PASS;
        });
    }
}
```
