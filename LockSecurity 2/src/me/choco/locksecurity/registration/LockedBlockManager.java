package me.choco.locksecurity.registration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.utils.LSPlayer;

/** 
 * The manager that keeps track of registered locked blocks and their information.
 * The registration of locked blocks does not mean that they do not exist. The existance
 * of locked blocks is contained within the {@link LSPlayer} object.
 * <p>
 * <b>NOTE:</b> A registered locked block simply means that its information has been loaded. 
 * Information of a locked block may be loaded and unloaded in this manager at any time
 * such that it still exists in the owner's LSPlayer object. Registration of locked blocks
 * should only be done if the world in which it is possessed is loaded. If the world
 * is unloaded, the locked block should also be unregistered to provide more efficient
 * lookups in LockSecurity code.
 * 
 * @author Parker Hawke - 2008Choco
 */
public class LockedBlockManager {
	
	private int nextLockID = -1, nextKeyID = -1;
	
	private final Set<LockedBlock> lockedBlocks = new HashSet<>(), unloadedBlocks = new HashSet<>();
	private final PlayerRegistry playerRegistry;
	
	public LockedBlockManager(LockSecurity plugin) {
		this.playerRegistry = plugin.getPlayerRegistry();
		
		// Read Lock / Key ID values
		try(BufferedReader reader = new BufferedReader(new FileReader(plugin.infoFile))){
			String line;
			while ((line = reader.readLine()) != null){
				String[] values = line.split("=");
				if (values[0].equalsIgnoreCase("nextLockID")){
					this.nextLockID = Integer.parseInt(values[1]);
				}else if (values[0].equalsIgnoreCase("nextKeyID")){
					this.nextKeyID = Integer.parseInt(values[1]);
				}
			}
		}catch(IOException | NumberFormatException e){ e.printStackTrace(); }
	}
	
	/** 
	 * Register a locked block to the manager
	 * 
	 * @param block the block to register
	 */
	public void registerBlock(LockedBlock block){
		this.lockedBlocks.add(block);
	}
	
	/** 
	 * Unregister a locked block from the manager
	 * 
	 * @param block the block to unregister
	 */
	public void unregisterBlock(LockedBlock block){
		this.lockedBlocks.remove(block);
	}
	
	/** 
	 * Check if a specific location possesses a registered locked block or not
	 * 
	 * @param location the location to check
	 * @return true if a block is registered in the specified location
	 */
	public boolean isRegistered(Location location){
		return getLockedBlock(location) != null;
	}
	
	/** 
	 * Check if a specific block is a registered locked block or not
	 * 
	 * @param block the block to check
	 * @return true if the block is a registered locked block
	 */
	public boolean isRegistered(Block block){
		return getLockedBlock(block) != null;
	}
	
	/** 
	 * Check if a specific locked block is registered or not
	 * 
	 * @param block the block to check
	 * @return true if the block is registered
	 */
	public boolean isRegistered(LockedBlock block){
		return lockedBlocks.contains(block);
	}
	
	/**
	 * Get a locked block from the registry based on a location
	 * 
	 * @param location the location to get the block from
	 * @return the locked block object in the specified location. null if none found
	 */
	public LockedBlock getLockedBlock(Location location){
		return getLockedBlock(location.getBlock());
	}
	
	/** 
	 * Get a locked block from the registry
	 * 
	 * @param block the block in which to receive a locked block from
	 * @return the locked block object. null if none found
	 */
	public LockedBlock getLockedBlock(Block block){
		for (LockedBlock lBlock : this.lockedBlocks)
			if (lBlock.getBlock().equals(block)) return lBlock;
		return null;
	}
	
	/** 
	 * Get a locked block from Lock ID
	 * 
	 * @return the locked block with the given Lock ID. Null if not found
	 */
	public LockedBlock getLockedBlock(int lockID){
		return this.lockedBlocks.stream()
			.filter(b -> b.getLockID() == lockID)
			.findFirst().orElse(null);
	}
	
	/** 
	 * Get a set of all currently registered locked block objects
	 * 
	 * @return a set of registered blocks
	 */
	public Set<LockedBlock> getLockedBlocks() {
		return ImmutableSet.copyOf(lockedBlocks);
	}
	
	/** 
	 * Get a set of all locked block objects with the given Key ID
	 * 
	 * @param keyID the Key ID to search
	 * @return a set of all registered blocks with the given Key ID
	 */
	public Set<LockedBlock> getLockedBlocks(int keyID){
		return this.lockedBlocks.stream()
				.filter(b -> b.getKeyID() == keyID)
				.collect(Collectors.toSet());
	}
	
	/** 
	 * Check if a block is lockable or not
	 * 
	 * @param block the block to check
	 * @return true if it is lockable
	 */
	public boolean isLockable(Block block){
		return isLockable(block.getType());
	}
	
	/** 
	 * Check if a material is lockable or not
	 * 
	 * @param type the material to check
	 * @return true if it is lockable
	 */
	public boolean isLockable(Material type){
		return (type.equals(Material.CHEST) || type.equals(Material.TRAPPED_CHEST) || type.equals(Material.TRAP_DOOR)
				|| type.equals(Material.FURNACE) || type.equals(Material.DISPENSER) || type.equals(Material.DROPPER)
				|| type.equals(Material.HOPPER) || type.equals(Material.ANVIL) 
				|| type.toString().contains("DOOR") || type.toString().contains("FENCE_GATE"));
		// TODO
//		List<String> lockableBlocks = getConfig().getStringList("LockableBlocks");
//		for (String listedType : lockableBlocks)
//			if (type.toString().equals(listedType)) return true;
	}
	
	/** 
	 * Get the next unique lock ID that should be used without incrementing 
	 * its value
	 * 
	 * @return the next lock ID
	 */
	public int getNextLockID(){
		return getNextLockID(false);
	}
	
	/** 
	 * Get the next unique lock ID that should be used
	 * 
	 * @param increment whether the next lock ID should increment or not
	 * @return the next lock ID
	 */
	public int getNextLockID(boolean increment){ // TODO
		if (this.nextLockID == -1){}
		
		int lockID = this.nextLockID;
		if (increment) this.nextLockID++;
		return lockID;
	}
	
	/** 
	 * Get the next unique key ID that should be used without incrementing 
	 * its value
	 * 
	 * @return the next key ID
	 */
	public int getNextKeyID(){
		return getNextKeyID(false);
	}
	
	/** 
	 * Get the next unique key ID that should be used
	 * 
	 * @param increment whether the next key ID should increment or not
	 * @return the next key ID
	 */
	public int getNextKeyID(boolean increment){
		if (this.nextKeyID == -1){}
		
		int keyID = this.nextKeyID;
		if (increment) this.nextKeyID++;
		return keyID;
	}
	
	/** 
	 * Load all existing locked blocks contained in the specified world.
	 * This information is based on registered players in the {@link PlayerRegistry}
	 * 
	 * @param world the world to load from
	 */
	public void loadDataForWorld(World world){
		// Add blocks in the player's data
		this.playerRegistry.getPlayers().values()
			.forEach(p -> p.getOwnedBlocks().stream()
				.filter(b -> b.getLocation().getWorld() == world)
				.forEach(b -> lockedBlocks.add(b))
			);
		
		// Add all unloaded blocks to memory
		this.unloadedBlocks.stream()
			.filter(b -> b.getLocation().getWorld() == world)
			.forEach(b -> lockedBlocks.add(b));
		this.unloadedBlocks.removeIf(b -> b.getLocation().getWorld() == world);
	}
	
	/** 
	 * Unload all registered locked blocks contained in the specified world.
	 * 
	 * @param world the world to unload from
	 */
	public void unloadDataForWorld(World world){
		this.lockedBlocks.stream()
			.filter(b -> b.getLocation().getWorld() == world)
			.forEach(b -> unloadedBlocks.add(b));
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