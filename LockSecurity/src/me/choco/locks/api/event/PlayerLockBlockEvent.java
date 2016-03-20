package me.choco.locks.api.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.choco.locks.LockSecurity;

public class PlayerLockBlockEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	
	private Player player;
	private Block block;
	/** This event is fired before a player successfully locks a block
	 * @param plugin - An instance of the LockSecurity plugin
	 * @param player - The player that locked the block
	 * @param block - The block that is being locked
	 */
	public PlayerLockBlockEvent(Player player, Block block){
		this.player = player;
		this.block = block;
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
	public Block getBlock(){
		return block;
	}
	
	/** Get the LockID that this block will have
	 * @return int - The lock ID
	 */
	public int getLockID(){
		return LockSecurity.getPlugin().getLocalizedData().getNextLockID();
	}
	
	/** Get the KeyID that this block will have
	 * @return int - The key ID
	 */
	public int getKeyID(){
		return LockSecurity.getPlugin().getLocalizedData().getNextKeyID();
	}
}
