package me.choco.LSaddon.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.choco.LSaddon.ChestCollector;
import me.choco.LSaddon.utils.CollectorHandler;
import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.api.event.PlayerInteractLockedBlockEvent;

public class ClickLockedChest implements Listener{
	
	private ChestCollector plugin;
	private CollectorHandler collectorHandler;
	public ClickLockedChest(ChestCollector plugin){
		this.plugin = plugin;
		this.collectorHandler = plugin.getCollectorHandler();
	}
	
	@EventHandler
	public void onClickLockedChest(PlayerInteractLockedBlockEvent event){
		LockedBlock block = event.getBlock();
		Player player = event.getPlayer().getPlayer().getPlayer();
		
		if (!plugin.collectorCreationMode.contains(player.getName())) return;
		if (block.getBlock().getType().equals(Material.CHEST)) return;
		
		if (!block.getOwner().getPlayer().getUniqueId().equals(player.getUniqueId())){
			player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
					"You do not own this chest");
			plugin.collectorCreationMode.remove(player.getName());
			return;
		}
		
		if (collectorHandler.isCollector(block.getBlock())){
			player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
					"This is already a collector");
			plugin.collectorCreationMode.remove(player.getName());
			return;
		}
		
		String[] items = plugin.getCommandItems(player.getName());
		collectorHandler.addCollector(player, items, block.getBlock().getLocation());
		String itemList = ""; for (String item : items){itemList = itemList + ", " + item;}
		player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
				"Chest collector created. The following items will be collected: " + itemList);
		plugin.collectorCreationMode.remove(player.getName());
	}
}