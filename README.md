# Join Messages

A mod that sends configurable chat messages and commands when a player joins the server.

The config file is located in the config directory (`<root>/config/join_messages.json`) and looks like this:

```JSON
{
  "messages": [
    "Welcome %s! ",
    "/say Hello %s"
  ]
}
```

The JSON file is declaring, under the `messages` array, what it's going to be sent when a player joins. Supports [color codes](https://minecraft.gamepedia.com/Formatting_codes#Color_codes) too! Every instance of `%s` will be replaced with the player's name.
Commands must start with `/`.
Use `/join_messages reload` to reload the mod configuration.

## Compiling

1. Clone the repository to your device.
2. On a command prompt run `./gradlew build` to build the mod.
3. The mod files will be located in the `build/libs/` folder
