package wtf.choco.locksecurity.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import wtf.choco.locksecurity.api.data.ILockSecurityPlayer;
import wtf.choco.locksecurity.api.data.ILockedBlock;

/** 
 * Called when a player successfully unlocks a block
 * 
 * @author Parker Hawke - 2008Choco
 */
public class PlayerUnlockBlockEvent extends LSPlayerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	
	private final ILockedBlock block;
	private final boolean byCommand;
	
	/**
	 * Construct a new PlayerUnlockBlockEvent
	 * 
	 * @param player the player taking part in the event
	 * @param block the unlocked block
	 * @param byCommand whether it was unlocked by command or not
	 */
	public PlayerUnlockBlockEvent(ILockSecurityPlayer player, ILockedBlock block, boolean byCommand) {
		super(player);
		this.block = block;
		this.byCommand = byCommand;
	}
	
	/**
	 * Construct a new PlayerUnlockBlockEvent
	 * 
	 * @param player the player taking part in the event
	 * @param block the unlocked block
	 */
	public PlayerUnlockBlockEvent(ILockSecurityPlayer player, ILockedBlock block) {
		this(player, block, false);
	}
	
	/** 
	 * Get the block involved with this event
	 * 
	 * @return the locked block to be unlocked
	 */
	public ILockedBlock getBlock() {
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