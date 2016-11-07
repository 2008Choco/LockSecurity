package me.choco.locksecurity.utils.general;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/** A class to assist those in creating ItemStacks in the confines of a single line of code
 * This class removes the need to get ItemMeta
 * @author Parker Hawke - 2008Choco
 * @since 3/30/2016 - March 30th, 2016
 */
public class ItemBuilder {
	
	private ItemStack item;
	private ItemMeta meta;
	public ItemBuilder(Material material){
		this(material, (short) 0);
	}
	
	public ItemBuilder(Material material, short durability){
		this.item = new ItemStack(material, 1, durability);
		this.meta = item.getItemMeta();
	}
	
	public ItemBuilder(Material material, byte data){
		this.item = new ItemStack(material, 1, data);
		this.meta = item.getItemMeta();
	}
	
	public ItemBuilder setName(String name){
		meta.setDisplayName(name);
		return this;
	}
	
	public ItemBuilder setLore(List<String> lore){
		meta.setLore(lore);
		return this;
	}
	
	public ItemBuilder setAmount(int amount){
		item.setAmount(amount);
		return this;
	}
	
	public ItemBuilder addFlags(ItemFlag... flags){
		meta.addItemFlags(flags);
		return this;
	}
	
	public ItemBuilder addEnchantment(Enchantment enchantment, int level){
		meta.addEnchant(enchantment, level, true);
		return this;
	}
	
	public ItemBuilder setUnbreakable(boolean unbreakable){
		meta.spigot().setUnbreakable(unbreakable);
		return this;
	}
	
	public ItemStack build(){
		item.setItemMeta(meta);
		return item;
	}
}