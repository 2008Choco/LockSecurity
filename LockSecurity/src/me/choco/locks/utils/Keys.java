package me.choco.locks.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
		if (player.getItemInHand().equals(createUnsmithedKey(1)))
			return true;
		return false;
	}
}