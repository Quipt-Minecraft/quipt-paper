# QUIPT [![Latest build status](https://ci.vanillaflux.com/job/quipt-paper/badge/icon)](https://ci.vanillaflux.com/job/quipt-paper)
[![available on github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/QuickScythe/QUIPT)
[![available on Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://www.modrinth.com/plugin/QUIPT)
[![discord](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-singular_vector.svg)](https://discord.gg/EhfMJmjTXh)
[![paper](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/paper_vector.svg)](https://papermc.io/downloads/paper)

QUIPT is an ambitious project that aims to provide a handful of tools to both server owners and developers. Be sure to check out the [wiki](https://github.com/QuickScythe/QUIPT/wiki) for a more in-depth description of how to use this plugin.

## Server Owner Features
QUIPT was created with developers in mind, however, also provides some features for server owners to use "out of the box"

### Resource Pack Management
QUIPT comes with an automatic resourcepack manager. Fill out the `plugins/quipt-paper/resources.json` file with the GitHub repository information that contains your server resourcepack, and make sure to set `serverIp` to the IP your server is being hosted on. You can manually trigger a pack update by typing `/resourcepack update`, or you can set up GitHub to send a webhook to`<your-ip>:<resource-pack-port>/update/` to automatically update the pack every time the repository receives a new push.

### Teleportation Management
QUIPT provides a simple way to manage teleportation requests. This applies to simple player-to-player teleports, but can also be configured to work as spawn points, warps, or homes.

#### Teleport Requests
Players can send teleport requests to other players by typing `/tpr <player>`. The receiving player can then accept or deny the request by typing `/tpaccept` or `/tpdeny`.

## Developer Features
QUIPT was designed to give Paper plugin developers a simple-to-use and lightweight library focused on providing features that _should_ be easier to implement in the base API.

### EphemeralAdvancements
EphemeralAdvancements are QUIPT's way of sending custom toasts to the clients. Since paper doesn't have any method of sending custom toasts, we hook into the Bukkit advancement API and load the EphemeralAdvancements at the same time you create a new instance for it, then is deleted from the server once it's been sent to a player. This means once you send an EphemeralAdvancement to a player, you must kill the instance before you accidentally send it to another player.
