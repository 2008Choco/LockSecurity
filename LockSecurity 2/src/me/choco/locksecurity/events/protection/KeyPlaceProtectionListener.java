package me.choco.locksecurity.events.protection;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import me.choco.locksecurity.api.KeyFactory.KeyType;

public class KeyPlaceProtectionListener implements Listener {
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
		
		String name = item.getItemMeta().getDisplayName();
		for (KeyType type : KeyType.values()) {
			if (type.getItemDisplayName().equals(name)) {
				event.setCancelled(true);
				break;
			}
		}
	}
	
}