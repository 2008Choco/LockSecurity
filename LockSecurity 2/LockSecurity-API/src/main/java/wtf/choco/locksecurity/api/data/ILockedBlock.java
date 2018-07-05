package wtf.choco.locksecurity.api.data;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.api.exception.IllegalBlockPositionException;
import wtf.choco.locksecurity.api.json.JSONSerializable;

/** 
 * Represents a block in which contains information about its owner, Lock ID, Key ID
 * and position in the world. This may take various forms as different types of blocks may be locked
 * 
 * @author Parker Hawke - 2008Choco
 */
public interface ILockedBlock extends JSONSerializable {
	
	/** 
	 * Set the owner of this block. This will also modify the {@link ILockSecurityPlayer#getOwnedBlocks()}
	 * list to remove from the old owner's blocks, and add it to the new owner's blocks
	 * 
	 * @param owner the owner to set
	 */
	public void setOwner(ILockSecurityPlayer owner);
	
	/** 
	 * Get the owner of the block
	 * 
	 * @return the owner of the block
	 */
	public ILockSecurityPlayer getOwner();
	
	/** 
	 * Check if the specified player is the owner of the block or not
	 * 
	 * @param player the player to check
	 * @return true if the player owns this block, false otherwise
	 */
	public boolean isOwner(ILockSecurityPlayer player);
	
	/**
	 * Check whether the provided player is the owner of the block or not
	 * 
	 * @param player the player to check
	 * @return true if the player owns this block, false otherwise
	 */
	public boolean isOwner(OfflinePlayer player);
	
	/**
	 * Check whether the provided player UUID is the owner of the block
	 * or not
	 * 
	 * @param player the player UUID to check
	 * @return true if the player UUID owns this block, false otherwise
	 */
	public boolean isOwner(UUID player);
	
	/** 
	 * Get the location in which this locked block is located
	 * 
	 * @return the location of the block
	 */
	public Location getLocation();
	
	/** 
	 * Get the block in which this locked block represents
	 * 
	 * @return the block this locked block represents
	 */
	public Block getBlock();
	
	/** 
	 * Get the Lock ID value that identifies this block
	 * 
	 * @return the Lock ID value
	 */
	public int getLockID();
	
	/** 
	 * Get the Key ID value required to open this block
	 * 
	 * @return the required Key ID value
	 */
	public int getKeyID();
	
	/** 
	 * Get the unique string of characters that represent this block
	 * 
	 * @return the UUID of the block
	 */
	public UUID getUniqueId();
	
	/** 
	 * Set the secondary component of this block. The secondary component MUST be
	 * of the same type and must be a valid contendor, such as DoubleChest or Door components. 
	 * This locked block and the specified component will be linked together
	 * 
	 * @param component the block to set as a secondary component
	 * @throws IllegalBlockPositionException if the block is not positioned correctly
	 */
	public void setSecondaryComponent(ILockedBlock component);
	
	/** 
	 * Set the secondary component of this block. The secondary component MUST be
	 * of the same type and must be a valid contendor, such as DoubleChest or Door components. 
	 * This locked block and the specified component will be linked together. 
	 * <br> If forced, the components will be linked regardless of their block position / state
	 * 
	 * @param component the block to set as a secondary component
	 * @param force if true, the blocks will be linked together regardless of their position
	 * 
	 * @throws IllegalBlockPositionException if the block is not positioned correctly (and "force" is false)
	 */
	public void setSecondaryComponent(ILockedBlock component, boolean force);
	
	/** 
	 * Check whether a block can successfully be a secondary component or not
	 * 
	 * @param block the block to check
	 * @return true if it can be a secondary component
	 */
	public boolean canBeSecondaryComponent(ILockedBlock block);
	
	/** 
	 * Get the secondary component for this locked block (if any)
	 * 
	 * @return the secondary component. null if none is set
	 * @see #hasSecondaryComponent()
	 */
	public ILockedBlock getSecondaryComponent();
	
	/** 
	 * Check whether this block has a secondary component or not
	 * 
	 * @return true if this block has a secondary component
	 */
	public boolean hasSecondaryComponent();
	
	/** 
	 * Check whether the specified smithedkey is a valid key or not. A key is 
	 * considered valid if its Key ID is similar to that of this block
	 * 
	 * @param key the key to check
	 * @return true if the Key ID values are similar
	 */
	public boolean isValidKey(ItemStack key);
	
}