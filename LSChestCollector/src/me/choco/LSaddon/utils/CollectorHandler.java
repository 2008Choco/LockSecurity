package me.choco.LSaddon.utils;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.choco.LSaddon.ChestCollector;

public class CollectorHandler {
	
	private ChestCollector plugin;
	public CollectorHandler(ChestCollector plugin){
		this.plugin = plugin;
	}
	
	/** Create a chest collector under the following parameters
	 * @param owner - The owner of the collector
	 * @param items - The items that this chest will collect
	 * @param location - The location of the chest
	 */
	public void addCollector(Player owner, String[] items, Location location){
		plugin.collectors.getConfig().set(getNextID() + ".OwnerUUID", owner.getUniqueId().toString());
		plugin.collectors.getConfig().set(getNextID() + ".OwnerName", owner.getName());
		plugin.collectors.getConfig().set(getNextID() + ".Items", items);
		plugin.collectors.getConfig().set(getNextID() + ".Location.X", location.getBlockX());
		plugin.collectors.getConfig().set(getNextID() + ".Location.Y", location.getBlockY());
		plugin.collectors.getConfig().set(getNextID() + ".Location.Z", location.getBlockZ());
		plugin.collectors.getConfig().set(getNextID() + ".Location.World", location.getWorld().getName());
		plugin.collectors.saveConfig();
		plugin.collectors.reloadConfig();
		plugin.collectorBlocks.put(location, getNextID());
		setNextID();
	}
	
	/** Unregister a collector from RAM and from the collectors.yml
	 * @param block - The block to unregister
	 */
	public void removeCollector(Block block){
		int id = plugin.collectorBlocks.get(block.getLocation());
		plugin.collectors.getConfig().set(String.valueOf(id), null);
		plugin.collectorBlocks.remove(block.getLocation());
		
		plugin.collectors.saveConfig();
		plugin.collectors.reloadConfig();
	}
	
	/** Unregister a collector from RAM and from the collectors.yml
	 * @param id - The id to unregister
	 */
	public void removeCollector(int id){
		String worldName = plugin.collectors.getConfig().getString(id + ".Location.World");
		int x = plugin.collectors.getConfig().getInt(id + ".Location.X");
		int y = plugin.collectors.getConfig().getInt(id + ".Location.Y");
		int z = plugin.collectors.getConfig().getInt(id + ".Location.Z");
		
		plugin.collectors.getConfig().set(String.valueOf(id), null);
		plugin.collectorBlocks.remove(new Location(Bukkit.getWorld(worldName), x, y, z));
		
		plugin.collectors.saveConfig();
		plugin.collectors.reloadConfig();
	}
	
	/** Get the name of the owner of the specified collector
	 * @param block - The block to gather information from
	 * @return String - The last known name of the owner
	 */
	public String getCollectorOwner(Block block){
		return plugin.collectors.getConfig().getString(plugin.collectorBlocks.get(block.getLocation()) + ".OwnerName");
	}
	
	/** Get the UUID of the owner of the specified collector
	 * @param block - The block to gather information from
	 * @return String - The UUID of the owner
	 */
	public String getCollectorUUID(Block block){
		return plugin.collectors.getConfig().getString(plugin.collectorBlocks.get(block.getLocation()) + ".OwnerUUID");
	}
	
	/** Get the items that the chest is collecting
	 * @param block - The block to gather information from
	 * @return String[] - An Array of Strings containing the Bukkit names of the items it is collecting
	 */
	public String[] getCollectorItems(Block block){
		String temp = plugin.collectors.getConfig().getString(plugin.collectorBlocks.get(block.getLocation()) + ".Items");
		temp = temp.replace("[", "").replace("]", "");
		return temp.split(", ");
	}
	
	/** Get all the stored collectors that the specified player owns
	 * @param player - The player to query
	 * @return ArrayList(Integer) - An ArrayList of ID's for all the player owned collectors
	 */
	public ArrayList<Integer> getAllCollectors(OfflinePlayer player){
		ArrayList<Integer> ids = new ArrayList<Integer>();
		Set<String> keys = plugin.collectors.getConfig().getKeys(false);
		keys.remove("NextCollectorID");
		for (String key : keys){
			try{
				if (plugin.collectors.getConfig().getString(key + ".OwnerUUID").equals(player.getUniqueId().toString()))
					ids.add(Integer.parseInt(key));
			}catch(NumberFormatException e){
				continue;
			}
		}
		return ids;
	}
	
	/** Check whether a block is a collector or not
	 * @param block - The block to gather information from
	 * @return boolean - True: Collector, False: Not-Collector
	 */
	public boolean isCollector(Block block){
		return plugin.collectors.getConfig().getKeys(false).contains(String.valueOf(plugin.collectorBlocks.get(block.getLocation())));
	}
	
	/** Get the next ID to be used for the next collector
	 * @return int - The next collector ID
	 */
	public int getNextID(){
		return plugin.collectors.getConfig().getInt("NextCollectorID");
	}
	
	private void setNextID(){
		plugin.collectors.getConfig().set("NextCollectorID", getNextID() + 1);
	}
}