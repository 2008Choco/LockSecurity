package me.choco.locksecurity.api.data;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import me.choco.locksecurity.api.json.JSONSerializable;
import me.choco.locksecurity.api.registration.ILockedBlockManager;
import me.choco.locksecurity.api.utils.LSMode;

import org.bukkit.OfflinePlayer;

/** 
 * A wrapper class for the OfflinePlayer interface containing information about a player's
 * LockSecurity details such as (but not limited to) their data file, owned blocks, active
 * modes ({@link LSMode}), etc.
 * <p>
 * <b>NOTE:</b> Information regarding owned locked blocks is a record of what locked blocks 
 * currently exist. Not all owned blocks are registered in the {@link ILockedBlockManager}, 
 * meaning unregistered blocks will be ignored in a locked block lookup / protection listener
 * 
 * @author Parker Hawke - 2008Choco
 */
public interface ILockSecurityPlayer extends JSONSerializable {
	
	/** 
	 * Get the {@link OfflinePlayer} instance this object represents
	 * 
	 * @return the represented player
	 */
	public OfflinePlayer getPlayer();
	
	/**
	 * Get the UUID of the represented {@link OfflinePlayer} instance
	 * 
	 * @return the represented player UUID
	 */
	public UUID getUniqueId();
	
	/** 
	 * Get an immutable set of all blocks owned by this player
	 * 
	 * @return a set of all owned blocks
	 */
	public Set<ILockedBlock> getOwnedBlocks();
	
	/** 
	 * Add a block to this player's ownership. This does not register the block to
	 * the {@link ILockedBlockManager}
	 * 
	 * @param block the block to add to ownership
	 */
	public void addBlockToOwnership(ILockedBlock block);
	
	/** 
	 * Remove a block from this players ownership
	 * 
	 * @param block the block to remove
	 */
	public void removeBlockFromOwnership(ILockedBlock block);
	
	/** 
	 * Check if the player owns the specified block or not
	 * 
	 * @param block the block to check
	 * @return true if the player owns this block
	 */
	public boolean ownsBlock(ILockedBlock block);
	
	/** 
	 * Enable a mode for this player
	 * 
	 * @param mode the mode to enable
	 */
	public void enableMode(LSMode mode);
	
	/** 
	 * Disable a mode for this player
	 * 
	 * @param mode the mode to disable
	 */
	public void disableMode(LSMode mode);
	
	/** 
	 * Toggle the enable / disable state of the specified mode for this player
	 * 
	 * @param mode the mode to toggle
	 * @return true if set enabled, false if set disabled
	 */
	public boolean toggleMode(LSMode mode);
	
	/** 
	 * Check if a mode is currently enabled for this player
	 * 
	 * @param mode the mode to check
	 * @return true if the specified mode is enabled
	 */
	public boolean isModeEnabled(LSMode mode);
	
	/** 
	 * Get an immutable set of all currently enabled modes for this player
	 * 
	 * @return a set of enabled modes
	 */
	public Set<LSMode> getEnabledModes();
	
	/**
	 * Set the player that this player is going to transfer the selected block to
	 * 
	 * @param target the player to transfer to
	 */
	public void setTransferTarget(ILockSecurityPlayer target);
	
	/**
	 * Get the player that will receive any transferred blocks from this player
	 * 
	 * @return the transfer target
	 */
	public ILockSecurityPlayer getTransferTarget();
	
	/** 
	 * Get the JSON data file that keeps track of offline information for this user
	 * 
	 * @return the player's JSON data file
	 */
	public File getJSONDataFile();
	
	/**
	 * Clear all localized data for this player
	 */
	public void clearLocalData();
	
}