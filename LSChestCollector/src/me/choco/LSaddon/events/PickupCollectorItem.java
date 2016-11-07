package me.choco.LSaddon.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.choco.LSaddon.ChestCollector;
import me.choco.LSaddon.utils.CollectorHandler;

public class PickupCollectorItem implements Listener{
	
	private ChestCollector plugin;
	private CollectorHandler collectorHandler;
	public PickupCollectorItem(ChestCollector plugin){
		this.plugin = plugin;
		this.collectorHandler = plugin.getCollectorHandler();
	}
	
	@EventHandler
	public void onCollectionPickup(PlayerPickupItemEvent event){
		boolean found = false;
		ItemStack pickedUpItem = event.getItem().getItemStack();
		String itemType = pickedUpItem.getType().toString();
		Player player = event.getPlayer();
		
		for (int id : collectorHandler.getAllCollectors(player)){
			String worldName = plugin.collectors.getConfig().getString(id + ".Location.World");
			int x = plugin.collectors.getConfig().getInt(id + ".Location.X");
			int y = plugin.collectors.getConfig().getInt(id + ".Location.Y");
			int z = plugin.collectors.getConfig().getInt(id + ".Location.Z");
			Location collectorLocation = new Location(Bukkit.getWorld(worldName), x, y, z);
			
			for (String item : collectorHandler.getCollectorItems(collectorLocation.getBlock())){
				if (item.toUpperCase().equals(itemType.toUpperCase())){
					if (collectorLocation.getBlock().getType().equals(Material.CHEST)
							|| collectorLocation.getBlock().getType().equals(Material.TRAPPED_CHEST)){
						Chest chest = (Chest) collectorLocation.getBlock().getState();
						if (hasOpenSlot(chest.getInventory())){
							chest.getInventory().addItem(pickedUpItem);
							found = true;
							
							event.setCancelled(true);
							event.getItem().remove();
							player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 0);
						}
						break;
					}
				}
			}
			if (found)break;
		}
	}
	
	private boolean hasOpenSlot(Inventory inventory){
		for (ItemStack item : inventory.getContents()){
			if (item == null) return true;
		}
		return false;
	}
}