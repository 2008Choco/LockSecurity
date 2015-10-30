package me.choco.locks.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.choco.locks.LockSecurity;
import me.choco.locks.utils.LockedBlockAccessor;

public class PlayerUnlockBlockEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	Player player;
	Block block;
	
	/** This event is fired before the player successfully unlocks a block
	 * @param plugin - An instance of the LockSecurity plugin
	 * @param player - The player that locked the block
	 * @param block - The block that is being locked
	 */
	public PlayerUnlockBlockEvent(LockSecurity plugin, Player player, Block block){
		this.plugin = plugin;
		this.player = player;
		this.block = block;
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
	
	/** Get the player that is unlocking the block
	 * @return Player - The player unlocking the block
	 */
	public Player getPlayer(){
		return player;
	}
	
	/** Get the block that is being unlocked
	 * @return Block - The block being unlocked
	 */
	public Block getBlock(){
		return block;
	}
	
	/** Get the LockID of the block being unlocked
	 * @return int - The lock ID
	 */
	@SuppressWarnings("deprecation")
	public int getLockID(){
		return lockedAccessor.getBlockLockID(block);
	}
	
	/** Get the KeyID of the block being unlocked
	 * @return int- The key ID
	 */
	public int getKeyID(){
		return lockedAccessor.getBlockKeyID(block);
	}
}
