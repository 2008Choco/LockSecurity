package me.choco.LSaddon.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.choco.LSaddon.ChestCollector;
import me.choco.locks.api.PlayerLockBlockEvent;

public class LockBlock implements Listener{
	ChestCollector plugin;
	public LockBlock(ChestCollector plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onLockBlock(PlayerLockBlockEvent event){
		if (event.getBlock().getType().equals(Material.CHEST)){
			if (plugin.getConfig().getBoolean("DisplayOnLockMsg") &&
					plugin.lockedAccessor.getLockCount(event.getPlayer()) == 0){
				event.getPlayer().sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
						"To convert this into a collector, use " + ChatColor.DARK_AQUA + "/collects <ItemStack,ItemStack,ItemStack>");
			}
		}
	}
}