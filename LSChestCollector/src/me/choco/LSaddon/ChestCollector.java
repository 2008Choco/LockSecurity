package me.choco.LSaddon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import me.choco.LSaddon.commands.CollectsCmd;
import me.choco.LSaddon.events.ClickLockedChest;
import me.choco.LSaddon.events.PickupCollectorItem;
import me.choco.LSaddon.events.UnlockBlock;
import me.choco.LSaddon.utils.CollectorHandler;
import me.choco.LSaddon.utils.ConfigAccessor;
import me.choco.LSaddon.utils.Metrics;
import me.choco.locksecurity.LockSecurity;

public class ChestCollector extends JavaPlugin{

	private LockSecurity lockSecurity;
	public ConfigAccessor collectors;

	public ArrayList<String> collectorCreationMode = new ArrayList<String>();
	public HashMap<Location, Integer> collectorBlocks = new HashMap<Location, Integer>();
	private HashMap<String, String[]> collectsCommandTempInfo = new HashMap<String, String[]>();

	private CollectorHandler collectorHandler;

	@Override
	public void onEnable(){
		lockSecurity = LockSecurity.getPlugin();
		this.getLogger().info("Successfully hooked into LockSecurity.");
		
		this.collectorHandler = new CollectorHandler(this);

		//LSChestCollector default config
		getConfig().options().copyDefaults(true);
		saveConfig();
		//LSChestCollector collectors.yml
		collectors = new ConfigAccessor(this, "collectors.yml");
		collectors.loadConfig();

		//Register commands
		this.getLogger().info("Registering commands");
		this.getCommand("collects").setExecutor(new CollectsCmd(this));

		//Register events
		this.getLogger().info("Registering events");
		Bukkit.getPluginManager().registerEvents(new ClickLockedChest(this), this);
		Bukkit.getPluginManager().registerEvents(new PickupCollectorItem(this), this);
		Bukkit.getPluginManager().registerEvents(new UnlockBlock(this), this);

		//Load Metrics
		if (getConfig().getBoolean("MetricsEnabled")){
			this.getLogger().info("Enabling Plugin Metrics");
			try{
				Metrics metrics = new Metrics(this);
				metrics.start();
			}
			catch (IOException e){
				e.printStackTrace();
				getLogger().warning("Could not enable Plugin Metrics. If issues continue, please put in a ticket on the "
						+ "LSChestCollector development page");
			}
		}

		//Add RAM information
		this.getLogger().info("Storing all Chest Collectors' information in the server RAM");
		int errors = 0;
		Set<String> keys = collectors.getConfig().getKeys(false);
		keys.remove("NextCollectorID");
		for (String key : keys){
			try{
				int id = Integer.parseInt(key);
				World world = Bukkit.getServer().getWorld(collectors.getConfig().getString(key + ".Location.World"));
				double x = collectors.getConfig().getDouble(key + ".Location.X");
				double y = collectors.getConfig().getDouble(key + ".Location.Y");
				double z = collectors.getConfig().getDouble(key + ".Location.Z");
				Location location = new Location(world, x, y, z);

				if (!location.getBlock().getType().equals(Material.CHEST) || !location.getBlock().getType().equals(Material.TRAPPED_CHEST)){
					collectorBlocks.put(location, id);
				}else{
					this.getLogger().info("Collector ID " + key + " (Location: " + formatLocation(location) + ", Owner: " + collectors.getConfig().getString(key + ".OwnerName") + ") removed due to not being identical as the save. Was it removed?");
					collectors.getConfig().set(key, null);
					collectors.saveConfig();
					collectors.reloadConfig();
				}
			}catch(NumberFormatException e){
				if (errors == 0){
					e.printStackTrace();
					this.getLogger().warning("Something went wrong. Tell Choco about it immediately!");
					this.getLogger().warning("Go to: http://dev.bukkit.org/bukkit-plugins/ls-chest-collector/tickets" + 
							", and create a ticket including the error logged above");
					this.getLogger().warning("Be sure to also include a copy of your collectors.yml file in the ticket for revision");
				}
				errors++;
				continue;
			}
		}
		if (errors > 0){
			this.getLogger().info("Stored as many collectors as possible in server RAM. " + errors + " collectors could not be loaded");
		}else{
			this.getLogger().info("Successfully stored all collectors in server RAM. Plugin ready for use!");
		}
	}

	@Override
	public void onDisable(){
		this.getLogger().info("Clearing all local RAM storage");
		collectorCreationMode.clear();
		collectorBlocks.clear();
		collectsCommandTempInfo.clear();
	}

	public LockSecurity getLockSecurity(){
		return lockSecurity;
	}
	
	public CollectorHandler getCollectorHandler() {
		return collectorHandler;
	}

	public void setCommandItems(String playerName, String[] items){
		collectsCommandTempInfo.put(playerName, items);
	}

	public String[] getCommandItems(String playerName){
		return collectsCommandTempInfo.get(playerName);
	}

	private String formatLocation(Location location){
		return location.getWorld().getName() + " x:" + (int)location.getBlockX() + " y:" + (int)location.getBlockY() + " z:" + (int)location.getBlockY();
	}

	/* TODO Future Version:
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------
	 * TODO: Next Version:
	 * Configuration Additions:
	 *     - A list to disable specific items from being collectable
	 *     - Maximize the amount of chest collectors you may have per world
	 */
}