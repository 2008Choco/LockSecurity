package me.choco.locks.api.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LockedMultiBlock extends LockedBlock{

	private Block secondaryBlock;
	public LockedMultiBlock(Block block, Block secondaryBlock, Player owner, int lockID, int keyID) {
		super(block, owner, lockID, keyID);
		this.secondaryBlock = secondaryBlock;
	}
	
	public Block getSecondaryBlock(){
		return secondaryBlock;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}