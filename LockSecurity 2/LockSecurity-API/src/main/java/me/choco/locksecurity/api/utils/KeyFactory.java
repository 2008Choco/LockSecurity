package me.choco.locksecurity.api.utils;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/** 
 * A utility class to assist with the creation, manipulation and information of LockSecurity keys
 * 
 * @author Parker Hawke - 2008Choco
 */
public class KeyFactory {
	
	private static final ItemStack UNSMITHED_KEY = KeyFactory.buildKey(KeyType.UNSMITHED).build();
	
	/** 
	 * Build a new key. Different types of keys have different limitations in the {@link KeyBuilder}
	 * 
	 * @param type the type of key to build
	 * @return a new KeyBuilder instance to create the key
	 */
	public static KeyBuilder buildKey(KeyType type) {
		Preconditions.checkArgument(type != null, "Cannot create a key of type null");
		return new KeyBuilder(type);
	}
	
	/** 
	 * Get a singular unsmithed key. A method to simplify the KeyBuilder 
	 * aspect of {@link KeyType#UNSMITHED}
	 * 
	 * @return 1 unsmithed key
	 */
	public static ItemStack getUnsmithedkey() {
		return UNSMITHED_KEY.clone();
	}
	
	/** 
	 * Get a certain amount of unsmithed keys. A method to simplify the KeyBuilder 
	 * aspect of {@link KeyType#UNSMITHED}
	 * 
	 * @param amount the amount of unsmithed keys to create
	 * @return unsmithed keys
	 */
	public static ItemStack getUnsmithedKey(int amount) {
		Preconditions.checkArgument(amount > 0, "Cannot create an ItemStack with a negative or zero amount (%s)", amount);
		
		ItemStack key = UNSMITHED_KEY.clone();
		key.setAmount(amount);
		return key;
	}
	
	/** 
	 * Check if the specified item is an unsmithed key or not
	 * 
	 * @param item the item to check
	 * @return true if the item is an unsmithed key
	 */
	public static boolean isUnsmithedKey(ItemStack item) {
		return UNSMITHED_KEY.isSimilar(item);
	}
	
	/** 
	 * Check if the specified item is a smithed key or not
	 * 
	 * @param item the item to check
	 * @return true if the item is a smithed key
	 */
	public static boolean isSmithedKey(ItemStack item) {
		return getIDs(item).length > 0;
	}
	
	/** 
	 * Get an array of all ID's engraved on a smithed key. If the specified item
	 * is not a smithed key, or an issue arises in the gathering of ID's, an empty
	 * integer array will be returned.
	 * 
	 * @param key the key to get the ID's from
	 * @return an array of all ID's on the key
	 */
	public static int[] getIDs(ItemStack key) {
		if (key == null || !key.hasItemMeta() || !key.getItemMeta().hasLore()) return new int[0];
		List<String> lore = key.getItemMeta().getLore();
		
		for (String line : lore) {
			line = ChatColor.stripColor(line);
			if (!line.startsWith("Key ID: ")) continue;
			
			String[] stringIDs = line.replace("Key ID: ", "").split(", ");
			return Arrays.stream(stringIDs).mapToInt(Integer::parseInt).filter(i -> i > 0).distinct().toArray();
		}
		
		return new int[0];
	}
	
	/** 
	 * Combine two smithed keys together to create multiple smithed key with the combined Key ID values. 
	 * Values will be sorted in incremental order, and any duplicate values will be removed.
	 * If either of the keys are not smithed keys, the returned value will be null. 
	 * 
	 * @param key1 the first smithed key to merge
	 * @param key2 the second smithed key to merge
	 * @param amount the amount of keys to create
	 * 
	 * @return keys with merged Key ID values. null if neither are smithed keys
	 */
	public static ItemStack mergeKeys(ItemStack key1, ItemStack key2, int amount) {
		Preconditions.checkNotNull(key1, "Cannot merge null key (key1)");
		Preconditions.checkNotNull(key2, "Cannot merge null key (key2)");
		Preconditions.checkArgument(amount >= 1, "Cannot create ItemStack with negative amount");
		
		// Add them up and sort in ascending order
		int[] newIDs = ArrayUtils.addAll(getIDs(key1), getIDs(key2));
		newIDs = Arrays.stream(newIDs).distinct().sorted().toArray();
		return KeyFactory.buildKey(KeyType.SMITHED).withIDs(newIDs).setAmount(amount).build();
	}
	
	/** 
	 * Combine two smithed keys together to create a singular smithed key with the combined Key ID values. 
	 * Values will be sorted in incremental order, and any duplicate values will be removed.
	 * If either of the keys are not smithed keys, the returned value will be null. 
	 * 
	 * @param key1 the first smithed key to merge
	 * @param key2 the second smithed key to merge
	 * 
	 * @return a singular key with merged Key ID values. null if neither are smithed keys
	 */
	public static ItemStack mergeKeys(ItemStack key1, ItemStack key2) {
		return mergeKeys(key1, key2, 1);
	}
	
	
	// KEY BUILDER UTILITY CLASSES
	
	/** 
	 * A utility class to assist in the creation of LockSecurity keys
	 * 
	 * @author Parker Hawke - 2008Choco
	 */
	public static class KeyBuilder {
		
		private final ItemBuilder keyBuilder;
		private final boolean smithed;
		
		private KeyBuilder(KeyType type) {
			this.smithed = type.allowDataModifications();
			
			this.keyBuilder = new ItemBuilder(Material.TRIPWIRE_HOOK);
			this.keyBuilder.setName(ChatColor.GRAY + type.getItemDisplayName());
			
			if (type.equals(KeyType.SMITHED)) {
				this.keyBuilder.addEnchantment(Enchantment.DURABILITY, 10).addFlags(ItemFlag.HIDE_ENCHANTS);
			}
			else {
				this.keyBuilder.setLore(Arrays.asList(ChatColor.GRAY + "Key ID: " + ChatColor.DARK_PURPLE + "N/A"));
			}
		}
		
		/** 
		 * Set the amount of keys to be returned
		 * 
		 * @param amount the amount of keys
		 * @return this instance of the builder. Allows for chaining
		 */
		public KeyBuilder setAmount(int amount) {
			this.keyBuilder.setAmount(amount);
			return this;
		}
		
		/** 
		 * Apply the specified Key ID's to the key
		 * <br><b>NOTE:</b> This method can only be used on {@link KeyType#SMITHED}
		 * 
		 * @param IDs the ID's to add 
		 * @return this instance of the builder. Allows for chaining
		 */
		public KeyBuilder withIDs(int... IDs) {
			Preconditions.checkArgument(smithed, "Cannot modify data of an unsmithed key");
			if (IDs.length == 0) return this;
			
			Arrays.sort(IDs);
			StringBuilder stringIDs = new StringBuilder();
			for (int id : IDs)
				stringIDs.append(", " + id);
			
			if (stringIDs.length() < 2) return this;
			
			this.keyBuilder.setLore(Arrays.asList(ChatColor.GRAY + "Key ID: " + ChatColor.DARK_PURPLE + stringIDs.toString().substring(2)));
			return this;
		}
		
		/** 
		 * Build the key and create a final ItemStack out of it
		 * 
		 * @return the finalized ItemStack (key)
		 */
		public ItemStack build() {
			return keyBuilder.build();
		}
	}
	
	/** 
	 * A constant representation of the two types of keys
	 * 
	 * @author Parker Hawke - 2008Choco
	 */
	public enum KeyType {
		
		/** 
		 * A key without specified Key ID values. May be used to lock blocks 
		 */
		UNSMITHED("Unsmithed Key", false),
		
		/** 
		 * A key with Key ID values. May be used to open locked blocks 
		 */
		SMITHED("Smithed Key", true);
		
		private final String itemDisplayName;
		private final boolean dataModifications;
		
		private KeyType(String itemDisplayName, boolean dataModifications) {
			this.itemDisplayName = itemDisplayName;
			this.dataModifications = dataModifications;
		}
		
		/** 
		 * Get the name of the key that should be displayed on the ItemStack
		 * 
		 * @return the name to display
		 */
		public String getItemDisplayName() {
			return itemDisplayName;
		}
		
		/** 
		 * Whether or not Key ID values are able to be applied to this key type or not
		 * 
		 * @return true if Key ID values are applicable
		 */
		public boolean allowDataModifications() {
			return dataModifications;
		}
	}
	
}