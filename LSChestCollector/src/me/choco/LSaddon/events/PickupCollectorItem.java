package me.choco.LSaddon.events;

import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import me.choco.LSaddon.ChestCollector;
import me.choco.LSaddon.utils.CollectorBlock;
import me.choco.LSaddon.utils.CollectorHandler;

public class PickupCollectorItem implements Listener {
	
	private final CollectorHandler collectorHandler;
	
	public PickupCollectorItem(ChestCollector plugin) {
		this.collectorHandler = plugin.getCollectorHandler();
	}
	
	@EventHandler
	public void onCollectionPickup(EntityPickupItemEvent event) {
		LivingEntity entity = event.getEntity();
		if (!(entity instanceof Player)) return;
		
		Player player = (Player) entity;
		ItemStack item = event.getItem().getItemStack();
		int remaining = item.getAmount();
		boolean found = false;
		
		// Add item to as many collectors as possible
		for (CollectorBlock collector : collectorHandler.getCollectors(player)) {
			if (!collector.shouldCollect(item.getType())) continue;
			Chest chest = (Chest) collector.getBlock().getBlock().getState();
			
			if (!hasOpenSlot(chest, item)) continue;
			
			remaining = chest.getInventory().addItem(item).values().stream()
					.mapToInt(ItemStack::getAmount).sum();
			
			found = true;
			if (remaining == 0) {
				break;
			}
		}
		
		// Update Item entity with remaining amount (or remove it)
		if (found) {
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 0);
			
			if (remaining == 0) {
				event.setCancelled(true);
				event.getItem().remove();
			}
			else {
				item.setAmount(remaining);
				event.getItem().setItemStack(item);
			}
		}
	}
	
	private boolean hasOpenSlot(Chest chest, ItemStack item) {
		for (ItemStack inventoryItem : chest.getInventory()) {
			if (inventoryItem == null) return true;
			if (inventoryItem.isSimilar(item) && inventoryItem.getAmount() < inventoryItem.getMaxStackSize()) return true;
		}
		
		return false;
	}
}