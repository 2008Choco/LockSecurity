package me.choco.locksecurity.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import me.choco.locksecurity.api.event.PlayerDuplicateKeyEvent;
import me.choco.locksecurity.api.event.PlayerMergeKeyEvent;
import me.choco.locksecurity.api.utils.KeyFactory;
import me.choco.locksecurity.api.utils.KeyFactory.KeyType;
import me.choco.locksecurity.utils.general.ItemBuilder;

public class KeyCraftingListener implements Listener {
	
	private static final ItemStack SINGLE_KEY_RESULT = new ItemBuilder(Material.BEDROCK).setName("SINGLE").build(),
			DUAL_KEY_RESULT = new ItemBuilder(Material.BEDROCK).setName("DUAL").build(),
			UNSMITHED_KEY = KeyFactory.getUnsmithedkey();
	
	@EventHandler
	public void onDuplicateMergeKey(PrepareItemCraftEvent event) {
		ItemStack result = event.getInventory().getResult();
		if (result == null || result.getType().equals(Material.AIR)) return;
		
		Player player = (Player) event.getInventory().getViewers().get(0);
		if (result.equals(DUAL_KEY_RESULT)) {
			ItemStack key1 = null, key2 = null; // (For merges:) - key1 = smithed
			
			for (ItemStack item : event.getInventory().getMatrix()) {
				if (item == null) continue;
				if (key1 != null && key2 != null) break;
				
				if (KeyFactory.isUnsmithedKey(item) || KeyFactory.isSmithedKey(item)) {
					if (key1 == null) { key1 = item; }
					else { key2 = item; break; }
				}
			}
			
			if (key1 == null || key2 == null || !player.hasPermission("locks.craft")) {
				event.getInventory().setResult(null);
				return;
			}
			
			boolean key1Unsmithed = KeyFactory.isUnsmithedKey(key1), key2Unsmithed = KeyFactory.isUnsmithedKey(key2);
			
			boolean duplicate = (!key1Unsmithed && key2Unsmithed) || (key1Unsmithed && !key2Unsmithed);
			boolean merge = !key1Unsmithed && !key2Unsmithed;
			
			if (duplicate) {
				int[] IDs = KeyFactory.getIDs((key2Unsmithed ? key1 : key2));
				
				PlayerDuplicateKeyEvent pdke = new PlayerDuplicateKeyEvent(player, key1, key2, IDs);
				Bukkit.getPluginManager().callEvent(pdke);
				
				event.getInventory().setResult(KeyFactory.buildKey(KeyType.SMITHED).setAmount(2).withIDs(IDs).build());
			}
			else if (merge) {
				ItemStack resultKey = KeyFactory.mergeKeys(key1, key2);
				
				PlayerMergeKeyEvent pmke = new PlayerMergeKeyEvent(player, key1, key2, KeyFactory.getIDs(resultKey));
				Bukkit.getPluginManager().callEvent(pmke);
				
				event.getInventory().setResult(resultKey);
			}
			else {
				event.getInventory().setResult(null); 
			}
		}
	}
	
	@EventHandler
	public void onConvertKey(PrepareItemCraftEvent event) {
		ItemStack result = event.getInventory().getResult();
		if (result == null) return;
		
		Player player = (Player) event.getInventory().getViewers().get(0);
		if (result.equals(SINGLE_KEY_RESULT)) {
			
			boolean convert = false;
			for (ItemStack item : event.getInventory().getMatrix()) {
				if (item == null) continue;
				
				if (KeyFactory.isSmithedKey(item)) {
					convert = true;
					break;
				}
			}
			
			event.getInventory().setResult((convert && player.hasPermission("locks.craft")) ? UNSMITHED_KEY : null);
		}
	}
	
}