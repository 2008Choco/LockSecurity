package me.choco.locks.events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import me.choco.locks.LockSecurity;
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
		if (event.getInventory().getResult().equals(keys.createUnsmithedKey(1))){
			newIDs.clear();
			for (ItemStack item : event.getInventory().getMatrix()){
				if (isLockedKey(item)){
					List<Integer> itemIDs = lockedAccessor.getKeyIDs(item);
					for (int id : itemIDs){
						newIDs.add(id);
					}
				}else if (isUnsmithedKey(item)){
					List<Integer> itemIDs = lockedAccessor.getKeyIDs(item);
					for (int id : itemIDs){
						newIDs.add(id);
					}
					combination = false;
				}
			}
			newIDs = removeDuplicates(newIDs);
			if (combination){
				event.getInventory().setResult(keys.createLockedKey(1, newIDs));
			}else{
				event.getInventory().setResult(keys.createLockedKey(2, newIDs));
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