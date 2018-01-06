package me.choco.locksecurity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FileUtils;
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.choco.locksecurity.api.ILockSecurityPlayer;
import me.choco.locksecurity.api.ILockedBlockManager;
import me.choco.locksecurity.api.IPlayerRegistry;
import me.choco.locksecurity.api.utils.KeyFactory;
import me.choco.locksecurity.commands.ForgeKeyCmd;
import me.choco.locksecurity.commands.GiveKeyCmd;
import me.choco.locksecurity.commands.IgnoreLocksCmd;
import me.choco.locksecurity.commands.LockInspectCmd;
import me.choco.locksecurity.commands.LockListCmd;
import me.choco.locksecurity.commands.LockNotifyCmd;
import me.choco.locksecurity.commands.LockSecurityCmd;
import me.choco.locksecurity.commands.TransferLockCmd;
import me.choco.locksecurity.commands.UnlockCmd;
import me.choco.locksecurity.data.LockSecurityPlayer;
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
import me.choco.locksecurity.utils.general.ItemBuilder;
import me.choco.locksecurity.utils.general.Metrics;
import me.choco.locksecurity.utils.general.UpdateChecker;
import me.choco.locksecurity.utils.json.JSONUtils;
import me.choco.locksecurity.utils.localization.Locale;

public class LockSecurity extends JavaPlugin {
	
	private static LockSecurity instance;
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	private AutoSaveLoop autosave;
	
	private IPlayerRegistry playerRegistry;
	private ILockedBlockManager lockedBlockManager;
	
	public File playerdataDir, infoFile;
	private Locale locale;
	
	@Override
	public void onEnable() {
		// Default values
		instance = this;
		this.playerdataDir = new File(this.getDataFolder(), "playerdata");
		this.infoFile = new File(this.getDataFolder(), "plugin.info");
		this.playerRegistry = new PlayerRegistry(this);
		this.saveDefaultConfig();
		
		// Locales
		Locale.init(this);
		Locale.saveDefaultLocale("en_CA");
		Locale.saveDefaultLocale("fr_CA");
		this.locale = Locale.getLocale(this.getConfig().getString("Locale", "en_CA"));
		
		// Transfer old data if necessary
		if (!playerdataDir.exists()) {
			if (this.playerdataDir.mkdirs()) this.getLogger().info("Successfully created new playerdata directory");
			
			if (new File(this.getDataFolder(), "lockinfo.db").exists())
				TransferUtils.fromDatabase(this);
			else if (new File(this.getDataFolder(), "locked.yml").exists())
				TransferUtils.fromFile(this);
		}
		
		// Save data file(s)
		if (!this.infoFile.exists()) {
			try{
				this.infoFile.createNewFile();
				FileUtils.write(infoFile, "nextLockID=1\nnextKeyID=1", Charset.defaultCharset());
			} catch (IOException e) { e.printStackTrace(); }
			
			this.getLogger().info("Successfully created new plugin information file");
		}
		
		this.lockedBlockManager = new LockedBlockManager(this);
		
		// Register events
		this.getLogger().info("Registering events...");
		
			/* General/Lock-Based listeners */
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new BlockClickListener(this), this);
		manager.registerEvents(new BlockBreakListener(this), this);
		manager.registerEvents(new KeyCraftingListener(), this);
		
			/* Protection listeners */
		manager.registerEvents(new KeyPlaceProtectionListener(), this);
		manager.registerEvents(new GriefProtectionListener(this), this);
		manager.registerEvents(new ExplosionProtectionListener(this), this);
		manager.registerEvents(new DoubleChestProtectionListener(this), this);
		
			/* Data listeners */
		manager.registerEvents(new WorldDataLoader(this), this);
		manager.registerEvents(new WorldDataUnloader(this), this);
		
		// Register commands
		this.getLogger().info("Registering plugin commands...");
		this.getCommand("locksecurity").setExecutor(new LockSecurityCmd(this));
		this.getCommand("forgekey").setExecutor(new ForgeKeyCmd(this));
		this.getCommand("givekey").setExecutor(new GiveKeyCmd(this));
		this.getCommand("ignorelocks").setExecutor(new IgnoreLocksCmd(this));
		this.getCommand("lockinspect").setExecutor(new LockInspectCmd(this));
		this.getCommand("locklist").setExecutor(new LockListCmd(this));
		this.getCommand("locknotify").setExecutor(new LockNotifyCmd(this));
		this.getCommand("transferlock").setExecutor(new TransferLockCmd(this));
		this.getCommand("unlock").setExecutor(new UnlockCmd(this));
		
		// Register crafting recipes
		this.getLogger().info("Registering custom crafting recipes...");
		ItemStack unsmithedKey = KeyFactory.getUnsmithedkey();
		unsmithedKey.setAmount(getConfig().getInt("RecipeYield"));
		Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey1"), unsmithedKey).shape("B  ", " I ", "  P").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey2"), unsmithedKey).shape(" B ", " I ", " P ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey3"), unsmithedKey).shape("  B", " I ", "P  ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey4"), unsmithedKey).shape("   ", "BIP", "   ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey5"), unsmithedKey).shape("   ", "PIB", "   ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey6"), unsmithedKey).shape("  P", " I ", "B  ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey7"), unsmithedKey).shape(" P ", " I ", " B ").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(this, "unsmithedkey8"), unsmithedKey).shape("P  ", " I ", "  B").setIngredient('B', Material.IRON_FENCE).setIngredient('I', Material.IRON_INGOT).setIngredient('P', Material.WOOD));
		Bukkit.addRecipe(new ShapelessRecipe(new NamespacedKey(this, "keyclear"), new ItemBuilder(Material.BEDROCK).setName("SINGLE").build()).addIngredient(1, Material.TRIPWIRE_HOOK));
		Bukkit.addRecipe(new ShapelessRecipe(new NamespacedKey(this, "keycombine"), new ItemBuilder(Material.BEDROCK).setName("DUAL").build()).addIngredient(2, Material.TRIPWIRE_HOOK));
		
		// Load all registered data
		this.getLogger().info(locale.getMessage("Loading player JSON data from file. This may take a while"));
		for (File file : playerdataDir.listFiles()) {
			OfflinePlayer rawPlayer = Bukkit.getOfflinePlayer(UUID.fromString(file.getName().replace(".json", "")));
			
			LockSecurityPlayer player = new LockSecurityPlayer(rawPlayer);
			this.playerRegistry.registerPlayer(player);
			
			player.read(JSONUtils.readJSON(file));
		}
		
		// Load data for worlds that are already loaded (In case of a reload)
		if (Bukkit.getOnlinePlayers().size() != 0) {
			for (World world : Bukkit.getWorlds()) {
				if (world.getPlayers().size() == 0) continue;
				this.lockedBlockManager.loadDataForWorld(world);
			}
		}
		
		this.autosave = AutoSaveLoop.startLoop(this, getConfig().getInt("DataSaveIntervalTicks"));
		
		UpdateChecker checker = new UpdateChecker(this, 12650);
		if (checker.queryUpdateCheck() && checker.requiresUpdate()) {
			this.getLogger().info(locale.getMessage("An update is available for download on SpigotMC!"));
		}
		
		this.getLogger().info("Enabling plugin metrics");
		new Metrics(this);
	}
	
	@Override
	public void onDisable() {
		if (autosave != null) {
			this.getLogger().info("Forcing one final registry save...");
			this.autosave.run();
			this.autosave.cancel();
		}
		
		if (playerRegistry != null) {
			this.getLogger().info("Clearing all localized data");
			this.playerRegistry.getPlayers().forEach(ILockSecurityPlayer::clearLocalData);
			this.playerRegistry.clearRegistry();
		}
		
		if (lockedBlockManager != null)
			this.lockedBlockManager.clearLockedBlockData();
		
		Locale.clearLocaleData();
	}
	
	/** 
	 * Get an instance of the LockSecurity class. This is for API usage
	 * 
	 * @return an instance of LockSecurity
	 */
	public static LockSecurity getPlugin() {
		return instance;
	}
	
	/** 
	 * Get the main instance of the {@link PlayerRegistry} class
	 * 
	 * @return the PlayerRegistry class
	 */
	public IPlayerRegistry getPlayerRegistry() {
		return playerRegistry;
	}
	
	/** 
	 * Get the main instance of the {@link LockedBlockManager} class
	 * 
	 * @return the LockedBlockManager class
	 */
	public ILockedBlockManager getLockedBlockManager() {
		return lockedBlockManager;
	}
	
	/** 
	 * Send a message to the specified player with the [LockSecurity] prefix
	 * 
	 * @param sender the user to send the message to
	 * @param message the message to send
	 */
	public void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "LockSecurity" + ChatColor.GOLD + "] " + ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', message));
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