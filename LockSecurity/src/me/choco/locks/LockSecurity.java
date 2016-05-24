package me.choco.locks;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.choco.locks.api.LockedBlock;
import me.choco.locks.api.utils.LSMode;
import me.choco.locks.commands.ForgeKey;
import me.choco.locks.commands.GiveKey;
import me.choco.locks.commands.IgnoreLocks;
import me.choco.locks.commands.LockInspect;
import me.choco.locks.commands.LockList;
import me.choco.locks.commands.LockNotify;
import me.choco.locks.commands.MainCommand;
import me.choco.locks.commands.TransferLock;
import me.choco.locks.commands.Unlock;
import me.choco.locks.events.AttemptKeyPlace;
import me.choco.locks.events.CombineKeyID;
import me.choco.locks.events.DestroyLockedBlock;
import me.choco.locks.events.ExplodeLockedBlock;
import me.choco.locks.events.InteractWithBlock;
import me.choco.locks.events.JoinAndQuit;
import me.choco.locks.events.LockedBlockGriefProtection;
import me.choco.locks.utils.Keys;
import me.choco.locks.utils.LocalizedDataHandler;
import me.choco.locks.utils.general.ConfigAccessor;
import me.choco.locks.utils.general.Metrics;
import me.choco.locks.utils.general.SQLite;
import me.choco.locks.utils.general.loops.DatabaseSaveLoop;
import net.milkbowl.vault.economy.Economy;

public class LockSecurity extends JavaPlugin{
	
	private static LockSecurity instance;
	public static LockSecurity getPlugin(Plugin plugin) { return instance; }
	public static LockSecurity getPlugin(){ return instance; }
	
	public ConfigAccessor locked;
	public ConfigAccessor messages;
	private LocalizedDataHandler data;
	private final SQLite database = new SQLite();

	public HashMap<String, String> transferTo = new HashMap<String, String>();
	public Economy economy = null;
	
	private HashMap<Player, List<LSMode>> modes = new HashMap<>();

	@Override
	public void onEnable(){
		instance = this;
		this.data = new LocalizedDataHandler(this);
		
		//LockSecurity default config file
		getConfig().options().copyDefaults(true);
	    saveConfig();
	    
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
		Bukkit.getPluginManager().registerEvents(new JoinAndQuit(this), this);
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
		this.getCommand("locknotify").setExecutor(new LockNotify(this));
		
		//Generate key recipes
		ItemStack unsmithedKey = new Keys(this).createUnsmithedKey(getConfig().getInt("RecipeYields"));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("B  ", " I ", "  P").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape(" B ", " I ", " P ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("  B", " I ", "P  ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("   ", "BIP", "   ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("   ", "PIB", "   ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("  P", " I ", "B  ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape(" P ", " I ", " B ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("P  ", " I ", "  B").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapelessRecipe(new ItemStack(Material.BEDROCK)).addIngredient(2, Material.TRIPWIRE_HOOK));
		
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
		
		if (setupEconomy())
			this.getLogger().info("Vault hooked successfully!");
		
		Connection connection = null;
		Statement statement = null;
		try{
			Class.forName("org.sqlite.JDBC");
			connection = getLSDatabase().openConnection();
			statement = getLSDatabase().createStatement(connection);
		}catch(Exception e){e.printStackTrace();}
		this.getLogger().info("Opened database successfully");
		
		//YML to database transfer
		try{
			if (!connection.getMetaData().getTables(null, null, "LockedBlocks", null).next()){
				this.getLogger().info("Creating database...");
				statement.execute("create table if not exists LockedBlocks "
					+ "(LockID integer primary key autoincrement, KeyID integer, OwnerUUID char(36), OwnerName char(20), BlockType char(30),"
					+ " LocationX integer, LocationY integer, LocationZ integer, LocationWorld char(50))");
				
				this.getLogger().info("-------------------------------------------------------------------------------------");
				this.getLogger().info("Converting all locked.yml information into SQLite Database");
				this.getLogger().info("");
				this.getLogger().info("Depending on the amount of locked blocks in the YAML file, this may take some time");
				this.getLogger().info("If any issues are encountered during this process, please copy the log, and create a ticket" +
						" on the main development page at http://dev.bukkit.org/bukkit-plugins/lock-security/tickets");
				this.getLogger().info("");
				this.getLogger().info("Please be patient while the data is converting into the database");
				this.getLogger().info("-------------------------------------------------------------------------------------");
				this.getLogger().info("Data conversion logs:");
				
				//THIS IS THE ONLY LOCATION WHERE LOCKED.YML SHOULD BE REFERENCED!!!!!
				locked = new ConfigAccessor(this, "locked.yml");
				Set<String> keys = locked.getConfig().getKeys(false);
				keys.remove("NextLockID");
				keys.remove("NextKeyID");
				for (String key : keys){
					try{
						int lockID = Integer.parseInt(key);
						int keyID = locked.getConfig().getInt(key + ".KeyID");
						String ownerUUID = locked.getConfig().getString(key + ".OwnerUUID");
						String ownerName = locked.getConfig().getString(key + ".PlayerName");
						String blockType = locked.getConfig().getString(key + ".BlockType");

						World world = Bukkit.getServer().getWorld(locked.getConfig().getString(key + ".Location.World"));
						double x = locked.getConfig().getDouble(key + ".Location.X");
						double y = locked.getConfig().getDouble(key + ".Location.Y");
						double z = locked.getConfig().getDouble(key + ".Location.Z");
						Location location = new Location(world, x, y, z);
						
						insertDatabaseInfo(lockID, keyID, ownerUUID, ownerName, blockType, location);
						this.getLogger().info("Successfully Transfered LockID: " + lockID + ", Location: " + (int)x + ", " + (int)y + ", " + (int)z);
					} catch (NumberFormatException e){}
				}
				this.getLogger().info("-------------------------------------------------------------------------------------");
				this.getLogger().info("All data has succesfully been transfered into the new SQLite database!");
				this.getLogger().info("At this point, you may now do one of the following with your locked.yml:");
				this.getLogger().info("   1. Delete the locked.yml completely");
				this.getLogger().info("   2. Remove the locked.yml from the files, but keep a backup just in case (RECOMMENDED)");
				this.getLogger().info("-------------------------------------------------------------------------------------");
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getLogger().warning("Something went wrong while transfering data into the database");
			this.getLogger().warning("Please leave a ticket on the LockSecurity plugin development page! http://dev.bukkit.org/bukkit-plugins/lock-security/tickets");
			this.getLogger().warning("BE SURE TO INCLUDE A COPY OF YOUR locked.yml, YOUR lockinfo.db, AND YOUR ERROR LOG");
		}
		getLSDatabase().closeStatement(statement); getLSDatabase().closeConnection(connection);
		
		//Database to LocalizedDataHandler
		new BukkitRunnable(){
			@Override
			public void run(){
				try{
					Connection connection = getLSDatabase().openConnection();
					Statement statement = getLSDatabase().createStatement(connection);
					ResultSet set = getLSDatabase().queryDatabase(statement, "SELECT * FROM LockedBlocks");
					while (set.next()){
						getLocalizedData().registerLockedBlock(new LockedBlock(
								Bukkit.getWorld(set.getString("LocationWorld")).getBlockAt(
										set.getInt("LocationX"),
										set.getInt("LocationY"), 
										set.getInt("LocationZ")),
								Bukkit.getOfflinePlayer(UUID.fromString(set.getString("OwnerUUID"))),
								set.getInt("LockID"),
								set.getInt("KeyID")));
					}
					getLSDatabase().closeResultSet(set); getLSDatabase().closeStatement(statement); getLSDatabase().closeConnection(connection);
				}catch(SQLException e){ e.printStackTrace(); }
				getLogger().info("Locked data successfully transfered to localized data handler");
			}
		}.runTask(this);
		
		long delay = (this.getConfig().getLong("DatabaseSaveIntreval") * 60) * 20;
		new DatabaseSaveLoop(this).runTaskTimer(this, delay, delay);
	}

	@Override
	public void onDisable(){
		this.getLogger().info("Removing temporary information");
		transferTo.clear();
		getLocalizedData().saveLocalizedDataToDatabase(true);
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
	
	/** Send a message to a player with Essentials colour code translations and the [LockSecurity] in front of it
	 * @param player - The player the message
	 * @param message - The message to send to the player
	 */
	public void sendPathMessage(CommandSender player, String message){
		player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Locked" + ChatColor.GOLD + "] " + ChatColor.GRAY +
			ChatColor.translateAlternateColorCodes('&', message));
	}
	
	/** Check if the player is in a specific mode
	 * @param player - The player to check
	 * @param mode - The mode to check
	 * @return Whether the player is in the specified mode or not
	 */
	public boolean isInMode(Player player, LSMode mode){
		if (!modes.containsKey(player)) modes.put(player, new ArrayList<LSMode>());
		return modes.get(player).contains(mode);
	}
	
	/** Get all players currently in a specified mode
	 * @param mode - The mode to reference
	 * @return A list of players in that mode
	 */
	public List<Player> getPlayersInMode(LSMode mode){
		List<Player> players = new ArrayList<>();
		for (Player player : modes.keySet())
			if (isInMode(player, mode)) players.addAll(players);
		return players;
	}
	
	/** Get all modes that the specified player is currently in
	 * @param player - The player to reference
	 * @return A list of all modes the player is in
	 */
	public List<LSMode> getModes(Player player){
		if (!modes.containsKey(player)) modes.put(player, new ArrayList<LSMode>());
		return modes.get(player);
	}
	
	/** Add a mode to a player
	 * @param player - The player to add the mode to
	 * @param mode - The mode to add
	 */
	public void addMode(Player player, LSMode mode){
		if (!modes.containsKey(player)) modes.put(player, new ArrayList<LSMode>());
		if (!isInMode(player, mode)) modes.get(player).add(mode);
	}
	
	/** Remove a mode from a player
	 * @param player - The player to remove the mode from
	 * @param mode - The mode to remove
	 */
	public void removeMode(Player player, LSMode mode){
		if (!modes.containsKey(player)) modes.put(player, new ArrayList<LSMode>());
		if (isInMode(player, mode)) modes.get(player).remove(mode);
	}
	
	/** Get the database storing information about all locks
	 * @return SQLite - The database information class
	 */
	public SQLite getLSDatabase(){
		return database;
	}
	
	/** Get localized data from LockSecurity, containing locked block information
	 * @return LocalizedDataHandler - Information about all locked blocks on the server
	 */
	public LocalizedDataHandler getLocalizedData(){
		return data;
	}

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        economy = rsp.getProvider();
        return economy != null;
    }
    
	private void insertDatabaseInfo(int lockID, int keyID, String ownerUUID, String ownerName, String blockType, Location location){
		Connection connection = getLSDatabase().openConnection();
		Statement statement = getLSDatabase().createStatement(connection);
		getLSDatabase().executeStatement(statement, "insert into LockedBlocks values(" + lockID + ", " + keyID + ", '" + ownerUUID + "', '" + ownerName + "', '" 
				+ blockType + "', " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ", '" + location.getWorld().getName() + "')");
		getLSDatabase().closeStatement(statement); getLSDatabase().closeConnection(connection);
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
 *     Add an override MaximumLocks for ALL worlds
 * -------------------------------------------------------------------------------------------------------------------------
 * TODO Next Version:
 * 
 */