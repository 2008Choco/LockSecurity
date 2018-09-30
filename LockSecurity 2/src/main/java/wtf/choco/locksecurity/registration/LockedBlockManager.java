package wtf.choco.locksecurity.registration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.data.LockSecurityPlayer;
import wtf.choco.locksecurity.data.LockedBlock;

/**
 * The manager that keeps track of registered locked blocks and their information.
 * The registration of locked blocks does not mean that they do not exist. The existence
 * of locked blocks is contained within the {@link LockSecurityPlayer} object.
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
public class LockedBlockManager {
	
	private int nextLockID = -1, nextKeyID = -1;
	
	private final Set<LockedBlock> lockedBlocks = new HashSet<>(), unloadedBlocks = new HashSet<>();
	private final PlayerRegistry playerRegistry;
	private final LockSecurity plugin;
	
	/**
	 * Construct a new LockedBlockManager. There should be need for one 1 manager
	 * 
	 * @param plugin the LockSecurity plugin
	 */
	public LockedBlockManager(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
		
		// Read Lock / Key ID values
		try (BufferedReader reader = new BufferedReader(new FileReader(plugin.infoFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] values = line.split("=");
				if (values[0].equalsIgnoreCase("nextLockID")) {
					this.nextLockID = Integer.parseInt(values[1]);
				} else if (values[0].equalsIgnoreCase("nextKeyID")) {
					this.nextKeyID = Integer.parseInt(values[1]);
				}
			}
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Register a locked block to the manager
	 * 
	 * @param block the block to register
	 */
	public void registerBlock(LockedBlock block) {
		Preconditions.checkArgument(block != null, "Cannot register a null block");
		this.lockedBlocks.add(block);
	}
	
	/**
	 * Unregister a locked block from the manager
	 * 
	 * @param block the block to unregister
	 */
	public void unregisterBlock(LockedBlock block) {
		this.lockedBlocks.remove(block);
	}
	
	/**
	 * Check if a specific locked block is registered or not
	 * 
	 * @param block the block to check
	 * @return true if the block is registered
	 */
	public boolean isRegistered(LockedBlock block) {
		return lockedBlocks.contains(block);
	}
	
	/**
	 * Check if a specific location possesses a registered locked block or not
	 * 
	 * @param location the location to check
	 * @return true if a block is registered in the specified location
	 */
	public boolean isLockedBlock(Location location) {
		return getLockedBlock(location) != null;
	}
	
	/**
	 * Check if a specific block is a registered locked block or not
	 * 
	 * @param block the block to check
	 * @return true if the block is a registered locked block
	 */
	public boolean isLockedBlock(Block block) {
		return getLockedBlock(block) != null;
	}
	
	/**
	 * Get a locked block from the registry based on a location
	 * 
	 * @param location the location to get the block from
	 * @return the locked block object in the specified location. null if none found
	 */
	public LockedBlock getLockedBlock(Location location) {
		return getLockedBlock(location.getBlock());
	}
	
	/**
	 * Get a locked block from the registry
	 * 
	 * @param block the block in which to receive a locked block from
	 * @return the locked block object. null if none found
	 */
	public LockedBlock getLockedBlock(Block block) {
		for (LockedBlock lBlock : this.lockedBlocks)
			if (lBlock.getBlock().equals(block)) return lBlock;
		return null;
	}
	
	/**
	 * Get a locked block from Lock ID
	 * 
	 * @param lockId the lock id of the block to obtain
	 * @return the locked block with the given Lock ID. Null if not found
	 */
	public LockedBlock getLockedBlock(int lockId) {
		return this.lockedBlocks.stream()
			.filter(b -> b.getLockID() == lockId)
			.findFirst().orElse(null);
	}
	
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
	 * @see #createNewLock(LockSecurityPlayer, Location, LockedBlock)
	 */
	public LockedBlock createNewLock(LockSecurityPlayer owner, Location location, int lockId, int keyId, LockedBlock secondaryComponent) {
		return new LockedBlock(owner, location, lockId, keyId, secondaryComponent);
	}
	
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
	 * @see #createNewLock(LockSecurityPlayer, Location)
	 */
	public LockedBlock createNewLock(LockSecurityPlayer owner, Location location, int lockId, int keyId) {
		return new LockedBlock(owner, location, lockId, keyId);
	}

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
	public LockedBlock createNewLock(LockSecurityPlayer owner, Location location, LockedBlock secondaryComponent) {
		return createNewLock(owner, location, getNextLockID(true), getNextKeyID(true), secondaryComponent);
	}

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
	public LockedBlock createNewLock(LockSecurityPlayer owner, Location location) {
		return createNewLock(owner, location, getNextLockID(true), getNextKeyID(true));
	}

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
	 * @see #createNewLock(LockSecurityPlayer, Block, LockedBlock)
	 */
	public LockedBlock createNewLock(LockSecurityPlayer owner, Block block, int lockId, int keyId, LockedBlock secondaryComponent) {
		return createNewLock(owner, block.getLocation(), lockId, keyId, secondaryComponent);
	}
	
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
	 * @see #createNewLock(LockSecurityPlayer, Block)
	 */
	public LockedBlock createNewLock(LockSecurityPlayer owner, Block block, int lockId, int keyId) {
		return createNewLock(owner, block.getLocation(), lockId, keyId);
	}

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
	public LockedBlock createNewLock(LockSecurityPlayer owner, Block block, LockedBlock secondaryComponent) {
		return createNewLock(owner, block.getLocation(), secondaryComponent);
	}

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
	public LockedBlock createNewLock(LockSecurityPlayer owner, Block block) {
		return createNewLock(owner, block.getLocation());
	}
	
	/**
	 * Get a set of all currently registered locked block objects
	 * 
	 * @return a set of registered blocks
	 */
	public Set<LockedBlock> getLockedBlocks() {
		return Collections.unmodifiableSet(lockedBlocks);
	}
	
	/**
	 * Get a set of all locked block objects with the given Key ID
	 * 
	 * @param keyId the Key ID to search
	 * @return a set of all registered blocks with the given Key ID
	 */
	public Set<LockedBlock> getLockedBlocks(int keyId) {
		return lockedBlocks.stream()
				.filter(b -> b.getKeyID() == keyId)
				.collect(Collectors.toSet());
	}
	/**
	 * Get a set of all currently unloaded locked block objects
	 * 
	 * @return a set of unloaded, but registered, blocks
	 */
	public Set<LockedBlock> getUnloadedBlocks() {
		return Collections.unmodifiableSet(unloadedBlocks);
	}
	
	/**
	 * Check if a block is lockable or not
	 * 
	 * @param block the block to check
	 * @return true if it is lockable
	 */
	public boolean isLockable(Block block) {
		return isLockable(block.getType());
	}
	
	/**
	 * Check if a material is lockable or not
	 * 
	 * @param type the material to check
	 * @return true if it is lockable
	 */
	public boolean isLockable(Material type) {
		if (type == null) return false;
		
		List<String> lockableBlocks = plugin.getConfig().getStringList("LockableBlocks");
		
		for (String material : lockableBlocks)
			if (type.name().equalsIgnoreCase(material)) return true;
		return false;
	}

	/**
	 * Get the next unique lock ID that should be used without incrementing
	 * its value
	 * 
	 * @return the next lock ID
	 */
	public int getNextLockID() {
		return getNextLockID(false);
	}

	/**
	 * Get the next unique lock ID that should be used
	 * 
	 * @param increment whether the next lock ID should increment or not
	 * @return the next lock ID
	 */
	public int getNextLockID(boolean increment) {
		return (increment ? nextLockID++ : nextLockID);
	}

	/**
	 * Get the next unique key ID that should be used without incrementing
	 * its value
	 * 
	 * @return the next key ID
	 */
	public int getNextKeyID() {
		return getNextKeyID(false);
	}

	/**
	 * Get the next unique key ID that should be used
	 * 
	 * @param increment whether the next key ID should increment or not
	 * @return the next key ID
	 */
	public int getNextKeyID(boolean increment) {
		return (increment ? nextKeyID++ : nextKeyID);
	}

	/**
	 * Load all existing locked blocks contained in the specified world.
	 * This information is based on registered players in the {@link PlayerRegistry}
	 * 
	 * @param world the world to load from
	 */
	public void loadDataForWorld(World world) {
		Preconditions.checkArgument(world != null, "Data cannot be loaded for world null");
		
		// Add blocks in the player's data
		this.playerRegistry.getPlayers()
			.forEach(p -> p.getOwnedBlocks().stream()
				.filter(b -> b.getLocation().getWorld() == world)
				.forEach(lockedBlocks::add)
			);
		
		// Add all unloaded blocks to memory
		this.unloadedBlocks.stream()
			.filter(b -> b.getLocation().getWorld() == world)
			.forEach(lockedBlocks::add);
		this.unloadedBlocks.removeIf(b -> b.getLocation().getWorld() == world);
	}

	/**
	 * Unload all registered locked blocks contained in the specified world.
	 * 
	 * @param world the world to unload from
	 */
	public void unloadDataForWorld(World world) {
		Preconditions.checkArgument(world != null, "Data cannot be unloaded for world null");
		
		this.lockedBlocks.stream()
			.filter(b -> b.getLocation().getWorld() == world)
			.forEach(unloadedBlocks::add);
		this.lockedBlocks.removeIf(b -> b.getLocation().getWorld() == world);
	}

	/**
	 * Clear all block data from memory
	 */
	public void clearLockedBlockData() {
		this.lockedBlocks.clear();
		this.unloadedBlocks.clear();
	}
	
}