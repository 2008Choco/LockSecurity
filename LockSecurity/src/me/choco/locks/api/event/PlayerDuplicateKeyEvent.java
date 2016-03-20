package me.choco.locks.api.event;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerDuplicateKeyEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	
	List<Integer> keyIDs;
	ItemStack smithedKey, unsmithedKey;
	public PlayerDuplicateKeyEvent(List<Integer> keyIDs, ItemStack unsmithedKey, ItemStack smithedKey){
		this.keyIDs = keyIDs;
		this.unsmithedKey = unsmithedKey;
		this.smithedKey = smithedKey;
	}
	
	@Override
	public HandlerList getHandlers(){
	    return handlers;
	}
	
	public static HandlerList getHandlerList(){
	    return handlers;
	}
	
	/** Get the KeyID's that are being duplicated in the result
	 * @return List - A list of key ID's 
	 */
	public List<Integer> getKeyIDs(){
		return keyIDs;
	}
	
	/** Get the unsmithed key from the crafting recipe
	 * @return ItemStack - The unsmithed key component of the recipe
	 */
	public ItemStack getUnsmithedKey(){
		return unsmithedKey;
	}
	
	/** Get the smithed key from the crafting recipe
	 * @return ItemStack - The smithed key component of the recipe
	 */
	public ItemStack getSmithedKey(){
		return smithedKey;
	}
}