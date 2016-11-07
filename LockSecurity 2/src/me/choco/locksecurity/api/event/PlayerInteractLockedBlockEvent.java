package me.choco.locksecurity.api.event;

import org.bukkit.event.HandlerList;

import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.api.event.variant.LSPlayerEvent;
import me.choco.locksecurity.utils.LSPlayer;

/** Called when a player interacts with a locked block. Interactions may include: 
 * <br>
 * <br> - Opening a locked block
 * <br> - Attempting to open a locked block without a key
 * <br> - Attempting to open a locked block with the wrong key
 * @author Parker Hawke - 2008Choco
 */
public class PlayerInteractLockedBlockEvent extends LSPlayerEvent {
	
	public enum InteractResult {
		SUCCESS, NO_KEY, NOT_RIGHT_KEY;
	}
	
	private static final HandlerList handlers = new HandlerList();
	
	private final LockedBlock block;
	private final InteractResult result;
	public PlayerInteractLockedBlockEvent(LSPlayer player, LockedBlock block, InteractResult result) {
		super(player);
		this.block = block;
		this.result = result;
	}
	
	/** Get the locked block involved with this event
	 * @return the interacted locked block
	 */
	public LockedBlock getBlock() {
		return block;
	}
	
	/** Get the result of the interaction
	 * @return the result
	 */
	public InteractResult getResult() {
		return result;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
}