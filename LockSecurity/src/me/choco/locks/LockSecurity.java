package me.choco.locks;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.choco.locks.api.utils.LSMode;
import me.choco.locks.commands.ForgeKey;
import me.choco.locks.commands.GiveKey;
import me.choco.locks.commands.IgnoreLocks;
import me.choco.locks.commands.LockInspect;
import me.choco.locks.commands.LockList;
import me.choco.locks.commands.MainCommand;
import me.choco.locks.commands.TransferLock;
import me.choco.locks.commands.Unlock;
import me.choco.locks.events.AttemptKeyPlace;
import me.choco.locks.events.CombineKeyID;
import me.choco.locks.events.DestroyLockedBlock;
import me.choco.locks.events.ExplodeLockedBlock;
import me.choco.locks.events.InteractWithBlock;
import me.choco.locks.events.LockedBlockGriefProtection;
import me.choco.locks.events.LoginNameCheck;
import me.choco.locks.utils.Keys;
import me.choco.locks.utils.LockStorageHandler;
import me.choco.locks.utils.general.ConfigAccessor;
import me.choco.locks.utils.general.Metrics;

public class LockSecurity extends JavaPlugin{
	
	LockStorageHandler ram = new LockStorageHandler(this);
	
	private static LockSecurity instance;
	public static final LockSecurity getPlugin(Plugin plugin) {
		System.out.println("Add-On Detected: " + plugin.getDescription().getName() + "v" + plugin.getDescription().getVersion()); 
		return instance;
	}
	@Deprecated
	public static final LockSecurity getPlugin(){
		System.out.println("Add-On Detected. No information provided");
		return instance;
	}
	
	public ConfigAccessor locked;
	public ConfigAccessor messages;
	Keys keysClass = new Keys(this);

	public HashMap<Location, Integer> lockedLockIDs = new HashMap<Location, Integer>();
	public HashMap<Location, Integer> lockedKeyIDs = new HashMap<Location, Integer>();
	public HashMap<String, String> transferTo = new HashMap<String, String>();

	ItemStack unsmithedKey = keysClass.createUnsmithedKey(1);
	public ShapedRecipe keyRecipe1 = new ShapedRecipe(unsmithedKey).shape("B  ", " I ", "  P").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD);
	public ShapedRecipe keyRecipe2 = new ShapedRecipe(unsmithedKey).shape(" B ", " I ", " P ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD);
	public ShapedRecipe keyRecipe3 = new ShapedRecipe(unsmithedKey).shape("  B", " I ", "P  ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD);
	public ShapedRecipe keyRecipe4 = new ShapedRecipe(unsmithedKey).shape("   ", "PIB", "   ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD);
	public ShapedRecipe keyRecipe5 = new ShapedRecipe(unsmithedKey).shape("  P", " I ", "B  ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD);
	public ShapedRecipe keyRecipe6 = new ShapedRecipe(unsmithedKey).shape(" P ", " I ", " B ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD);
	public ShapedRecipe keyRecipe7 = new ShapedRecipe(unsmithedKey).shape("P  ", " I ", "  B").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD);
	public ShapedRecipe keyRecipe8 = new ShapedRecipe(unsmithedKey).shape("   ", "BIP", "   ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD);
	public ShapelessRecipe combinationRecipe = new ShapelessRecipe(keysClass.createUnsmithedKey(1)).addIngredient(2, Material.TRIPWIRE_HOOK);
	
	@Override
	public void onEnable(){
		instance = this;
		
		//LockSecurity default config file
		getConfig().options().copyDefaults(true);
	    saveConfig();
	    //LockSecurity locked.yml file
		locked = new ConfigAccessor(this, "locked.yml");
		locked.loadConfig();
		//LockSecurity messages.yml file
		messages = new ConfigAccessor(this, "messages.yml");
		messages.loadConfig();
		messages.getConfig().options().copyDefaults(true);
		messages.saveConfig();
		
		//Enable Events
		Bukkit.getPluginManager().registerEvents(new InteractWithBlock(this), this);
		Bukkit.getPluginManager().registerEvents(new DestroyLockedBlock(this), this);
		Bukkit.getPluginManager().registerEvents(new ExplodeLockedBlock(this), this);
		Bukkit.getPluginManager().registerEvents(new AttemptKeyPlace(), this);
		Bukkit.getPluginManager().registerEvents(new LoginNameCheck(this), this);
		Bukkit.getPluginManager().registerEvents(new CombineKeyID(this), this);
		Bukkit.getPluginManager().registerEvents(new LockedBlockGriefProtection(this), this);
		
		//Enable commands
		this.getCommand("ignorelocks").setExecutor(new IgnoreLocks(this));
		this.getCommand("givekey").setExecutor(new GiveKey(this));
		this.getCommand("locklist").setExecutor(new LockList(this));
		this.getCommand("locklistother").setExecutor(new LockList(this));
		this.getCommand("forgekey").setExecutor(new ForgeKey(this));
		this.getCommand("locksecurity").setExecutor(new MainCommand(this)); this.getCommand("locksecurity").setTabCompleter(new MainCommand(this));
		this.getCommand("lockinspect").setExecutor(new LockInspect(this));
		this.getCommand("unlock").setExecutor(new Unlock(this));
		this.getCommand("transferlock").setExecutor(new TransferLock(this));
		
		//Generate key recipes
		Bukkit.getServer().addRecipe(keyRecipe1);
		Bukkit.getServer().addRecipe(keyRecipe2);
		Bukkit.getServer().addRecipe(keyRecipe3);
		Bukkit.getServer().addRecipe(keyRecipe4);
		Bukkit.getServer().addRecipe(keyRecipe5);
		Bukkit.getServer().addRecipe(keyRecipe6);
		Bukkit.getServer().addRecipe(keyRecipe7);
		Bukkit.getServer().addRecipe(keyRecipe8);
		Bukkit.getServer().addRecipe(combinationRecipe);
		
		//Load Metrics
		if (getConfig().getBoolean("MetricsEnabled") == true){
			this.getLogger().info("Enabling Plugin Metrics");
		    try{
		        Metrics metrics = new Metrics(this);
		        metrics.start();
		    }
		    catch (IOException e){
		    	e.printStackTrace();
		        getLogger().warning("Could not enable Plugin Metrics. If issues continue, please put in a ticket on the "
		        	+ "Lock Security development page");
		    }
		}
		
		//Load blocks into RAM (HashMap)
		int errors = 0;
		boolean checked = false;
		this.getLogger().info("Storing all locked blocks and ID's in server RAM");
		Set<String> keys = locked.getConfig().getKeys(false);
		keys.remove("NextLockID");
		keys.remove("NextKeyID");
		for (String key : keys){
			try{
				int lockID = Integer.parseInt(key);
				int keyID = locked.getConfig().getInt(key + ".KeyID");
				World world = Bukkit.getServer().getWorld(locked.getConfig().getString(key + ".Location.World"));
				double x = locked.getConfig().getDouble(key + ".Location.X");
				double y = locked.getConfig().getDouble(key + ".Location.Y");
				double z = locked.getConfig().getDouble(key + ".Location.Z");
				Location location = new Location(world, x, y, z);
				
				if (location.getBlock().getType().toString().equals(locked.getConfig().getString(key + ".BlockType"))){
					ram.addLockInformation(location, lockID, keyID);
				}else{
					this.getLogger().info("Lock ID " + key + " (Location: " + formatLocation(location) + ", Owner: " 
							+ locked.getConfig().getString(key + ".PlayerName") + ") removed due to not being identical as the save. Was it removed?");
					locked.getConfig().set(key, null);
					locked.saveConfig();
					locked.reloadConfig();
				}
				
				if ((locked.getConfig().getInt("NextKeyID") == 1) && !checked){
					Object[] ids = keys.toArray();
					setNextKeyID(locked.getConfig().getInt((String) ids[ids.length - 1] + ".KeyID") + 1);
					checked = true;
				}
			}catch(NumberFormatException e){
				if (errors == 0){
					e.printStackTrace();
					this.getLogger().warning("Something went wrong. Tell Choco about it immediately!");
					this.getLogger().warning("Go to: http://dev.bukkit.org/bukkit-plugins/lock-security/tickets" + 
							", and create a ticket including the error logged above");
					this.getLogger().warning("Be sure to also include a copy of your locked.yml file in the ticket for revision");
				}
				errors++;
				continue;
			}
		}
		if (errors > 0){
			this.getLogger().info("Stored as many locks as possible in server RAM. " + errors + " locks could not be loaded");
		}else{
			this.getLogger().info("Successfully stored all locks in server RAM. Plugin ready for use!");
		}
	}

	@Override
	public void onDisable(){
		this.getLogger().info("Removing stored data from the plugin, and saving it in locked.yml");
		ram.clearLocks();
		this.getLogger().info("Removing temporary information");
		LSMode.modeHandler.clear();
		transferTo.clear();
	}
	
	/** Check whether the specified block is a lockable or not
	 * @param block - The block to check
	 * @return boolean - Whether the block is lockable or not
	 */
	public boolean isLockable(Block block){
		/*   Chest, trapped chest, trapdoor, furnace, dispenser, dropper, hopper, (oak, acacia, birch, dark oak, jungle, spruce) doors, all fence gates   */
		Material type = block.getType();
		if (!(type.equals(Material.CHEST) || type.equals(Material.TRAPPED_CHEST) || type.equals(Material.TRAP_DOOR)
				|| type.equals(Material.FURNACE) || type.equals(Material.DISPENSER) || type.equals(Material.DROPPER)
				|| type.equals(Material.HOPPER) || type.equals(Material.WOODEN_DOOR) || type.equals(Material.ACACIA_DOOR)
				|| type.equals(Material.BIRCH_DOOR) || type.equals(Material.DARK_OAK_DOOR) || type.equals(Material.JUNGLE_DOOR)
				|| type.equals(Material.SPRUCE_DOOR) || type.toString().contains("FENCE_GATE"))){
			return false;
		}
		List<String> lockableBlocks = getConfig().getStringList("LockableBlocks");
		for (String listedType : lockableBlocks){
			if (type.toString().equals(listedType)){
				return true;
			}
		}
		return false;
	}
	
	public void sendPathMessage(CommandSender player, String message){
		player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Locked" + ChatColor.GOLD + "] " + ChatColor.GRAY +
			ChatColor.translateAlternateColorCodes('&', message));
	}
	
	private String formatLocation(Location location){
		return location.getWorld().getName() + " x:" + (int)location.getBlockX() + " y:" + (int)location.getBlockY() + " z:" + (int)location.getBlockY();
	}
	
	private void setNextKeyID(int id){
		locked.getConfig().set("NextKeyID", id);
		locked.saveConfig();
		locked.reloadConfig();
	}
}

/* TODO Upcoming Versions
 * Commands-Related:
 *     /adminlock - Convert the unbinded key in the player's hand to an AdminKey, which will lock any block under an administrative name
 *     Allow 3 parameters in the /transferlock command to transfer a specific ID wirelessly to a player (/transferlock <player> <lockid>)
 * 
 * General:
 *     Create a WorldGuard flag, "locks", to determine whether players can lock chests or not
 *     Add the location of the chest on the keys
 *         -> Make it a configuration option to display or not?
 *     Add faction support. Lock a block under your faction name rather than your own name
 *     Add a new configuration option: OnlyDuplicateOwnKeys: true/false
 *         -> You MUST be the owner of the keys to merge or duplicate them
 *     Add IRON_DOOR / IRON_TRAPDOOR support. Right clicking on a locked IRON_DOOR / IRON_TRAPDOOR will open it like a wooden door
 *     Create a list for "MaximumBlocks" to allow for exceptions to the list
 *     Automatically set configuration lists for each and every world on the server onEnable
 * -------------------------------------------------------------------------------------------------------------------------
 * TODO Next Version:
 * /locknotify <on|off> - Toggle the visibility of administrative lock displays (Displays all lock information to administrators)
 */

/* Version 1.6.0: 99.4KiB */
/* Version 1.6.1: 100.2KiB */