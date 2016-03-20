package me.choco.LSaddon.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.choco.LSaddon.ChestCollector;
import me.choco.LSaddon.utils.CollectorHandler;
import me.choco.locks.LockSecurity;
import me.choco.locks.api.LockedBlock;
import me.choco.locks.api.event.PlayerInteractLockedBlockEvent;

public class ClickLockedChest implements Listener{
	ChestCollector plugin;
	LockSecurity lockSecurity;
	CollectorHandler collectorHandler;
	public ClickLockedChest(ChestCollector plugin){
		this.plugin = plugin;
		lockSecurity = plugin.getLockSecurity();
		this.collectorHandler = new CollectorHandler(plugin);
	}
	
	@EventHandler
	public void onClickLockedChest(PlayerInteractLockedBlockEvent event){
		LockedBlock block = event.getBlock();
		Player player = event.getPlayer();
		if (plugin.collectorCreationMode.contains(player.getName())){
			if (block.getBlock().getType().equals(Material.CHEST)){
				if (block.getOwner().getUniqueId().equals(player.getUniqueId())){
					event.setCancelled(true);
					if (!collectorHandler.isCollector(block.getBlock())){
						String[] items = plugin.getCommandItems(player.getName());
						collectorHandler.addCollector(player, items, block.getBlock().getLocation());
						String itemList = ""; for (String item : items){itemList = itemList + ", " + item;}
						player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
								"Chest collector created. The following items will be collected: " + itemList);
						plugin.collectorCreationMode.remove(player.getName());
					}else{
						player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
								"This is already a collector");
						plugin.collectorCreationMode.remove(player.getName());
					}
				}else{
					player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
							"You do not own this chest");
					plugin.collectorCreationMode.remove(player.getName());
				}
			}
		}
	}
}