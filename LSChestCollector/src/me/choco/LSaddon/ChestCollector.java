package me.choco.LSaddon;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.enums.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.choco.LSaddon.commands.CollectsCmd;
import me.choco.LSaddon.events.ClickLockedChest;
import me.choco.LSaddon.events.PickupCollectorItem;
import me.choco.LSaddon.events.UnlockBlock;
import me.choco.LSaddon.utils.CollectorBlock;
import me.choco.LSaddon.utils.CollectorHandler;
import me.choco.LSaddon.utils.ConfigAccessor;
import me.choco.locksecurity.LockSecurity;

public class ChestCollector extends JavaPlugin {

	public ConfigAccessor collectorsFile;

	private LockSecurity lockSecurity;
	private CollectorHandler collectorHandler;
	private final Map<UUID, Material[]> collectsCommandInfo = new HashMap<>();

	@Override
	public void onEnable() {
		if (!Bukkit.getPluginManager().isPluginEnabled("LockSecurity")) {
			this.getLogger().severe("Could not find LockSecurity. Please install it on the server. Shutting down...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		// LSChestCollector collectors.yml
		this.collectorsFile = new ConfigAccessor(this, "collectors.yml");
		this.collectorsFile.loadConfig();
		
		// Load required fields
		this.saveDefaultConfig();
		this.lockSecurity = LockSecurity.getPlugin();
		this.collectorHandler = new CollectorHandler(this);

		// Register commands
		this.getLogger().info("Registering commands");
		this.getCommand("collects").setExecutor(new CollectsCmd(this));

		// Register events
		this.getLogger().info("Registering events");
		Bukkit.getPluginManager().registerEvents(new ClickLockedChest(this), this);
		Bukkit.getPluginManager().registerEvents(new PickupCollectorItem(this), this);
		Bukkit.getPluginManager().registerEvents(new UnlockBlock(this), this);

		//Add information to local memory
		int errors = 0;
		this.getLogger().info("Storing all Chest Collectors information into local memory");
		Set<String> keys = collectorsFile.getConfig().getKeys(false);
		keys.remove("NextCollectorID");
		for (String key : keys) {
			try {
				int id = Integer.parseInt(key);
				World world = Bukkit.getServer().getWorld(collectorsFile.getConfig().getString(key + ".Location.World"));
				double x = collectorsFile.getConfig().getDouble(key + ".Location.X");
				double y = collectorsFile.getConfig().getDouble(key + ".Location.Y");
				double z = collectorsFile.getConfig().getDouble(key + ".Location.Z");
				Location location = new Location(world, x, y, z);
				Material[] materials = collectorsFile.getConfig().getStringList(key + ".Items").stream()
						.map(m -> EnumUtils.getEnum(Material.class, m))
						.filter(m -> m != null)
						.toArray(Material[]::new);
				
				CollectorBlock collector = new CollectorBlock(lockSecurity.getLockedBlockManager().getLockedBlock(location), id, materials);

				if (location.getBlock().getType().equals(Material.CHEST) || location.getBlock().getType().equals(Material.TRAPPED_CHEST)) {
					this.collectorHandler.registerCollector(collector);
				}
				else {
					this.getLogger().info("Collector ID " + key + " (Location: " + formatLocation(location) + ", Owner: " + collectorsFile.getConfig().getString(key + ".OwnerName") + ") removed due to not being identical as the save. Was it removed?");
					this.collectorsFile.getConfig().set(key, null);
				}
			}
			catch (NumberFormatException e) {
				if (errors++ == 0) {
					e.printStackTrace();
					this.getLogger().warning("Something went wrong. Tell Choco about it immediately!");
					this.getLogger().warning("Go to: http://dev.bukkit.org/bukkit-plugins/ls-chest-collector/tickets" + 
							", and create a ticket including the error logged above");
					this.getLogger().warning("Be sure to also include a copy of your collectors.yml file in the ticket for revision");
				}
				
				continue;
			}

			this.collectorsFile.saveConfig();
			this.collectorsFile.reloadConfig();
		}
		
		this.getLogger().info(errors > 0
				? "Stored as many collectors as possible into local memory. \" + errors + \" collectors could not be loaded"
				: "Successfully stored all collectors into local memory. Plugin ready for use!");
	}

	@Override
	public void onDisable() {
		this.getLogger().info("Clearing all local memory");
		
		if (collectorHandler != null) {
			for (CollectorBlock collector : collectorHandler.getCollectors()) {
				int id = collector.getId();
				OfflinePlayer owner = collector.getBlock().getOwner().getPlayer();
				Location location = collector.getBlock().getLocation();
				
				this.collectorsFile.getConfig().set(id + ".OwnerUUID", owner.getUniqueId().toString());
				this.collectorsFile.getConfig().set(id + ".OwnerName", owner.getName());
				this.collectorsFile.getConfig().set(id + ".Items", collector.getCollectionMaterials().stream().map(Object::toString).collect(Collectors.toList()));
				this.collectorsFile.getConfig().set(id + ".Location.X", location.getBlockX());
				this.collectorsFile.getConfig().set(id + ".Location.Y", location.getBlockY());
				this.collectorsFile.getConfig().set(id + ".Location.Z", location.getBlockZ());
				this.collectorsFile.getConfig().set(id + ".Location.World", location.getWorld().getName());
			}
			
			this.collectorHandler.clearCollectors();
			this.collectorsFile.getConfig().set("NextCollectorID", collectorHandler.getNextCollectorID());
		}
		
		if (collectorsFile != null) this.collectorsFile.saveConfig();
		if (collectsCommandInfo != null) this.collectsCommandInfo.clear();
	}

	public LockSecurity getLockSecurity() {
		return lockSecurity;
	}
	
	public CollectorHandler getCollectorHandler() {
		return collectorHandler;
	}

	public void setCommandItems(Player player, Material[] materials) {
		this.collectsCommandInfo.put(player.getUniqueId(), materials);
	}

	public Material[] getCommandItems(Player player) {
		return collectsCommandInfo.get(player.getUniqueId());
	}
	
	public boolean hasCommandItems(Player player) {
		return collectsCommandInfo.containsKey(player.getUniqueId());
	}
	
	public void clearCommandItems(Player player) {
		this.collectsCommandInfo.remove(player.getUniqueId());
	}

	private String formatLocation(Location location) {
		return location.getWorld().getName() + " x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockY();
	}
	
}