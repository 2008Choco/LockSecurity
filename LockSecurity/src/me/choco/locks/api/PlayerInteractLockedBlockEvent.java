package me.choco.locks.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.utils.InteractResult;
import me.choco.locks.utils.LockedBlockAccessor;

public class PlayerInteractLockedBlockEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	Player player;
	Block block;
	InteractResult result;
	
	/** This event is fired before a player interacts with a locked block
	 * @param plugin - An instance of the LockSecurity plugin
	 * @param player - The player that locked the block
	 * @param block - The block that is being locked
	 * @param InteractResult - The result of the event
	 */
	public PlayerInteractLockedBlockEvent(LockSecurity plugin, Player player, Block block, InteractResult result){
		this.plugin = plugin;
		this.player = player;
		this.block = block;
		this.result = result;
		this.lockedAccessor = new LockedBlockAccessor(plugin);
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
		return lockedAccessor.getNextLockID();
	}
	
	/** Get the KeyID that this block will have
	 * @return int - The key ID
	 */
	public int getKeyID(){
		return lockedAccessor.getNextKeyID();
	}
	
	public InteractResult getResult(){
		return result;
	}
}