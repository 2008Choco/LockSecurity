package wtf.choco.locksecurity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

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

import wtf.choco.locksecurity.api.LockSecurity;
import wtf.choco.locksecurity.api.LockSecurityAPI;
import wtf.choco.locksecurity.api.data.ILockSecurityPlayer;
import wtf.choco.locksecurity.api.registration.ILockedBlockManager;
import wtf.choco.locksecurity.api.utils.ItemBuilder;
import wtf.choco.locksecurity.api.utils.KeyFactory;
import wtf.choco.locksecurity.commands.ForgeKeyCmd;
import wtf.choco.locksecurity.commands.GiveKeyCmd;
import wtf.choco.locksecurity.commands.IgnoreLocksCmd;
import wtf.choco.locksecurity.commands.LockInspectCmd;
import wtf.choco.locksecurity.commands.LockListCmd;
import wtf.choco.locksecurity.commands.LockNotifyCmd;
import wtf.choco.locksecurity.commands.LockSecurityCmd;
import wtf.choco.locksecurity.commands.TransferLockCmd;
import wtf.choco.locksecurity.commands.UnlockCmd;
import wtf.choco.locksecurity.data.LockSecurityPlayer;
import wtf.choco.locksecurity.events.BlockBreakListener;
import wtf.choco.locksecurity.events.BlockClickListener;
import wtf.choco.locksecurity.events.KeyCraftingListener;
import wtf.choco.locksecurity.events.data.WorldDataLoader;
import wtf.choco.locksecurity.events.data.WorldDataUnloader;
import wtf.choco.locksecurity.events.protection.DoubleChestProtectionListener;
import wtf.choco.locksecurity.events.protection.ExplosionProtectionListener;
import wtf.choco.locksecurity.events.protection.GriefProtectionListener;
import wtf.choco.locksecurity.events.protection.KeyPlaceProtectionListener;
import wtf.choco.locksecurity.registration.LockedBlockManager;
import wtf.choco.locksecurity.registration.PlayerRegistry;
import wtf.choco.locksecurity.utils.AutoSaveLoop;
import wtf.choco.locksecurity.utils.JSONUtils;
import wtf.choco.locksecurity.utils.general.Metrics;
import wtf.choco.locksecurity.utils.general.UpdateChecker;
import wtf.choco.locksecurity.utils.localization.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LockSecurityPlugin extends JavaPlugin implements LockSecurity {
	
	private static LockSecurityPlugin instance;
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	private AutoSaveLoop autosave;
	
	private PlayerRegistry playerRegistry;
	private LockedBlockManager lockedBlockManager;
	
	public File playerdataDir, infoFile;
	private Locale locale;
	
	@Override
	public void onEnable() {
		// Default values
		instance = this;
		LockSecurityAPI.setImplementation(this);
		
		this.playerdataDir = new File(getDataFolder(), "playerdata");
		this.infoFile = new File(getDataFolder(), "plugin.info");
		this.playerRegistry = new PlayerRegistry(this);
		this.saveDefaultConfig();
		
		// Locale
		Locale.init(this);
		Locale.saveDefaultLocale("en_CA");
		Locale.saveDefaultLocale("fr_CA");
		Locale.setDefaultMessageFunction(this::sendMessage);
		this.locale = Locale.getLocale(this.getConfig().getString("Locale", "en_CA"));
		
		// Transfer old data if necessary
		if (!playerdataDir.exists()) {
			if (this.playerdataDir.mkdirs()) this.getLogger().info("Successfully created new playerdata directory");
			
			if (new File(getDataFolder(), "lockinfo.db").exists())
				TransferUtils.fromDatabase(this);
			else if (new File(getDataFolder(), "locked.yml").exists())
				TransferUtils.fromFile(this);
		}
		
		// Save data file(s)
		if (!this.infoFile.exists()) {
			try {
				this.infoFile.createNewFile();
				FileUtils.write(infoFile, "nextLockID=1\nnextKeyID=1", Charset.defaultCharset());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
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
		this.getLogger().info("Loading player JSON data from file. This may take a while");
		for (File file : playerdataDir.listFiles()) {
			OfflinePlayer rawPlayer = Bukkit.getOfflinePlayer(UUID.fromString(file.getName().replace(".json", "")));
			
			LockSecurityPlayer player = new LockSecurityPlayer(rawPlayer);
			player.read(JSONUtils.readJSON(file));
			this.playerRegistry.registerPlayer(player);
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
			this.getLogger().info("An update is available for download on SpigotMC!");
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
	 * Get an instance of the LockSecurityPlugin class. This is for implementation
	 * usage. For API use, see {@link LockSecurityAPI} and its various methods
	 * 
	 * @return an instance of LockSecurityPlugin
	 */
	public static LockSecurityPlugin getPlugin() {
		return instance;
	}
	
	/**
	 * Get the player registry for LockSecurity
	 * 
	 * @return the player registry
	 */
	public PlayerRegistry getPlayerRegistry() {
		return playerRegistry;
	}
	
	@Override
	public ILockedBlockManager getLockedBlockManager() {
		return lockedBlockManager;
	}
	
	@Override
	public ILockSecurityPlayer getPlayer(OfflinePlayer player) {
		return playerRegistry.getPlayer(player);
	}
	
	@Override
	public ILockSecurityPlayer getPlayer(UUID player) {
		return playerRegistry.getPlayer(player);
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