package me.choco.locks.events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.event.PlayerCombineKeyEvent;
import me.choco.locks.api.event.PlayerDuplicateKeyEvent;
import me.choco.locks.utils.Keys;
import me.choco.locks.utils.general.ItemBuilder;

public class CombineKeyID implements Listener{
	
	private static final ItemStack combinedKeyResult = new ItemBuilder(Material.BEDROCK).setName("COMBINE").build(),
									convertKeyResult = new ItemBuilder(Material.BEDROCK).setName("CONVERT").build();
	private static ItemStack unsmithedKey;
	
	private Keys keys;
	public CombineKeyID(LockSecurity plugin) {
		this.keys = plugin.getKeyManager();
		unsmithedKey = keys.createUnsmithedKey(1);
	}
	
	private static List<Integer> newIDs = new ArrayList<Integer>();
	
	@EventHandler
	public void onAttemptKeyCombine(PrepareItemCraftEvent event){
		if (event.getInventory().getResult() == null) return;
		
		Player player = (Player) event.getInventory().getViewers().get(0);
		boolean combination = true;
		ItemStack unsmithedKey = null;
		ItemStack smithedKey1 = null;
		ItemStack smithedKey2 = null;
		if (event.getInventory().getResult().equals(combinedKeyResult)){
			newIDs.clear();
			for (ItemStack item : event.getInventory().getMatrix()){
				if (isLockedKey(item)){
					List<Integer> itemIDs = keys.getKeyIDs(item);
					for (int id : itemIDs){
						newIDs.add(id);
					}
					if (smithedKey1 == null){
						smithedKey1 = item;
					}else{
						smithedKey2 = item;
					}
				}else if (isUnsmithedKey(item)){
					List<Integer> itemIDs = keys.getKeyIDs(item);
					for (int id : itemIDs)
						newIDs.add(id);
					combination = false;
					unsmithedKey = item;
				}
			}
			newIDs = removeDuplicates(newIDs);
			if (combination){
				PlayerCombineKeyEvent combineEvent = new PlayerCombineKeyEvent(newIDs, smithedKey1, smithedKey2);
				Bukkit.getPluginManager().callEvent(combineEvent);
				event.getInventory().setResult(keys.createLockedKey(1, newIDs));
			}else{
				PlayerDuplicateKeyEvent duplicateEvent = new PlayerDuplicateKeyEvent(newIDs, unsmithedKey, smithedKey1);
				Bukkit.getPluginManager().callEvent(duplicateEvent);
				event.getInventory().setResult(keys.createLockedKey(2, newIDs));
			}
		}
		
		else if (event.getInventory().getResult().equals(convertKeyResult)){
			boolean allowConvert = false;
			for (ItemStack item : event.getInventory().getMatrix()){
				if (isLockedKey(item)) allowConvert = true;
			}
			
			event.getInventory().setResult((allowConvert ? CombineKeyID.unsmithedKey : new ItemStack(Material.AIR)));
		}
		
		if (event.getInventory().getResult() == null) return;
		if (event.getInventory().getResult().equals(CombineKeyID.unsmithedKey)){
			if (!player.hasPermission("locks.craft")){
				event.getInventory().setResult(new ItemStack(Material.AIR));
			}
		}
	}
	
	private boolean isLockedKey(ItemStack item){
		if (item == null) return false;
		if (!item.hasItemMeta() || !item.getType().equals(Material.TRIPWIRE_HOOK)) return false;
		if (!item.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Key")) return false;
		
		return true;
	}
	
	private boolean isUnsmithedKey(ItemStack item){
		if (item == null) return false;
		if (!item.hasItemMeta() || !item.getType().equals(Material.TRIPWIRE_HOOK)) return false;
		if (!item.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Unsmithed Key")) return false;
		
		return true;
	}
	
	private List<Integer> removeDuplicates(List<Integer> numbers){
	    int size = numbers.size(), out = 0;
        
	    Set<Integer> encountered = new HashSet<Integer>();
        for (int in = 0; in < size; in++) {
            int i = numbers.get(in);
            boolean first = encountered.add(i);
            
            if (first) numbers.set(out++, i);
        }
	    
        while (out < size) numbers.remove(--size);
	    return numbers;
	}
}