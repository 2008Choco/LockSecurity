package me.choco.locksecurity.registration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.ILockedBlock;
import me.choco.locksecurity.api.ILockedBlockManager;
import me.choco.locksecurity.api.IPlayerRegistry;

public class LockedBlockManager implements ILockedBlockManager {
	
	private int nextLockID = -1, nextKeyID = -1;
	
	private final List<ILockedBlock> lockedBlocks = new ArrayList<>(), unloadedBlocks = new ArrayList<>();
	private final IPlayerRegistry playerRegistry;
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
	
	@Override
	public void registerBlock(ILockedBlock block) {
		this.lockedBlocks.add(block);
	}
	
	@Override
	public void unregisterBlock(ILockedBlock block) {
		this.lockedBlocks.remove(block);
	}
	
	@Override
	public boolean isRegistered(ILockedBlock block) {
		return lockedBlocks.contains(block);
	}
	
	@Override
	public boolean isRegistered(Location location) {
		return getLockedBlock(location) != null;
	}
	
	@Override
	public boolean isRegistered(Block block) {
		return getLockedBlock(block) != null;
	}
	
	@Override
	public ILockedBlock getLockedBlock(Location location) {
		return getLockedBlock(location.getBlock());
	}
	
	@Override
	public ILockedBlock getLockedBlock(Block block) {
		for (ILockedBlock lBlock : this.lockedBlocks)
			if (lBlock.getBlock().equals(block)) return lBlock;
		return null;
	}
	
	@Override
	public ILockedBlock getLockedBlock(int lockID) {
		return this.lockedBlocks.stream()
			.filter(b -> b.getLockID() == lockID)
			.findFirst().orElse(null);
	}
	
	@Override
	public List<ILockedBlock> getLockedBlocks() {
		return ImmutableList.copyOf(lockedBlocks);
	}
	
	@Override
	public List<ILockedBlock> getLockedBlocks(int keyID) {
		return this.lockedBlocks.stream()
				.filter(b -> b.getKeyID() == keyID)
				.collect(Collectors.toList());
	}
	
	@Override
	public List<ILockedBlock> getUnloadedBlocks() {
		return ImmutableList.copyOf(unloadedBlocks);
	}
	
	@Override
	public boolean isLockable(Block block) {
		return isLockable(block.getType());
	}
	
	@Override
	public boolean isLockable(Material type) {
		List<String> lockableBlocks = plugin.getConfig().getStringList("LockableBlocks");
		
		for (String material : lockableBlocks)
			if (type.name().equalsIgnoreCase(material)) return true;
		
		return false;
	}
	
	@Override
	public int getNextLockID() {
		return getNextLockID(false);
	}
	
	@Override
	public int getNextLockID(boolean increment) {
		if (this.nextLockID == -1) {}
		
		int lockID = this.nextLockID;
		if (increment) this.nextLockID++;
		return lockID;
	}
	
	@Override
	public int getNextKeyID() {
		return getNextKeyID(false);
	}
	
	@Override
	public int getNextKeyID(boolean increment) {
		if (this.nextKeyID == -1) {}
		
		int keyID = this.nextKeyID;
		if (increment) this.nextKeyID++;
		return keyID;
	}
	
	@Override
	public void loadDataForWorld(World world) {
		// Add blocks in the player's data
		this.playerRegistry.getPlayers()
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
	
	@Override
	public void unloadDataForWorld(World world) {
		this.lockedBlocks.stream()
			.filter(b -> b.getLocation().getWorld() == world)
			.forEach(b -> unloadedBlocks.add(b));
		this.lockedBlocks.removeIf(b -> b.getLocation().getWorld() == world);
	}
	
	@Override
	public void clearLockedBlockData() {
		this.lockedBlocks.clear();
		this.unloadedBlocks.clear();
	}
	
}