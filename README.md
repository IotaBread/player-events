# Player Events

A mod that executes and sends configurable commands and messages respectively when a player does something.

**Downloads are on [the releases page](https://github.com/ByMartrixx/join-messages/releases).**

The config file is located in the config directory (`<root>/config/player_events.json`) and looks like this:

```
//TODO: Example
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
#### Adding the API as a dependency
```
// TODO
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
