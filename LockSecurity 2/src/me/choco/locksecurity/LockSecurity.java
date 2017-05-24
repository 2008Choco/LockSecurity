package me.choco.locksecurity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import me.choco.locksecurity.utils.ConfigOption;
import me.choco.locksecurity.utils.LSPlayer;
import me.choco.locksecurity.utils.commands.ForgeKeyCmd;
import me.choco.locksecurity.utils.commands.GiveKeyCmd;
import me.choco.locksecurity.utils.commands.LockSecurityCmd;
import me.choco.locksecurity.utils.general.ItemBuilder;
import me.choco.locksecurity.utils.general.UpdateChecker;
import me.choco.locksecurity.utils.json.JSONUtils;
import me.choco.locksecurity.utils.localization.Locale;

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
	private Locale locale;
	
	@Override
	public void onEnable() {
		// Default values
		instance = this;
		this.playerdataDir = new File(this.getDataFolder().getAbsolutePath() + File.separator + "playerdata");
		this.infoFile = new File(this.getDataFolder().getAbsolutePath() + File.separator + "plugin.info");
		this.saveDefaultConfig();
		ConfigOption.loadConfigurationValues(this);
		
		// Locales
		Locale.saveDefaultLocale("en_CA");
		Locale.saveDefaultLocale("fr_CA");
		locale = Locale.getLocale(this.getConfig().getString("Locale", "en_CA"));
		
		// Transfer old data if necessary
		if (!playerdataDir.exists()){
			if (new File(this.getDataFolder().getAbsolutePath() + File.separator + "lockinfo.db").exists())
				TransferUtils.fromDatabase(this);
			else if (new File(this.getDataFolder().getAbsolutePath() + File.separator + "locked.yml").exists())
				TransferUtils.fromFile(this);
			
			if (this.playerdataDir.mkdirs()) this.getLogger().info(locale.getMessage("enable.generate.playerdir"));
		}
		
		// Save data file(s)
		if (!this.infoFile.exists()){
			try{
				this.infoFile.createNewFile();
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(infoFile));
				writer.write("nextLockID=1\nnextKeyID=1");
				writer.close();
			}catch(IOException e){ e.printStackTrace(); }
			
			this.getLogger().info(locale.getMessage("enable.generate.infofile"));
		}
		
		// Instantiate necessary variables
		this.getLogger().info(locale.getMessage("enable.registration.variables"));
		this.playerRegistry = new PlayerRegistry(this);
		this.lockedBlockManager = new LockedBlockManager(this);
		
		// Register events
		this.getLogger().info(locale.getMessage("enable.registration.events"));
		
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
		this.getLogger().info(locale.getMessage("enable.registration.commands"));
		this.getCommand("locksecurity").setExecutor(new LockSecurityCmd(this));
		this.getCommand("forgekey").setExecutor(new ForgeKeyCmd(this));
		this.getCommand("givekey").setExecutor(new GiveKeyCmd(this));
		
		// Register crafting recipes
		this.getLogger().info(locale.getMessage("enable.registration.recipes"));
		ItemStack unsmithedKey = KeyFactory.getUnsmithedkey();
		Bukkit.getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey1"), unsmithedKey).shape("B  ", " I ", "  P").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey2"), unsmithedKey).shape(" B ", " I ", " P ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey3"), unsmithedKey).shape("  B", " I ", "P  ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey4"), unsmithedKey).shape("   ", "BIP", "   ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey5"), unsmithedKey).shape("   ", "PIB", "   ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey6"), unsmithedKey).shape("  P", " I ", "B  ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey7"), unsmithedKey).shape(" P ", " I ", " B ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey8"), unsmithedKey).shape("P  ", " I ", "  B").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.getServer().addRecipe(new ShapelessRecipe(new NamespacedKey(this, "keyclear"), new ItemBuilder(Material.BEDROCK).setName("SINGLE").build()).addIngredient(1, Material.TRIPWIRE_HOOK));
		Bukkit.getServer().addRecipe(new ShapelessRecipe(new NamespacedKey(this, "keycombine"), new ItemBuilder(Material.BEDROCK).setName("DUAL").build()).addIngredient(2, Material.TRIPWIRE_HOOK));
		
		// Load all registered data
		this.getLogger().info(locale.getMessage("enable.load.jsondata"));
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
			System.out.println(locale.getMessage("enable.load.update"));
		}
	}
	
	@Override
	public void onDisable() {
		if (autosave != null){
			this.getLogger().info(locale.getMessage("disable.savedata"));
			this.autosave.run();
			this.autosave.cancel();
		}
		
		if (playerRegistry != null){
			this.getLogger().info(locale.getMessage("disable.cleardata"));
			for (LSPlayer player : playerRegistry.getPlayers().values()){
				player.getOwnedBlocks().clear();
				player.getActiveModes().clear();
			}
			
			this.playerRegistry.getPlayers().clear();
		}
		
		if (lockedBlockManager != null)
			this.lockedBlockManager.getLockedBlocks().clear();
		
		Locale.clearLocaleData();
	}
	
	/** 
	 * Get an instance of the LockSecurity class. This is for API usage
	 * 
	 * @return an instance of LockSecurity
	 */
	public static LockSecurity getPlugin(){
		return instance;
	}
	
	/** 
	 * Get the main instance of the {@link PlayerRegistry} class
	 * 
	 * @return the PlayerRegistry class
	 */
	public PlayerRegistry getPlayerRegistry() {
		return playerRegistry;
	}
	
	/** 
	 * Get the main instance of the {@link LockedBlockManager} class
	 * 
	 * @return the LockedBlockManager class
	 */
	public LockedBlockManager getLockedBlockManager() {
		return lockedBlockManager;
	}
	
	/** 
	 * Send a message to the specified player with the [LockSecurity] prefix
	 * 
	 * @param sender - The user to send the message to
	 * @param message - The message to send
	 */
	public void sendMessage(CommandSender sender, String message){
		sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "LockSecurity" + ChatColor.GOLD + "] " + ChatColor.GRAY + message);
	}
	
	/** 
	 * Get the current plugin localization to receive messages
	 * 
	 * @return current locale
	 */
	public Locale getLocale() {
		return locale;
	}
}