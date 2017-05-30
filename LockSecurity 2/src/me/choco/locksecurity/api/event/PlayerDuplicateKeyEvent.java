package me.choco.locksecurity.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Called when a player duplicates a smithed key in a crafting inventory. The
 * first and second keys in this event are in no particular order, so they may
 * be either smithed or unsmithed. Do check before assuming which is which
 * 
 * @author Parker Hawke - 2008Choco
 */
public class PlayerDuplicateKeyEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final ItemStack firstKey, secondKey;
	private final int[] IDs;
	
	/**
	 * Construct a new PlayerDuplicateKeyEvent
	 * 
	 * @param player the player taking part in the event
	 * @param firstKey the first key in the crafting window
	 * @param secondKey the second key in the crafting window
	 * @param IDs the IDs being duplicated
	 */
	public PlayerDuplicateKeyEvent(Player player, ItemStack firstKey, ItemStack secondKey, int[] IDs) {
		super(player);
		this.firstKey = firstKey;
		this.secondKey = secondKey;
		this.IDs = IDs;
	}
	
	/**
	 * Get the first key involved with this duplication
	 * 
	 * @return the first key
	 */
	public ItemStack getFirstKey() {
		return firstKey;
	}
	
	/**
	 * Get the second key involved with this duplication
	 * 
	 * @return the second key
	 */
	public ItemStack getSecondKey() {
		return secondKey;
	}
	
	/**
	 * Get the ID's to be assigned 
	 * 
	 * @return the ID's
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