package me.choco.locks.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import me.choco.locks.LockSecurity;

public class LockedBlock {
	
	private Block block;
	private OfflinePlayer owner;
	private final int lockID;
	private final int keyID;
	public LockedBlock(Block block, OfflinePlayer owner, int lockID, int keyID){
		LockSecurity plugin = LockSecurity.getPlugin();
		if (plugin.getLocalizedData().isLockedBlock(block)) throw new IllegalArgumentException("Block " + block + " is already a registered locked block");
		if (plugin.getLocalizedData().isLockedBlock(lockID)) throw new IllegalArgumentException("Lock ID " + lockID + " is already in use");
		
		this.block = block;
		this.owner = owner;
		this.lockID = lockID;
		this.keyID = keyID;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	public OfflinePlayer getOwner() {
		return (owner.isOnline() ? owner.getPlayer() : owner);
	}
	
	public void setOwner(OfflinePlayer owner) {
		this.owner = owner;
	}
	
	public int getLockId(){
		return lockID;
	}
	
	public int getKeyId(){
		return keyID;
	}
	
	@Override
	public String toString(){
		return "LockedBlock:{"
				+ "LockID:" + getLockId() + ","
				+ "KeyID:" + getKeyId() + ","
				+ "Owner:" + getOwner().getName() + "}";
	}
}