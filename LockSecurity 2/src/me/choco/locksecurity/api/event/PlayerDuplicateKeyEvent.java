package me.choco.locksecurity.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDuplicateKeyEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final ItemStack firstKey, secondKey;
	private final int[] IDs;
	public PlayerDuplicateKeyEvent(Player player, ItemStack firstKey, ItemStack secondKey, int[] IDs) {
		super(player);
		this.firstKey = firstKey;
		this.secondKey = secondKey;
		this.IDs = IDs;
	}
	
	public ItemStack getFirstKey() {
		return firstKey;
	}
	
	public ItemStack getSecondKey() {
		return secondKey;
	}
	
	public int[] getIDs() {
		return IDs;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
}