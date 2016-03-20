package me.choco.locks.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.choco.locks.api.LockedBlock;
import me.choco.locks.api.utils.InteractResult;

public class PlayerInteractLockedBlockEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	
	private boolean cancelled = false;
	private Player player;
	private LockedBlock block;
	private InteractResult result;
	
	/** This event is fired before a player interacts with a locked block
	 * @param plugin - An instance of the LockSecurity plugin
	 * @param player - The player that locked the block
	 * @param block - The block that is being locked
	 * @param InteractResult - The result of the event
	 */
	public PlayerInteractLockedBlockEvent(Player player, LockedBlock block, InteractResult result){
		this.player = player;
		this.block = block;
		this.result = result;
	}
	
	@Override
	public HandlerList getHandlers(){
	    return handlers;
	}
	
	public static HandlerList getHandlerList(){
	    return handlers;
	}//Close handler
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	
	/** Get the player that is attempting to lock a block
	 * @return The player locking the block
	 */
	public Player getPlayer(){
		return player;
	}
	
	/** Get the block that is being locked in this event
	 * @return Block - The block being locked
	 */
	public LockedBlock getBlock(){
		return block;
	}
	
	public InteractResult getResult(){
		return result;
	}
}