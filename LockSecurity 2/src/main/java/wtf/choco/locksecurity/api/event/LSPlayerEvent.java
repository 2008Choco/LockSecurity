package wtf.choco.locksecurity.api.event;

import org.bukkit.event.Event;

import wtf.choco.locksecurity.data.LockSecurityPlayer;

/**
 * Represents an LSPlayer based event
 * 
 * @author Parker Hawke - 2008Choco
 */
public abstract class LSPlayerEvent extends Event {
	
	private final LockSecurityPlayer player;
	
	/**
	 * Construct a new LSPlayerEvent
	 * 
	 * @param player the player involved in the event
	 */
	public LSPlayerEvent(LockSecurityPlayer player) {
		this.player = player;
	}
	
	/**
	 * Get the player in which contributed to this event
	 * 
	 * @return the player
	 */
	public LockSecurityPlayer getPlayer() {
		return player;
	}
	
}