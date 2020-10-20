# Player Events

[ ![Download](https://api.bintray.com/packages/bymartrixx/maven/player_events_api/images/download.svg) ](https://github.com/ByMartrixx/join-messages/releases/tag/1.0.0)
[ ![Build Status](https://travis-ci.com/ByMartrixx/player-events.svg?branch=master)](https://travis-ci.com/ByMartrixx/player-events)

<a href='https://www.curseforge.com/minecraft/mc-mods/fabric-api'><img src='https://i.imgur.com/Ol1Tcf8.png' width="150"></a>

A mod that executes and sends configurable commands and messages respectively when a player does something.

The config file is located in the config directory (`<root>/config/player_events.json`) and looks like this:

```
{
  "death_actions": [
    "%s just died! F"
  ],
  "join_actions": [
    "Welcome %s",
    "/say Hello %s"
  ],
  "leave_actions": [
    "Goodbye %s!",
    "/say Hope to see you soon %s"
  ]
}
```

The JSON file is declaring, under the `<event>_actions` array, what it's going to be sent and/or executed on the `<event>`.
Every instance of `%s` will be replaced with the player's name. Commands on the config must start with `/`.

Supports [color codes](https://minecraft.gamepedia.com/Formatting_codes#Color_codes) too!

Use `/pe reload` or `/player_events reload` to reload the mod config.

You can use `/pe test <event>` or `/player_events test <event>` to test the actions on a specific event, or use `/pe test *` to test every event.

### Current supported events
* `death` - Executed when a player dies.
* `join` - Executed when a player joins.
* `leave` - Executed when a player leaves.

## Developing
This part is intended for developers.

### Compiling

1. Clone or download the repository
2. On a command prompt run `gradlew build` to compile the mod. (If you only need the API files, you can run `gradlew player-events-api:build` instead)
3. You'll find the compiled `.jar` files under `<repository>/build/libs` and `<repository>/player-events-api/build/libs`

### API
#### Adding the API as a dependency of your mod
Add the following text to your `build.gradle`:
```groovy
repositories {
    maven {
        url 'https://dl.bintray.com/bymartrixx/maven'    
    }
}

dependencies {
    modImplementation "io.github.bymartrixx.player_events.api:player-events-api:${project.player_events_api_version}"
}
```
And this to your `gradle.properties`
```properties
player_events_api_version = 1.0.0
```

Also, this to your `fabric.mod.json`:
```json
{
  "depends": {
    "player_events_api": ">=1.0.0"
  }
}
```

#### Events
* `death` - `io.github.bymartrixx.player_events.api.event.PlayerDeathCallback.EVENT`
* `join` - `io.github.bymartrixx.player_events.api.event.PlayerJoinCallback.EVENT`
* `leave` - `io.github.bymartrixx.player_events.api.event.PlayerLeaveCallback.EVENT`

#### Using the events
```JAVA
public class FooClass {
    public void fooMethod() {
        PlayerDeathCallback.EVENT.register((player, source) -> {
            // Do something
            return ActionResult.PASS;
        });
    }
}
```
