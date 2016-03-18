package me.choco.locks.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.block.LockedBlock;
import me.choco.locks.api.block.LockedMultiBlock;

public class LocalizedDataHandler {
	
	private final List<LockedBlock> lockedBlocks = new ArrayList<LockedBlock>();
	
	LockSecurity plugin;
	public LocalizedDataHandler(LockSecurity plugin) {
		this.plugin = plugin;
	}
	
	public List<LockedBlock> getLockedBlocks(){
		return lockedBlocks;
	}

	public void registerLockedBlock(LockedBlock block){
		this.lockedBlocks.add(block);
	}
	
	public void registerLockedBlock(LockedMultiBlock block){
		this.lockedBlocks.add(block);
	}
	
	public void unregisterLockedBlock(LockedBlock block){
		this.lockedBlocks.remove(block);
	}
	
	public void unregisterLockedBlock(LockedMultiBlock block){
		this.lockedBlocks.remove(block);
	}
	
	public void unregisterLockedBlock(Block block){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getBlock().equals(block)) lockedBlocks.remove(block);
	}
	
	public LockState getLockedState(Block block){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getBlock().equals(block)) return LockState.LOCKED;
		return LockState.UNLOCKED;
	}
	
	public boolean isLockedBlock(Block block){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock instanceof LockedMultiBlock){
				LockedMultiBlock lmb = (LockedMultiBlock) lockedBlock;
				if (lmb.getBlock().equals(block) || lmb.getSecondaryBlock().equals(block)) 
					return true;
			}else{
				if (lockedBlock.getBlock().equals(block)) return true;
			}
		return false;
	}
	
	public LockedBlock getLockedBlock(Block block){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getBlock().equals(block)) return lockedBlock;
		return null;
	}
}