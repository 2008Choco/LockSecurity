package me.choco.locksecurity.api.event.variant;

import org.bukkit.event.Event;

import me.choco.locksecurity.api.ILockSecurityPlayer;

/** 
 * Represents an LSPlayer based event
 * 
 * @author Parker Hawke - 2008Choco
 */
public abstract class LSPlayerEvent extends Event {
	
	private final ILockSecurityPlayer player;
	
	/**
	 * Construct a new LSPlayerEvent
	 * 
	 * @param player the player involved in the event
	 */
	public LSPlayerEvent(ILockSecurityPlayer player) {
		this.player = player;
	}
	
	/** 
	 * Get the player in which contributed to this event
	 * 
	 * @return the player
	 */
	public ILockSecurityPlayer getPlayer() {
		return player;
	}
	
}