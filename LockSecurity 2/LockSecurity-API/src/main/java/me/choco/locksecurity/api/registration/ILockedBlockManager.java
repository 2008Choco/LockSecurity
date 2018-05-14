package me.choco.locksecurity.api.registration;

import java.util.Set;

import me.choco.locksecurity.api.data.ILockSecurityPlayer;
import me.choco.locksecurity.api.data.ILockedBlock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/** 
 * The manager that keeps track of registered locked blocks and their information.
 * The registration of locked blocks does not mean that they do not exist. The existence
 * of locked blocks is contained within the {@link ILockSecurityPlayer} object.
 * <p>
 * <b>NOTE:</b> A registered locked block simply means that its information has been loaded. 
 * Information of a locked block may be loaded and unloaded in this manager at any time
 * such that it still exists in the owner's LockSecurityPlayer object. Registration of locked
 * blocks should only be done if the world in which it is possessed is loaded. If the world
 * is unloaded, the locked block should also be unregistered to provide more efficient
 * lookups in LockSecurity code.
 * 
 * @author Parker Hawke - 2008Choco
 */
public interface ILockedBlockManager {
	
	/** 
	 * Register a locked block to the manager
	 * 
	 * @param block the block to register
	 */
	public void registerBlock(ILockedBlock block);
	
	/** 
	 * Unregister a locked block from the manager
	 * 
	 * @param block the block to unregister
	 */
	public void unregisterBlock(ILockedBlock block);
	
	/** 
	 * Check if a specific locked block is registered or not
	 * 
	 * @param block the block to check
	 * @return true if the block is registered
	 */
	public boolean isRegistered(ILockedBlock block);
	
	/** 
	 * Check if a specific location possesses a registered locked block or not
	 * 
	 * @param location the location to check
	 * @return true if a block is registered in the specified location
	 */
	public boolean isLockedBlock(Location location);
	
	/** 
	 * Check if a specific block is a registered locked block or not
	 * 
	 * @param block the block to check
	 * @return true if the block is a registered locked block
	 */
	public boolean isLockedBlock(Block block);
	
	/**
	 * Get a locked block from the registry based on a location
	 * 
	 * @param location the location to get the block from
	 * @return the locked block object in the specified location. null if none found
	 */
	public ILockedBlock getLockedBlock(Location location);
	
	/** 
	 * Get a locked block from the registry
	 * 
	 * @param block the block in which to receive a locked block from
	 * @return the locked block object. null if none found
	 */
	public ILockedBlock getLockedBlock(Block block);
	
	/** 
	 * Get a locked block from Lock ID
	 * 
	 * @param lockId the lock id of the block to obtain
	 * @return the locked block with the given Lock ID. Null if not found
	 */
	public ILockedBlock getLockedBlock(int lockId);
	
	/**
	 * Create a new lock at the given location with a specified lock ID and key ID, and assign a
	 * secondary component
	 * 
	 * @param owner the owner of the locked block
	 * @param location the location at which the block is positioned
	 * @param lockId the ID of the lock
	 * @param keyId the ID of the key required to open this block
	 * @param secondaryComponent the secondary component to this block
	 * 
	 * @return the newly created locked block
	 * 
	 * @see #createNewLock(ILockSecurityPlayer, Location, ILockedBlock)
	 */
	public ILockedBlock createNewLock(ILockSecurityPlayer owner, Location location, int lockId, int keyId, ILockedBlock secondaryComponent);
	
	/**
	 * Create a new lock at the given location with a specified lock ID and key ID
	 * 
	 * @param owner the owner of the locked block
	 * @param location the location at which the block is positioned
	 * @param lockId the ID of the lock
	 * @param keyId the ID of the key required to open this block
	 * 
	 * @return the newly created locked block
	 * 
	 * @see #createNewLock(ILockSecurityPlayer, Location)
	 */
	public ILockedBlock createNewLock(ILockSecurityPlayer owner, Location location, int lockId, int keyId);
	
	/**
	 * Create a new lock at the given location and assign a secondary component. Lock and key IDs
	 * will be automatically generated according to {@link #getNextLockID()} and {@link #getNextKeyID()}
	 * with incremented values enabled
	 * 
	 * @param owner the owner of the locked block
	 * @param location the location at which the block is positioned
	 * @param secondaryComponent the secondary component to this block
	 * 
	 * @return the newly created locked block
	 */
	public ILockedBlock createNewLock(ILockSecurityPlayer owner, Location location, ILockedBlock secondaryComponent);
	
	/**
	 * Create a new lock at the given location. Lock and key IDs will be automatically generated
	 * according to {@link #getNextLockID()} and {@link #getNextKeyID()} with incremented values
	 * enabled
	 * 
	 * @param owner the owner of the locked lock
	 * @param location the location at which the block is positioned
	 * 
	 * @return the newly created locked block
	 */
	public ILockedBlock createNewLock(ILockSecurityPlayer owner, Location location);
	
	/**
	 * Create a new lock for the provided block with a specified lock ID and key ID, and assign a
	 * secondary component
	 * 
	 * @param owner the owner of the locked block
	 * @param block the block to lock
	 * @param lockId the ID of the lock
	 * @param keyId the ID of the key required to open this block
	 * @param secondaryComponent the secondary component to this block
	 * 
	 * @return the newly created locked block
	 * 
	 * @see #createNewLock(ILockSecurityPlayer, Block, ILockedBlock)
	 */
	public ILockedBlock createNewLock(ILockSecurityPlayer owner, Block block, int lockId, int keyId, ILockedBlock secondaryComponent);
	
	/**
	 * Create a new lock for the provided block with a specified lock ID and key ID
	 * 
	 * @param owner the owner of the locked block
	 * @param block the block to lock
	 * @param lockId the ID of the lock
	 * @param keyId the ID of the key required to open this block
	 * 
	 * @return the newly created locked block
	 * 
	 * @see #createNewLock(ILockSecurityPlayer, Block)
	 */
	public ILockedBlock createNewLock(ILockSecurityPlayer owner, Block block, int lockId, int keyId);
	
	/**
	 * Create a new lock for the provided block and assign a secondary component. Lock and key IDs
	 * will be automatically generated according to {@link #getNextLockID()} and {@link #getNextKeyID()}
	 * with incremented values enabled
	 * 
	 * @param owner the owner of the locked block
	 * @param block the block to lock
	 * @param secondaryComponent the secondary component to this block
	 * 
	 * @return the newly created locked block
	 */
	public ILockedBlock createNewLock(ILockSecurityPlayer owner, Block block, ILockedBlock secondaryComponent);
	
	/**
	 * Create a new lock for the provided block. Lock and key IDs will be automatically generated
	 * according to {@link #getNextLockID()} and {@link #getNextKeyID()} with incremented values
	 * enabled
	 * 
	 * @param owner the owner of the locked block
	 * @param block the block to lock
	 * 
	 * @return the newly created locked block
	 */
	public ILockedBlock createNewLock(ILockSecurityPlayer owner, Block block);
	
	/** 
	 * Get a set of all currently registered locked block objects
	 * 
	 * @return a set of registered blocks
	 */
	public Set<ILockedBlock> getLockedBlocks();
	
	/** 
	 * Get a set of all locked block objects with the given Key ID
	 * 
	 * @param keyId the Key ID to search
	 * @return a set of all registered blocks with the given Key ID
	 */
	public Set<ILockedBlock> getLockedBlocks(int keyId);
	
	/**
	 * Get a set of all currently unloaded locked block objects
	 * 
	 * @return a set of unloaded, but registered, blocks
	 */
	public Set<ILockedBlock> getUnloadedBlocks();
	
	/** 
	 * Check if a block is lockable or not
	 * 
	 * @param block the block to check
	 * @return true if it is lockable
	 */
	public boolean isLockable(Block block);
	
	/** 
	 * Check if a material is lockable or not
	 * 
	 * @param material the material to check
	 * @return true if it is lockable
	 */
	public boolean isLockable(Material material);
	
	/** 
	 * Get the next unique lock ID that should be used without incrementing 
	 * its value
	 * 
	 * @return the next lock ID
	 */
	public int getNextLockID();
	
	/** 
	 * Get the next unique lock ID that should be used
	 * 
	 * @param increment whether the next lock ID should increment or not
	 * @return the next lock ID
	 */
	public int getNextLockID(boolean increment);
	
	/** 
	 * Get the next unique key ID that should be used without incrementing 
	 * its value
	 * 
	 * @return the next key ID
	 */
	public int getNextKeyID();
	
	/** 
	 * Get the next unique key ID that should be used
	 * 
	 * @param increment whether the next key ID should increment or not
	 * @return the next key ID
	 */
	public int getNextKeyID(boolean increment);
	
	/** 
	 * Load all existing locked blocks contained in the specified world.
	 * This information is based on registered players in the {@link IPlayerRegistry}
	 * 
	 * @param world the world to load from
	 */
	public void loadDataForWorld(World world);
	
	/** 
	 * Unload all registered locked blocks contained in the specified world.
	 * 
	 * @param world the world to unload from
	 */
	public void unloadDataForWorld(World world);
	
	/**
	 * Clear all block data from memory
	 */
	public void clearLockedBlockData();
	
}