# <center> Changelog </center> #
- - - - - - - - - - - - - - - - -

This is where you will find the official changelog of all releases of *LS-ChestCollector&copy; 2015 *. This will be updated periodically in order to keep track of all changes related to this document. This is a large database of all changelogs that have ever been published to the official BukkitDev page.

* The official BukkitDev page can be found by [clicking here](BUKKIT_LINK)
* The official SpigotMC page can be found by [clicking here](SPIGOT_LINK)

## Official Releases ##
- - - - - - - - - -

### Release 1.0.0 ###
* Added a /collects <ItemStack,ItemStack,ItemStack...> command to set the player in collector creation mode
* |-> Requires the "collections.command" permission node. Default: true (All players)
* |-> You can have as many item stacks as your chat lets you. Example: "/collects IRON\_INGOT,GOLD\_INGOT,DIAMOND,REDSTONE,COAL"
* When in the collector creation mode, right clicking on LockSecurity locked chest will convert it into a Collector Chest
* Collector chests will store the command's ItemStack parameters in the collectors.yml file
* When an item of the specified type is picked up, it will automatically suck it from your inventory, and go into the collector chest
* |-> If you have multiple collector chests with the same item ID, it will go into the first one with an available slot
* |-> If all of your chests are full, you will simply pick up the item. Not all that bad :P
* Breaking or unlocking a locked collector chest will remove the collector chest information from the collectors.yml
* Metrics are available in this plugin as well to gather general anonymous information about the plugin/server