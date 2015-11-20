package me.choco.locks.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AttemptKeyPlace implements Listener{
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		ItemStack item;
		if (event.getItemInHand() != null){
			item = event.getItemInHand();
			if (item.hasItemMeta()){
				ItemMeta itemMeta = item.getItemMeta();
				if (itemMeta.getDisplayName().equals(ChatColor.GRAY + "Key") || itemMeta.getDisplayName().equals(ChatColor.GRAY + "Unsmithed Key")){
					event.setCancelled(true);
				}
			}
		}
	}
}