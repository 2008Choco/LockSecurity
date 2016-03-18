package me.choco.locks.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class AttemptKeyPlace implements Listener{
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		ItemStack item = event.getItemInHand();
		if (!item.hasItemMeta()) return;
		if (!item.getItemMeta().hasDisplayName()) return;
		
		String name = item.getItemMeta().getDisplayName();
		if (name.equals(ChatColor.GRAY + "Key") || name.equals(ChatColor.GRAY + "Unsmithed Key"))
			event.setCancelled(true);
	}
}