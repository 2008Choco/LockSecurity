# <center> Changelog </center> #
- - - - - - - - - - - - - - - - -

This is where you will find the official changelog of all releases of *LockSecurity&copy; 2015 *. This will be updated periodically in order to keep track of all changes related to this document. This is a large database of all changelogs that have ever been published to the official BukkitDev page.

* The official BukkitDev page can be found by [clicking here](http://dev.bukkit.org/bukkit-plugins/lock-security)
* The official SpigotMC page can be found by [clicking here](https://www.spigotmc.org/resources/locksecurity.12650/)

## Official Releases ##
- - - - - - - - - -

### Release 1.5.0 ###
* Started development on an external event API, fully documented the API, as well as uploaded the source code to LockSecurity and the future add-on in development to a private online repository. [Click here](http://www.bitbucket.org/2008Choco/lock-security)
* Created 4 new Player oriented events:
* |-> PlayerLockBlockEvent - Fired before a player successfully locks a block. Is cancellable
* |-> PlayerUnlockBlockEvent - Fired before a player successfully unlocks a block. Is cancellable
* |-> PlayerCombineKeyEvent - Fired when a player combines two keys in a crafting table. Is not cancellable
* |-> PlayerDuplicateKeyEvent - Fired when a player duplicates a key in a crafting table. Is not cancellable

### Release 1.4.2 ###
* Added an optional parameter to the /lockinspect command to wirelessly inspect a lock when a LockID is specified
* Fixed duplicate KeyID's when combining keys. There will now only be one of each KeyID
* Added a new list in the configuration to determine which blocks are lockable. It is limited however...
* |-> The only supported lockable blocks are listed in the config already. You can't add blocks other than those. You can remove blocks though

### Release 1.4.1 ###
* Fixed the 4 following grief-related bugs that can destroy blocks:
* |-> Fixed zombies being able to break locked wooden doors
* |-> Fixed wooden doors, chests, fence gates, and other wooden lockable blocks being able to burn
* |-> Fixed being able to remove a locked wooden door by breaking the block under it
* |-> Fixed being able to open a gate or door with redstone
* Added just an extra few lines of debug info to the onDisable method
* Fixed not being able to lock wooden doors (I used the wrong Material enum)
* Changed the message when breaking a locked block

### Release 1.4.0 ###
* Added Multi-KeyID support!!! Woohoo! 
* |-> Put 2 locked keys in a crafting table, and the keys will combine into one key with multiple id's
* Added duplication key support!!! :D (I'm on a roll, man)
* |-> Put 1 locked key, and one unsmithed key in a crafting table, and the key's will duplicate (so you can share with friends)
* Allowed lockinspect to work with a left click as well as a right click
* Created a private "displayLockInfo()" method to make lockinspect run a bit more efficiently
* Added support for trapdoors, dispensers, droppers, furnaces, hoppers, and fence gates (All of these are now lockable)
* Removed support for all iron-related blocks (iron trapdoors, iron doors, etc.). Those are lockable with redstone
* Changed recipe formations in the onEnable to be more efficient. Rather than creating 9 new objects, all recipes are now based on 1 object
* Added 2 aliases to /lockinspect. (/inspectlock, and /inspectlocks)

### Release 1.3.3 ###
* Created a createLockedKey() method to create a new ItemStack of a locked key (rather than converting an unsmithed key). This simplifies the /forgekey command
* Created two new methods in the LockStorageHandler class. getLocationFromLockID() and getLocationFromKeyID() to gather locations from ID's
* Removed the Commands.GiveKey.InvalidInteger & Commands.ForgeKey.InvalidInteger messages.yml path in replacement for Commands.General.InvalidInteger
* Added an optional int parameter to the /unlock command. If you specify a LockID (**NOT a KeyID**), it will unlock it
* Changed the structure of the locks.unlock permission nodes. It will now go as followed:
* |-> locks.unlock.* - The parent node, which includes all of the below
* |-> locks.unlock.self - Unlock blocks that you own using the /unlock command (without parameters)
* |-> locks.unlock.id - Allow use of the /unlock parameter to unlock a specified ID (Note, can unlock locks that are not yours)
* |-> locks.unlock.admin - Unlock blocks that you own or do not own using the /unlock command
* Added an unlocking sound when unlocking a chest
* Temporarily and minimally fixed interactions with Anti-Griefing plugins (Should prevent users from locking WorldGuarded / Admin claimed chests). Will add a WorldGuard flag later
* Fixed an ArrayIndexOutOfBoundsException when using /givekey on an offline player without specifying an amount of keys
* Fixed the check for the new KeyID variable in the onEnable method (**Please update if you were having strange KeyID issues before**)

### Release 1.3.2 ###
* Fixed the /forgekey messages being in DARK_AQUA rather than AQUA (Lol, sometimes I get finger happy and type a bit extra)
* Fixed /locklist & /locklistother not displaying it's first message
* Added support for the Messages.yml file and all messages in the plugin (You don't even know how long this took ._.)
* |-> Essentials colour codes are supported for all messages, but will default to gray
* Changed a few messages that needed to be changed a long time ago
* |-> Changed the message when unregistering a lock to display the LockID rather than the KeyID
* |-> Changed the message when locking a block to display the LockID, and the KeyID in brackets
* |-> Added consistancy to many of the messages (No permission messages, command messages, etc.)
* General code improvements, fixes, and optimizations (not even worth noting the TINY code changes)

### Release 1.3.1 ###
* Fixed /ignorelocks not being toggleable anymore
* Fixed the "This chest is locked by <player>" message always displaying chest rather than the block type
* Fixed a NumberFormatException in the getAllLocks() method (In result, messing up /locklist and /locklistother)
* Fixed the console message not displaying when a player changes their name, even if the config is set to true
* Changed the console message to display how many indexes were changed as well
* Removed the "createLockedKey()" method (Due to Deprecation after a few versions)

### Release 1.3.0 ###
* Added a new /unlock command! This can be used for both admins and players:
* |-> Players: (Permission: locks.unlock) Allow the ability to unlock blocks YOU OWN by right clicking on it (default: true)
* |-> Admins: (Permission: locks.adminunlock) Allow the ability to unlock any block by right click on it (default: op)
* Deprecated the "getBlockOwner()" method in replacement for the "getBlockOwnerUUID()" method (UUID support)
* Changed the way Block ID's are handled. It will instead return the Key ID (that is how block ID's should have been handled)
* |-> This will support dual-component blocks. When checking ID's in the locked.yml, use the KeyID, NOT THE STRING IDENTIFIER
* Added a new variable in the locked.yml, known as "NextKeyID". Not to be confused with "NextLockID". These are two variables that should NEVER be changed. This is to differenciate the unbinded String ID and the binded Key ID
* Created a private "addLockedYMLInformation()" method to store information in the locked.yml easier
* Fixed the "getLockID()" method to return proper mapped values
* Created a new LockStorageHandler method to handle the memory stored in RAM for locks
* Added dual-component block support (doors, double chests, double trapped chests, etc.)
* Fixed not being able to break locked block whilst in locked blocks mode (assuming the config enables it)
* Separated two HashMaps. One for KeyID information, and one for LockID information
* Modified the /lockinspect command to display both LockID and KeyID values of the block

### Release 1.2.2 ###
* Added a new "getAllLocks(player)" method to return a collection of ID's that the player owns
* Modified the /locklist command to run more efficiently and using the new "getAllLocks()" method
* Added a new login check. If the player has changed their name, it will refactor their name in the locked.yml to their new name. Will log in the console if a change has been made
* |-> NOTE: This is experimental and I have no way of guaranteeing this will work. I do not plan on changing my name, so please let me know if someone changes their name and there are bugs with the refactoring
* Added a new boolean configuration option to toggle whether the name change will log in the console or not "DisplayNameChangeNotice", default: true

### Release 1.2.1 ###
* Moved the "playerHasUnsmithedKey()" method into the Keys class, and made it a public method
* Fixed being able to place down Unsmithed Key's and Key's
* Added a new configuration option under the Griefing section, "OwnerRequiresKey", default: true. Determines whether or not the owner of the locked block requires the key or not
* Added a new /lockinspect command to gather information about a locked block (Permission: locks.lockinspect, default: op)
* |-> This is a toggleable mode. When enabled, right click the block you're curious about
* |-> Will gather the following information: LockID, Owner name (and UUID), Location

### Release 1.2.0 ###
* Fixed the /forgekey command not working (like... at all... oops .-.)
* Fixed the /forgekey command without any parameters displaying in DarkGray text rather than Gray
* Fixed the /forgekey command generating a NullPointerException and not giving a forged key
* Added a small smoke particle above a locked chest when attempting to open it without a key
* Added a new configuration section "Aesthetics". This will contain configuration options revolved around aesthetic additions
* Added a new configuration option under the Aesthetics category. "DisplayLockedSmokeParticle". Will toggle the smoke particle above the chest, default: true
* Removed a check if a block was neither Locked or Unlocked (which is impossible ._.)
* Added a new main administrative /locksecurity command with the following sub-commands. (Aliases: /ls)
* |-> /locksecurity reload - Reloads ALL the configurations (config.yml, locked.yml, and messages.yml) - Permission: locks.reload - Default: op
* |-> /locksecurity version - Gather version information / general information about the plugin

### Release 1.1.2 ###
* Added the ability for admins ignoring locks to destroy locked blocks as well
* Added a new configuration option under the Griefing category: IgnorelocksCanBreakLocks. Default: true
* |-> Toggles the ability to destroy locked blocks when ignoring locks
* Added a /forgekey <id> command to give a locked key with the specified ID (Permission: locks.forgekey. Default: op)

### Release 1.1.1 ###
* Changed the formatListing() method to return a direct variable rather than an indirect variable
* Fixed the format of /locklist not displaying whole numbers for coordinates
* Added a declaration of text at the beginning of /locklist determining the player you're looking up
* Fixed /locklistother without any parameters returning false
* Added a check when enabling the plugin. If the block id at the stored location doesn't match "BlockType", it will automatically remove the lock from the locked.yml. This saves unnecessary data usage on startup
* |-> If a block is removed, it will display information in the console. The Lock ID, the location, and the owner

### Release 1.1.0 ###
* Added the /locklist command to list every lock the sender owns. Permission: locks.locklist. Default: true
* Added the /locklistother command to list every lock the specified player owns. Permission: locks.locklistother. Default: op
* Added aliases the following aliases to the following commands:
* |-> **/givekey**. Aliases: /givekeys
* |-> **/ignorelocks**. Aliases: /ignorelock, /il
* |-> **/locklist**. Aliases: /locklists 
* |-> **/locklistother**. Aliases: locklistsother, /otherlocklists, /otherlocklist
* Added an extra parameter to the /givekey command. It now accepts amounts. /givekey \[player\] \[count\]
* Added an extra parameter to the createUnsmithedKey() method to allow an amount of Unsmithed Keys (future use, and compensation for the /givekey modifications)
* Changed the key recipe to be semi-shapless. As long as the iron bars and the wooden plank are on opposite ends of the crafting table, and the iron ingot is in the middle, the recipe will work (8 different ways to do it now)

### Release 1.0.2 ###
* Added JavaDoc comments to the LockedBlockAccessor.class file (A bit better documentation of the methods)
* Deprecated the createLockedKey(player) method (will be removed soon), and replaced it with a convertToLockedKey(key, id) method
* |-> Added a suplimentary convertToLockedKey(key, id's) method to set multiple key ID's in preparation for future plans
* Changed the getKeyID(player) method to getKeyIDs(key), and let it return List<Integer> rather than a String (preparation for future plans)
* Fixed the unlock registry saying "Chest" every time instead of the block material id
* Fixed the annoying "invisible key after lock" glitch. (Sorry about that. Lazy coding)
* Removed unnecessary declarations of the configuration file (might load up ever so slightly faster now?)
* Added more console messages on startup to inform the user what's going on whilst starting up
* Allowed the RAM storage of locks to continue if a NumberFormatException occurs (This basically means, if something can't be loaded in to RAM, the rest of the locks and the entire plugin will still load. Only the lock that could not be loaded will not be locked anymore)
* Fixed the error message above displaying multiple times when there were more than 1 errors
* Added an extra error message displaying how many locks could not be loaded

### Release 1.0.1 ###
* Added sounds to the following events
* |-> Locking a chest with an Unsmithed Key
* |-> Attempting to open a locked chest
* |-> Attempting to open a locked chest with the wrong key (failing to picking a lock)
* Fixed being able to explode a locked block with either TnT, Creepers, Ghasts, and EnderDragons (1.9 feature)
* Added a new Configuration Section. "Griefing". Will add griefing related configs
* Added a config option to enable or disable the ability to destroy locked items with explosions (Griefing.PreventLockedExplosions)
* Changed the message when breaking a locked chest to display who owns the chest
* Fixed not being able to shift click on a locked chest

### Release 1.0.0 ###
This is the initial release to BukkitDev and Spigot websites. Here is the list of currently integrated features, which can also be found on the main page:
* Crafting recipe for an *Unsmithed Key* (found on main page)
* /givekey command for administrators to give an *Unsmithed Key* (permission: locks.givekey)
* Right clicking on a chest or trapped chest with an *Unsmithed Key* (Tripwire Hook) will lock the object clicked
* |-> Required permission: locks.lock. This is default to true, and all players have this permission
* The *Unsmithed Key* will transform into a *Key*, with a unique ID on the lore
* When right clicking on the chest with that binded key, the chest will open
* Right clicking on the chest without that binded key, the chest will remain unopened
* Breaking a chest that is locked will unlock it
* |-> Only the Owner of the chest (player that locked it), may break the chest. Otherwise, the chest is not breakable
* Administrators are able to use the /ignorelocks command to override all locks (permission: locks.ignorelocks)

There are TONS of future plans for this project, and they will be added as soon as time is available. Please leave suggestions for ideas to add in to this plugin, as I am widely open to them. Submit a ticket if you have found a bug (also leave your suggestions in the ticket section for organization purposes). Don't leave a bug alone and hope that it'll be fixed, because 9 times out of 10, most people don't come and report bugs. No bug reports, no bug fixes :(.