# Changelog
## 2.4.3 [1.19]
- Fix a mixin error

## 2.4.2 [1.19] (20-12-2022)
- Update to 1.19.3

## 2.4.1 [1.19] (19-08-2022)
- Update to 1.19.2
- Fix custom commands triggering incorrectly

## 2.4.0 [1.19] (12-07-2022)
- Fix `/` executing every custom command
- Fix execution of commands not working (#26)
- Fix custom commands not doing anything

## 2.3.2 [1.18.2] (12-07-2022)
- Fix `/` executing every custom command

## 2.3.1 (21-06-2022)
* Update to 1.19

## 2.3.0 (09-06-2022)
* Refactor text parsing and placeholder replacement
* Fix first death event

## 2.2.4 (08-03-2022)
* Update to 1.18.2

## 2.2.3 (03-02-2022)
* Fix incompatibility with Kibe (#19)

## 2.2.2 (04-12-2021)
* Update to 1.18

## 2.2.1 (18-08-2021)
* Add `first_death` event
* Add missing test command `custom_commands`

## 2.2.0 (09-08-2021)
* Add datapack functions support
* Add random message picking

## 2.1.6 (28-07-2021)
* Fix error in console when executing command with braces

# 2.1.5 (25-07-2021)
* Fix Reload command not working? (#8)

# 2.1.4 (19-07-2021)
* Fix error ingame when executing a custom command

# 2.1.3 (15-07-2021)
* Add command listening

## 2.1.2 (28-05-2021)
* Add [COMMAND] prefix to sent/printed commands when testing actions
* Add first_join event
* Fix killPlayer test
* Use comma as decimal separator for placeholder doubles

## 2.1.1 (24-05-2021)
* Fix a crash in snapshots because of an intermediary change

## 2.1.0 (17-05-2021)
* Code cleanup and improvements code-wise
* Maven repository has been changed to https://maven.bymartrixx.me
* Remove pointless mod icon that made the jar file bigger
* Changed the package to me.bymartrixx
* Remove pointless log messages
* Events can no longer be cancelled
* Added support to reference player coordinates
* Added support for FabricPlaceholderAPI

## 2.1.0-beta.1 (25-03-2021)
This is a prerelease of the 2.1.0 version of the mod, as I still have to figure out some things and I have some other work to do.

* Fix [BUG] Sends player kill messages when killing entity (#5)
* Add support for [PlaceholderAPI](https://github.com/Patbox/FabricPlaceholderAPI). Currently, the placeholder support isn't fully implemented, so it may have bugs.
* Clean up of some code, mainly removing the unused test mod jar and some other things in build.gradle

## 2.0.0 (02-12-2020)
* Mayor refactor of the code
* Move package from `io.github.bymartrixx.player_events` to `io.github.bymartrixx.playerevents`
* Classes of `io.github.bymartrixx.player_events.api` are now deprecated
* Add Events for Player Kills, and Entity Kills (#2)
* Add option to not broadcast to everyone
