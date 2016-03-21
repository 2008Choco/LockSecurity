package me.choco.locks.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.choco.locks.LockSecurity;

public class Keys {
	LockSecurity plugin;
	public Keys(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	public ItemStack createUnsmithedKey(int count){
		ItemStack unsmithedKey = new ItemStack(Material.TRIPWIRE_HOOK, count);
		ItemMeta unsmithedMeta = unsmithedKey.getItemMeta();
		unsmithedMeta.setDisplayName(ChatColor.GRAY + "Unsmithed Key");
		unsmithedMeta.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GRAY + "Key ID: " + ChatColor.DARK_PURPLE + "N/A")));
		unsmithedKey.setItemMeta(unsmithedMeta);
		return unsmithedKey;
	}
	
	/** Create a locked key with the specified ID's
	 * This will return a whole new item stack rather than converting an unsmithed key
	 * @param count - The amount of keys to create
	 * @param ID - The ID that the key will be binded to
	 * @return ItemStack - A new locked key item
	 */
	public ItemStack createLockedKey(int count, int ID){
		ItemStack lockedKey = new ItemStack(Material.TRIPWIRE_HOOK, count);
		ItemMeta lockedMeta = lockedKey.getItemMeta();
		lockedMeta.setDisplayName(ChatColor.GRAY + "Key");
		lockedMeta.addEnchant(Enchantment.DURABILITY, 10, true);
		lockedMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		lockedMeta.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GRAY + "Key ID: " + ChatColor.DARK_PURPLE + ID)));
		lockedKey.setItemMeta(lockedMeta);
		return lockedKey;
	}
	
	/** Create a locked key with the specified ID's
	 * This will return a whole new item stack rather than converting an unsmithed key
	 * @param count - The amount of key's to create
	 * @param IDs - The ID's that the keys will be binded to
	 * @return ItemStack - A new locked key item (with multiple ID's)
	 */
	public ItemStack createLockedKey(int count, List<Integer> IDs){
		ItemStack lockedKey = new ItemStack(Material.TRIPWIRE_HOOK, count);
		ItemMeta lockedMeta = lockedKey.getItemMeta();
		lockedMeta.setDisplayName(ChatColor.GRAY + "Key");
		lockedMeta.addEnchant(Enchantment.DURABILITY, 10, true);
		lockedMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		lockedMeta.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GRAY + "Key ID: " + ChatColor.DARK_PURPLE + IDs.toString().replaceAll("\\[", "").replaceAll("\\]", ""))));
		lockedKey.setItemMeta(lockedMeta);
		return lockedKey;
	}
	
	/**
	 * @param key - The key in which to convert to a key with a binded id
	 * @param ID - The ID in which will be binded to the key
	 * @return ItemStack - The converted key
	 */
	public ItemStack convertToLockedKey(ItemStack key, int ID){
		ItemMeta keyMeta = key.getItemMeta();
		keyMeta.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GRAY + "Key ID: " + ChatColor.DARK_PURPLE + ID)));
		keyMeta.setDisplayName(ChatColor.GRAY + "Key");
		keyMeta.addEnchant(Enchantment.DURABILITY, 10, true);
		keyMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		key.setItemMeta(keyMeta);
		return key;
	}
	
	/**
	 * @param key - The key in which to convert to a key with a binded id
	 * @param IDs - The ID's in which will be binded to the key (Multiple ID's)
	 * @return ItemStack - The converted key
	 */
	public ItemStack convertToLockedKey(ItemStack key, List<Integer> IDs){
		ItemMeta keyMeta = key.getItemMeta();
		keyMeta.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GRAY + "Key ID: " + ChatColor.DARK_PURPLE + IDs.toString().replaceAll("\\[", "").replaceAll("\\]", ""))));
		keyMeta.setDisplayName(ChatColor.GRAY + "Key");
		keyMeta.addEnchant(Enchantment.DURABILITY, 10, true);
		keyMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		key.setItemMeta(keyMeta);
		return key;
	}
	
	/**
	 * @param player The player to check
	 * @return Whether the player has an unsmithed key in hand
	 */
	public boolean playerHasUnsmithedKey(Player player){
		if (player.getInventory().getItemInMainHand().isSimilar(createUnsmithedKey(1)))
			return true;
		return false;
	}
	
	/** A boolean method to determine whether the Key ID matches the Block Lock ID
	 * @param block - The block to reference an ID from
	 * @param player - The player that is right clicking the locked block
	 * @return boolean - Whether the player has the right key in their hand or not
	 */
	public boolean playerHasCorrectKey(Block block, Player player){
		if (!plugin.getLocalizedData().isLockedBlock(block)) return true;
		if (player.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK)){
			List<Integer> keyIDs = getKeyIDs(player.getInventory().getItemInMainHand());
			if (keyIDs == null) return false;
			return keyIDs.contains(plugin.getLocalizedData().getLockedBlock(block).getKeyId());
		}
		return false;
	}
	
	/** Get the IDs of the Key in the players hand
	 * @param player - The player to reference the item in hand
	 * @return String - String value of the ID binded to the key
	 */
	public List<Integer> getKeyIDs(ItemStack key){
		if (!key.getType().equals(Material.TRIPWIRE_HOOK)) return null;
		if (!key.hasItemMeta()) return null;
		if (!key.getItemMeta().hasLore()) return null;
		String[] ids = key.getItemMeta().getLore().toString().replace("Key ID: ", "").replaceAll("\\[", "").replaceAll("\\]", "").split(", ");
		
		List<Integer> intIDs = new ArrayList<Integer>();
		for (String currentID : ids){
			try{ intIDs.add(Integer.parseInt(ChatColor.stripColor(currentID)));
			}catch(NumberFormatException e){ continue; }
		}
		return intIDs;
	}
}