package me.choco.locksecurity.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import me.choco.locksecurity.registration.PlayerRegistry;

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
	public boolean isRegistered(Location location);
	
	/** 
	 * Check if a specific block is a registered locked block or not
	 * 
	 * @param block the block to check
	 * @return true if the block is a registered locked block
	 */
	public boolean isRegistered(Block block);
	
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
	 * @param lockID the lock id of the block to obtain
	 * @return the locked block with the given Lock ID. Null if not found
	 */
	public ILockedBlock getLockedBlock(int lockId);
	
	/** 
	 * Get a set of all currently registered locked block objects
	 * 
	 * @return a set of registered blocks
	 */
	public List<ILockedBlock> getLockedBlocks();
	
	/** 
	 * Get a set of all locked block objects with the given Key ID
	 * 
	 * @param keyID the Key ID to search
	 * @return a set of all registered blocks with the given Key ID
	 */
	public List<ILockedBlock> getLockedBlocks(int keyId);
	
	/**
	 * Get a set of all currently unloaded locked block objects
	 * 
	 * @return a set of unloaded, but registered, blocks
	 */
	public List<ILockedBlock> getUnloadedBlocks();
	
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
	 * @param type the material to check
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
	 * This information is based on registered players in the {@link PlayerRegistry}
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