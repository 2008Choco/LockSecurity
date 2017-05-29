package me.choco.locksecurity.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.api.event.variant.LSPlayerEvent;
import me.choco.locksecurity.utils.LSPlayer;

/** 
 * Called when a player successfully unlocks a block
 * 
 * @author Parker Hawke - 2008Choco
 */
public class PlayerUnlockBlockEvent extends LSPlayerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	
	private final LockedBlock block;
	private final boolean byCommand;
	
	public PlayerUnlockBlockEvent(LSPlayer player, LockedBlock block, boolean byCommand) {
		super(player);
		this.block = block;
		this.byCommand = byCommand;
	}
	
	public PlayerUnlockBlockEvent(LSPlayer player, LockedBlock block) {
		this(player, block, false);
	}
	
	/** 
	 * Get the block involved with this event
	 * 
	 * @return the locked block to be unlocked
	 */
	public LockedBlock getBlock() {
		return block;
	}
	
	/** 
	 * Whether the block was unlocked by command or not
	 * 
	 * @return true if the block was unlocked by command
	 */
	public boolean isByCommand() {
		return byCommand;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
}