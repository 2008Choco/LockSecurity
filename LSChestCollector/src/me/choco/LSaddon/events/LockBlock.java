package me.choco.LSaddon.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.choco.LSaddon.ChestCollector;
import me.choco.locksecurity.api.event.PlayerLockBlockEvent;
import me.choco.locksecurity.utils.LSPlayer;

public class LockBlock implements Listener {
	
	private final ChestCollector plugin;
	
	public LockBlock(ChestCollector plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onLockBlock(PlayerLockBlockEvent event) {
		Block block = event.getBlock();
		if (block.getType() != Material.CHEST || block.getType() != Material.TRAPPED_CHEST) return;
		
		LSPlayer lsPlayer = event.getPlayer();
		
		if (plugin.getConfig().getBoolean("DisplayOnLockMsg") && lsPlayer.getOwnedBlocks().size() == 0) {
			lsPlayer.getPlayer().getPlayer().sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
					"To convert this into a collector, use " + ChatColor.DARK_AQUA + "/collects <ItemStack,ItemStack,ItemStack>");
		}
	}
}