package me.choco.locksecurity.api.event.variant;

import org.bukkit.event.Event;

import me.choco.locksecurity.utils.LSPlayer;

/** Represents an LSPlayer based event
 * @author Parker Hawke - 2008Choco
 */
public abstract class LSPlayerEvent extends Event {

	private LSPlayer player;
	public LSPlayerEvent(LSPlayer player) {
		this.player = player;
	}
	
	/** Get the player in which contributed to this event
	 * @return the player
	 */
	public LSPlayer getPlayer(){
		return player;
	}
}