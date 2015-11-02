package me.choco.LSaddon.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.choco.LSaddon.ChestCollector;
import me.choco.LSaddon.utils.CollectorHandler;
import me.choco.locks.api.PlayerInteractLockedBlockEvent;
import me.choco.locks.utils.LockedBlockAccessor;

public class ClickLockedChest implements Listener{
	ChestCollector plugin;
	LockedBlockAccessor lockedAccessor;
	CollectorHandler collectorHandler;
	public ClickLockedChest(ChestCollector plugin){
		this.plugin = plugin;
		this.lockedAccessor = plugin.lockedAccessor;
		this.collectorHandler = new CollectorHandler(plugin);
	}
	
	@EventHandler
	public void onClickLockedChest(PlayerInteractLockedBlockEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (block.getType().equals(Material.CHEST)){
			if (lockedAccessor.getBlockOwnerUUID(block).equals(player.getUniqueId().toString())){
				if (plugin.collectCreationMode.contains(player.getName())){
					event.setCancelled(true);
					String[] items = plugin.getCommandItems(player.getName());
					collectorHandler.addCollector(player, items, block.getLocation());
					String itemList = ""; for (String item : items){itemList = itemList + ", " + item;}
					player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
							"Chest collector created. The following items will be collected: " + itemList);
					plugin.collectCreationMode.remove(player.getName());
				}
			}else{
				player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
						"You do not own this chest");
			}
		}
	}
}