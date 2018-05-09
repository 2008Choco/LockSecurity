package me.choco.locksecurity.api.event;

import me.choco.locksecurity.api.data.ILockSecurityPlayer;

import org.bukkit.event.Event;

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