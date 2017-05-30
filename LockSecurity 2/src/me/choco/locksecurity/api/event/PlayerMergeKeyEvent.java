package me.choco.locksecurity.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Called when a player merges two smithed keys together and creates a new key 
 * with the combined numerical ID's
 * 
 * @author Parker Hawke - 2008Choco
 */
public class PlayerMergeKeyEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final ItemStack firstKey, secondKey;
	private final int[] IDs;
	
	/**
	 * Construct a new PlayerMergeKeyEvent
	 * 
	 * @param player the player taking part in the event
	 * @param firstKey the first key in the crafting window
	 * @param secondKey the second key in the crafting window
	 * @param IDs the new ID's (two keys combined)
	 */
	public PlayerMergeKeyEvent(Player player, ItemStack firstKey, ItemStack secondKey, int[] IDs) {
		super(player);
		this.firstKey = firstKey;
		this.secondKey = secondKey;
		this.IDs = IDs;
	}
	
	/**
	 * Get the first key involved with this merge
	 * 
	 * @return the first key
	 */
	public ItemStack getFirstKey() {
		return firstKey;
	}
	
	/**
	 * Get the second key involved with this merge
	 * 
	 * @return the second key
	 */
	public ItemStack getSecondKey() {
		return secondKey;
	}
	
	/**
	 * Get the new ID's to be assigned to the merged key
	 * 
	 * @return the new ID's
	 */
	public int[] getIDs() {
		return IDs;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}