package me.choco.locksecurity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import me.choco.locksecurity.api.KeyFactory;
import me.choco.locksecurity.events.BlockBreakListener;
import me.choco.locksecurity.events.BlockClickListener;
import me.choco.locksecurity.events.KeyCraftingListener;
import me.choco.locksecurity.events.data.WorldDataLoader;
import me.choco.locksecurity.events.data.WorldDataUnloader;
import me.choco.locksecurity.events.protection.DoubleChestProtectionListener;
import me.choco.locksecurity.events.protection.ExplosionProtectionListener;
import me.choco.locksecurity.events.protection.GriefProtectionListener;
import me.choco.locksecurity.events.protection.KeyPlaceProtectionListener;
import me.choco.locksecurity.registration.LockedBlockManager;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.AutoSaveLoop;
import me.choco.locksecurity.utils.LSConfig;
import me.choco.locksecurity.utils.LSPlayer;
import me.choco.locksecurity.utils.commands.ForgeKeyCmd;
import me.choco.locksecurity.utils.commands.GiveKeyCmd;
import me.choco.locksecurity.utils.commands.LockSecurityCmd;
import me.choco.locksecurity.utils.general.ConfigAccessor;
import me.choco.locksecurity.utils.general.ItemBuilder;
import me.choco.locksecurity.utils.general.UpdateChecker;
import me.choco.locksecurity.utils.json.JSONUtils;

public class LockSecurity extends JavaPlugin {
	
	/* LEFT TODO:
	 *    - TransferUtils method bodies
	 *       * Write transfered LockID and KeyID data to the info file
	 *    - Permission nodes
	 *    - Utilize configuration options
	 *    - Send all messages
	 *    
	 *    - MAKE SURE I'VE DOCUMENTED EVERYTHING I'VE WRITTEN
	 *    
	 * LEFT TO TEST:
	 *   - Commands:
	 *      * /lockinspect <lockID>
	 *      * /locklist
	 *      * /transferlock
	 *    - All API event calls (This should be testable through ChestCollectors)
	 *    - All LSMode features
	 */
	
	private static LockSecurity instance;
	
	private AutoSaveLoop autosave;
	
	private PlayerRegistry playerRegistry;
	private LockedBlockManager lockedBlockManager;
	
	public File playerdataDir, infoFile;
	public ConfigAccessor messages;
	
	@Override
	public void onEnable() {
		if (getJavaVersion() < 1.8){
			this.getLogger().severe("\n" + 
					StringUtils.repeat("=", 60) + "\n" +
					"            Java 8 is REQUIRED to run LockSecurity 2 \n" +
					"    Please speak to your server hosting company to get Java updated \n" + 
					"                   Disabling LockSecurity 2...       \n" + 
					StringUtils.repeat("=", 60));
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		String testJSON = "";
		
		instance = this;
		this.playerdataDir = new File(this.getDataFolder().getAbsolutePath() + File.separator + "playerdata");
		this.infoFile = new File(this.getDataFolder().getAbsolutePath() + File.separator + "plugin.info");
		this.messages = new ConfigAccessor(this, "messages.yml");
		
		// Transfer old data if necessary
		if (!playerdataDir.exists()){
			if (new File(this.getDataFolder().getAbsolutePath() + File.separator + "lockinfo.db").exists())
				TransferUtils.fromDatabase();
			else if (new File(this.getDataFolder().getAbsolutePath() + File.separator + "locked.yml").exists())
				TransferUtils.fromFile();
			
			if (this.playerdataDir.mkdirs()) this.getLogger().info("Successfully created playerdata directory");
		}
		
		// Save configuration file(s)
		saveDefaultConfig();
		LSConfig.loadValues();
		this.messages.loadConfig();
		this.messages.saveDefaultConfig();
		if (!this.infoFile.exists()){
			try{
				this.infoFile.createNewFile();
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(infoFile));
				writer.write("nextLockID=1\nnextKeyID=1");
				writer.close();
			}catch(IOException e){ e.printStackTrace(); }
			
			this.getLogger().info("Successfully created new plugin information file");
		}
		
		// Instanstiate necessary variables
		this.getLogger().info("Instantiating necessary variables");
		this.playerRegistry = new PlayerRegistry(this);
		this.lockedBlockManager = new LockedBlockManager(this);
		
		// Register events
		this.getLogger().info("Registering events");
		
		    /* General/Lock-Based listeners */
		Bukkit.getPluginManager().registerEvents(new BlockClickListener(this), this);
		Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
		Bukkit.getPluginManager().registerEvents(new KeyCraftingListener(), this);
		
		    /* Protection listeners */
		Bukkit.getPluginManager().registerEvents(new KeyPlaceProtectionListener(), this);
		Bukkit.getPluginManager().registerEvents(new GriefProtectionListener(this), this);
		Bukkit.getPluginManager().registerEvents(new ExplosionProtectionListener(this), this);
		Bukkit.getPluginManager().registerEvents(new DoubleChestProtectionListener(this), this);
		
		    /* Data listeners */
		Bukkit.getPluginManager().registerEvents(new WorldDataLoader(this), this);
		Bukkit.getPluginManager().registerEvents(new WorldDataUnloader(this), this);
		
		
		// Register commands
		this.getLogger().info("Registering commands");
		this.getCommand("locksecurity").setExecutor(new LockSecurityCmd(this));
		this.getCommand("forgekey").setExecutor(new ForgeKeyCmd(this));
		this.getCommand("givekey").setExecutor(new GiveKeyCmd(this));
		
		// Register crafting recipes
		this.getLogger().info("Registering crafting recipes");
		ItemStack unsmithedKey = KeyFactory.getUnsmithedkey();
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("B  ", " I ", "  P").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape(" B ", " I ", " P ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("  B", " I ", "P  ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("   ", "BIP", "   ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("   ", "PIB", "   ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("  P", " I ", "B  ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape(" P ", " I ", " B ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(unsmithedKey).shape("P  ", " I ", "  B").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapelessRecipe(new ItemBuilder(Material.BEDROCK).setName("SINGLE").build()).addIngredient(1, Material.TRIPWIRE_HOOK));
		Bukkit.getServer().addRecipe(new ShapelessRecipe(new ItemBuilder(Material.BEDROCK).setName("DUAL").build()).addIngredient(2, Material.TRIPWIRE_HOOK));
		
		// Load all registered data
		this.getLogger().info("Loading player JSON data from file");
		for (File file : playerdataDir.listFiles()){
			OfflinePlayer rawPlayer = Bukkit.getOfflinePlayer(UUID.fromString(file.getName().replace(".json", "")));
			
			LSPlayer player = new LSPlayer(rawPlayer);
			playerRegistry.registerPlayer(player);
			
			player.read(JSONUtils.readJSON(file));
		}
		
		// Load data for worlds that are already loaded (In case of a reload)
		if (Bukkit.getOnlinePlayers().size() != 0){
			for (World world : Bukkit.getWorlds()){
				if (world.getPlayers().size() == 0) continue;
				this.lockedBlockManager.loadDataForWorld(world);
			}
		}
		
		this.autosave = new AutoSaveLoop(this);
		this.autosave.runTaskTimerAsynchronously(this, 0L, 6000L);
		
		UpdateChecker checker = new UpdateChecker(this, 12650);
		if (checker.queryUpdateCheck() && checker.requiresUpdate()){
			System.out.println("AN UPDATE IS REQUIRED");
		}
	}
	
	@Override
	public void onDisable() {
		if (autosave != null){
			this.getLogger().info("Forcing a registry save");
			this.autosave.run();
			this.autosave.cancel();
		}
		
		if (playerRegistry != null){
			this.getLogger().info("Clearing localized data");
			for (LSPlayer player : playerRegistry.getPlayers().values()){
				player.getOwnedBlocks().clear();
				player.getActiveModes().clear();
			}
			
			this.playerRegistry.getPlayers().clear();
		}
		
		if (lockedBlockManager != null)
			this.lockedBlockManager.getLockedBlocks().clear();
	}
	
	/** Get an instance of the LockSecurity class. This is for API usage
	 * @return an instance of LockSecurity
	 */
	public static LockSecurity getPlugin(){
		return instance;
	}
	
	/** Get the main instance of the {@link PlayerRegistry} class
	 * @return the PlayerRegistry class
	 */
	public PlayerRegistry getPlayerRegistry() {
		return playerRegistry;
	}
	
	/** Get the main instance of the {@link LockedBlockManager} class
	 * @return the LockedBlockManager class
	 */
	public LockedBlockManager getLockedBlockManager() {
		return lockedBlockManager;
	}
	
	/** Send a message to the specified player with the [LockSecurity] prefix
	 * @param sender - The user to send the message to
	 * @param message - The message to send
	 */
	public void sendMessage(CommandSender sender, String message){
		sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "LockSecurity" + ChatColor.GOLD + "] " + ChatColor.GRAY + message);
	}
	
	private static double getJavaVersion(){
	    String version = System.getProperty("java.version");
	    int pos = version.indexOf('.');
	    pos = version.indexOf('.', pos+1);
	    return Double.parseDouble (version.substring (0, pos));
	}
}