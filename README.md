# Neutron | An open-source essential suite for the Velocity proxy

## What is Neutron?
Neutron is an open-source plugin written to try to provide the bare essentials for servers using Velocity.

## What does it offer?
Currently, Neutron offers 4 commands:
  * `find` - allows you to find what server another player is on
  * `info` - provides information regarding a player
  * `glist` - shows a list of all servers along with how many players are connected
  * `send`- sends either a player, a server, or everyone to a specified server

Alongside the commands, Neutron offers 3 modules:
  * Commands:
    * Allows the customization of all command aliases
    * Disable each command individually, or disable them all
  * Locales:
    * Translate all messages per player using pre-defined localizations
    * English(`en_US`) is default and currently the only locale shipped with the plugin
    * Adding locales:
      1. Copy the existing `en_US.conf` file and rename it to the target locale
      2. Change any messages to your liking
      3. Save the file, and run /velocity reload in-game
  * Server list:
    * Provide a custom MOTD for your server in the multiplayer server list
    * Change the max players online:
      * `Current`: player count matches the number of players online
      * `OneMore`: player count shows the number of players online plus 1 
      * `Ping`: ping all online backend servers and sum the max player counts
      * `Static`: player count will always be the number defined under `max-player-count` 
    * Change the message shown when the mouse hovers over the online player count:
      * `Message`: preview will show the messages defined under `preview-messages`
      * `Players`: preview matches the vanilla server preview of showing online players
      * `Empty`: preview is empty

_Modules can be `enabled/disabled` in the `config.conf`_
_All modules support reloading with the `/velocity reload` command_

## Permissions
Permissions are simply in the way that all command permissions are `neutron.command.{command}`

For example: the permission for find is `neutron.command.find`
