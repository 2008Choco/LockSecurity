![Information](http://i.imgur.com/LVYLTWF.png)
LockSecurity! The easiest to use container protection plugin on Bukkit! This plugin allows the ability for you to lock your containers with a key, and only be able to open the container if you have the key in your hand! There is no way that the chest can be accessed by another user unless they have a key with the ID binded to the block you have locked. IMPOSSIBLE to access any functions of the locked block

The plugin is simple to use and get started. First, all you have to do, is craft yourself a rusty old Unsmithed Key. If you right click on a lockable block, that chest and the Unsmithed Key you right clicked with, will be assigned a unique ID in incremental order. All information about the locked block is neatly stored in an SQLite lckinfo.db file inside the LockSecurity folder in your server files. No one will be able to access the contents of that chest... unless... they attempt to open the chest with the key that was binded to it. Simple as that! I know, you're probably thinking, "But why not just try and break the chest and get the contents inside?". That's been thought of. If you are not the owner of the chest (the player who locked it in the first place), you will not be able to break it. What about explosions? That's okay... all locked blocks are explosion resistant! :D No more need to hide your chests! What about doors and redstone? That's though of too! Redstone doesn't open or close locked blocks!

This plugin is highly configurable, including per/world block lock limits, recipe result yieldings, and even a list of blocks that can be locked. As of version 1.5.0, there is an external API which has been fully documented on a BitBucket repository where the source code is privately backed up. If you're a developer, and you would like to create an add-on for LockSecurity, please scroll down to the "LockSecurity API / Add-Ons" section, and hack away!

![Commands](http://i.imgur.com/7dgYga0.png)
There are a few commands to help administrators manage this plugin. There are of course a couple of missing features from these commands, and there will be more added in the future. You can view all command references and their related permission nodes by -= Clicking Here =-

![Permissions](http://i.imgur.com/HNa7Nsv.png)
There are a few permission nodes that should be taken notice of. Although they are very minor, and already have reasonable default values for servers that do not have permission manager plugins, please read the permission node guide page for reference to what they all do. You can view all permission node references by -= Clicking Here =-

![Crafting Recipes](http://i.imgur.com/5qkaxOP.png)
The following recipe will get you an Unsmithed Key, which can be used to lock a lockable block

![Recipe](http://i.imgur.com/vd1Jsw0.png)

![Configuration](http://i.imgur.com/uYL6JVC.png)
As of version 1.0.1, I am commencing the construction of configuration options. Please view the following page on how to use any of the configuration options if they are not already clear, by -= Clicking Here =-

![LockSecurity API](http://i.imgur.com/6YmdkQF.png)
As of version 1.5.0 of LockSecurity, there is a very extensive API that has full documentation. If you are a developer interested in creating add-ons for LockSecurity, please, I highly encourage you to do so! I would love to see some neat little add-ons for this plugin, and perhaps even use them! If you create a LockSecurity add-on, do not hesitate to inform me over PM on BukkitDev, Twitter (found below), or in the comments of LockSecurity. Doesn't matter how detailed it is, as long as it's an add-on, I would love to hear about it, and I will list it here :D

I have created an add-on for LockSecurity to show the true power of the new API. It is called LS-ChestCollector! Using locked chests, you can create wireless collection systems, to collect the items you specify in the command when you pick them up! You can check this add-on out by =- Clicking Here =-

About the API. If you would like to know how to actually code an extension to the LockSecurity plugin, go ahead and head over to the BitBucket Wiki page which can be found by -= Clicking Here =-

![Support a Dev](http://i.imgur.com/LUrfSli.png)
I work really hard on my projects to try and produce the best updates as I possibly can. If you would like to donate and help support me, that would be very much appreciated. My plugins are not pay-to-play, and donating is simply optional. If you can't support me through monetary means, please feel free to comment and provide me with suggestions instead. Anything helps.

![[donate](http://i.imgur.com/3qYi9hJ.png)](https://www.paypal.com/cgi-bin/webscr?return=http%3A%2F%2Fdev.bukkit.org%2Fbukkit-plugins%2Falchemical-arrows%2F&cn=Add+special+instructions+to+the+addon+author%28s%29&business=hawkeboyz%40hotmail.com&bn=PP-DonationsBF%3Abtn_donateCC_LG.gif%3ANonHosted&cancel_return=http%3A%2F%2Fdev.bukkit.org%2Fbukkit-plugins%2Falchemical-arrows%2F&lc=US&item_name=Lock+Security+%28from+Bukkit.org%29&cmd=_donations&rm=1&no_shipping=1&currency_code=USD)