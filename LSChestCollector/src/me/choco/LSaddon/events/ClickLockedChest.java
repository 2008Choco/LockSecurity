package me.choco.LSaddon.events;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.choco.LSaddon.ChestCollector;
import me.choco.LSaddon.utils.CollectorBlock;
import me.choco.LSaddon.utils.CollectorHandler;
import me.choco.locksecurity.api.ILockedBlock;
import me.choco.locksecurity.api.event.PlayerInteractLockedBlockEvent;

public class ClickLockedChest implements Listener {
	
	private final ChestCollector plugin;
	private final CollectorHandler collectorHandler;
	
	public ClickLockedChest(ChestCollector plugin) {
		this.plugin = plugin;
		this.collectorHandler = plugin.getCollectorHandler();
	}
	
	@EventHandler
	public void onClickLockedChest(PlayerInteractLockedBlockEvent event) {
		ILockedBlock block = event.getBlock();
		Player player = event.getPlayer().getPlayer().getPlayer();
		
		if (block.getBlock().getType() != Material.CHEST && block.getBlock().getType() != Material.TRAPPED_CHEST) return;
		if (!plugin.hasCommandItems(player)) return;
		
		if (!block.isOwner(event.getPlayer())) {
			player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
					"You do not own this chest");
			
			this.plugin.clearCommandItems(player);
			return;
		}
		
		if (collectorHandler.isCollector(block.getBlock())) {
			player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
					"This is already a collector");
			
			this.plugin.clearCommandItems(player);
			return;
		}
		
		Material[] items = plugin.getCommandItems(player);
		this.collectorHandler.registerCollector(new CollectorBlock(block, collectorHandler.getNextCollectorID(true), items));
		
		String itemList = String.join(",", Arrays.stream(items)
				.map(Object::toString)
				.toArray(String[]::new));
		
		player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
				"Chest collector created. The following items will be collected: " + itemList);
		this.plugin.clearCommandItems(player);
	}
	
}