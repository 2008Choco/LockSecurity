package me.choco.locks.events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.PlayerCombineKeyEvent;
import me.choco.locks.api.PlayerDuplicateKeyEvent;
import me.choco.locks.utils.Keys;
import me.choco.locks.utils.LockedBlockAccessor;

public class CombineKeyID implements Listener{
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	Keys keys;
	public CombineKeyID(LockSecurity plugin) {
		this.plugin = plugin;
		this.lockedAccessor = new LockedBlockAccessor(plugin);
		this.keys = new Keys(plugin);
	}
	
	List<Integer> newIDs = new ArrayList<Integer>();
	
	@EventHandler
	public void onAttemptKeyCombine(PrepareItemCraftEvent event){
		boolean combination = true;
		ItemStack unsmithedKey = null;
		ItemStack smithedKey1 = null;
		ItemStack smithedKey2 = null;
		if (event.getRecipe().equals(plugin.combinationRecipe)){
			newIDs.clear();
			for (ItemStack item : event.getInventory().getMatrix()){
				if (isLockedKey(item)){
					List<Integer> itemIDs = lockedAccessor.getKeyIDs(item);
					for (int id : itemIDs){
						newIDs.add(id);
					}
					if (smithedKey1 == null){
						smithedKey1 = item;
					}else{
						smithedKey2 = item;
					}
				}else if (isUnsmithedKey(item)){
					List<Integer> itemIDs = lockedAccessor.getKeyIDs(item);
					for (int id : itemIDs){
						newIDs.add(id);
					}
					combination = false;
					unsmithedKey = item;
				}
			}
			newIDs = removeDuplicates(newIDs);
			if (combination){
				PlayerCombineKeyEvent combineEvent = new PlayerCombineKeyEvent(plugin, newIDs, smithedKey1, smithedKey2);
				Bukkit.getPluginManager().callEvent(combineEvent);
				event.getInventory().setResult(keys.createLockedKey(1, newIDs));
			}else{
				PlayerDuplicateKeyEvent duplicateEvent = new PlayerDuplicateKeyEvent(newIDs, unsmithedKey, smithedKey1);
				Bukkit.getPluginManager().callEvent(duplicateEvent);
				event.getInventory().setResult(keys.createLockedKey(2, newIDs));
			}
		}
		if (event.getRecipe().equals(plugin.keyRecipe1) || event.getRecipe().equals(plugin.keyRecipe2)
				|| event.getRecipe().equals(plugin.keyRecipe3) || event.getRecipe().equals(plugin.keyRecipe4)
				|| event.getRecipe().equals(plugin.keyRecipe5) || event.getRecipe().equals(plugin.keyRecipe6)
				|| event.getRecipe().equals(plugin.keyRecipe7) || event.getRecipe().equals(plugin.keyRecipe8)){
			if (!event.getViewers().get(0).hasPermission("locks.craft")){
				event.getInventory().setResult(new ItemStack(Material.AIR));
			}
		}
	}
	
	private boolean isLockedKey(ItemStack item){
		if (item != null)
			if (item.hasItemMeta())
				if (item.getType().equals(Material.TRIPWIRE_HOOK))
					if (item.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Key"))
						return true;
		return false;
	}
	
	private boolean isUnsmithedKey(ItemStack item){
		if (item != null)
			if (item.hasItemMeta())
				if (item.getType().equals(Material.TRIPWIRE_HOOK))
					if (item.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Unsmithed Key"))
						return true;
		return false;
	}
	
	private List<Integer> removeDuplicates(List<Integer> numbers){
	    int size = numbers.size();
	    int out = 0;
	    {
	        final Set<Integer> encountered = new HashSet<Integer>();
	        for (int in = 0; in < size; in++) {
	            final Integer i = numbers.get(in);
	            final boolean first = encountered.add(i);
	            if (first) {
	                numbers.set(out++, i);
	            }
	        }
	    }
	    while (out < size) {
	        numbers.remove(--size);
	    }
	    return numbers;
	}
}