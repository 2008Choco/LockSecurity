package me.choco.locks.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.LockedBlock;

public class LocalizedDataHandler {
	
	private final List<LockedBlock> lockedBlocks = new ArrayList<LockedBlock>();
	private boolean isDirty = false;
	
	private LockSecurity plugin;
	public LocalizedDataHandler(LockSecurity plugin) {
		this.plugin = plugin;
	}
	
	/** Get all currently registered locked blocks (Local data)
	 * @return A list of all LockedBlock objects
	 */
	public List<LockedBlock> getLockedBlocks(){
		return lockedBlocks;
	}

	/** Register a new locked block
	 * @param block - The new locked block to create
	 */
	public void registerLockedBlock(LockedBlock block){
		this.lockedBlocks.add(block);
		isDirty = true;
	}
	
	/** Unregister a locked block based on a LockedBlock object
	 * @param block - The locked block to unregister
	 */
	public void unregisterLockedBlock(LockedBlock block){
		this.lockedBlocks.remove(block);
		isDirty = true;
	}
	
	/** Unregister a locked block based on a {@link Block}
	 * @param block - The block to unregister
	 */
	public void unregisterLockedBlock(Block block){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getBlock().equals(block)) lockedBlocks.remove(block);
		isDirty = true;
	}
	
	/** Get the LockState of a specified block
	 * @param block - The block to reference
	 * @return LockState.UNLOCKED if unlocked, or LockState.LOCKED elsewise
	 * @deprecated Unused. See {@link LocalizedDataHandler#isLockedBlock(Block)} or {@link LocalizedDataHandler#isLockedBlock(int)}
	 */
	@Deprecated
	public LockState getLockedState(Block block){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getBlock().equals(block)) return LockState.LOCKED;
		return LockState.UNLOCKED;
	}
	
	/** Check whether a block is currently locked or not
	 * @param block - The block to reference
	 * @return true if the block is locked, false elsewise
	 */
	public boolean isLockedBlock(Block block){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getBlock().equals(block)) return true;
		return false;
	}
	
	/** Check whether a LockID is currently registered or not
	 * @param lockID - The lock id to reference
	 * @return true if the block is locked, false elsewise
	 */
	public boolean isLockedBlock(int lockID){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getLockId() == lockID) return true;
		return false;
	}
	
	/** Get an instance of a LockedBlock (If one exists). Will return null if not found
	 * @param block - The block to get an instance of LockedBlock from
	 * @return The LockedBlock instance of the block
	 */
	public LockedBlock getLockedBlock(Block block){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getBlock().equals(block)) return lockedBlock;
		return null;
	}
	
	/** Get an instance of a LockedBlock (If one exists). Will return null if not found
	 * @param lockID - The lockID to get an instance of LockedBlock from
	 * @return The LockedBlock instance of the ID
	 */
	public LockedBlock getLockedBlock(int lockID){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getLockId() == lockID) return lockedBlock;
		return null;
	}
	
	/** Get all locks owned by the specified player
	 * @param player - The player to reference
	 * @return A list of all LockedBlocks owned by the player
	 */
	public List<LockedBlock> getAllLocks(OfflinePlayer player){
		List<LockedBlock> blocks = new ArrayList<LockedBlock>();
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getOwner().getUniqueId().equals(player.getUniqueId())) blocks.add(lockedBlock);
		return blocks;
	}
	
	/** Get the next LockID
	 * @return int - The ID of the lock
	 */
	public int getNextLockID(){
		if (lockedBlocks.size() <= 0) return 1;
		return lockedBlocks.get(lockedBlocks.size() - 1).getLockId() + 1;
	}
	
	/** Get the next KeyID
	 * @return int - The ID of the next key
	 */
	public int getNextKeyID(){
		if (lockedBlocks.size() <= 0) return 1;
		return lockedBlocks.get(lockedBlocks.size() - 1).getKeyId() + 1;
	}
	
	/** Save all current localized data to the database (Can be slow if lots of information is present)
	 * @param logInfo - Whether information should be logged to the console or not
	 */
	public void saveLocalizedDataToDatabase(boolean logInfo){
		if (isDirty){
			if (logInfo) plugin.getLogger().info("... Saving localized data to the database ...");
			try{
				Connection connection = plugin.getLSDatabase().openConnection();
				
				//Remove unused data (removed blocks)
				Statement statement = plugin.getLSDatabase().createStatement(connection);
				Statement removalStatement = plugin.getLSDatabase().createStatement(connection);
				ResultSet set = plugin.getLSDatabase().queryDatabase(statement, "SELECT LockID FROM LockedBlocks");
				while (set.next()){
					int lockId = set.getInt(1);
					if (!isLockedBlock(lockId)){
						removalStatement.execute("DELETE FROM LockedBlocks WHERE LockID = " + lockId);
					}
				}
				plugin.getLSDatabase().closeResultSet(set); 
				plugin.getLSDatabase().closeStatement(statement); plugin.getLSDatabase().closeStatement(removalStatement);
				
				//Save new data / Update old data
				PreparedStatement pstatement = plugin.getLSDatabase().createPreparedStatement(connection, 
						"INSERT OR REPLACE INTO LockedBlocks VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
				for (LockedBlock block : getLockedBlocks()){
					pstatement.setInt(1, block.getLockId());
					pstatement.setInt(2, block.getKeyId());
					pstatement.setString(3, block.getOwner().getUniqueId().toString());
					pstatement.setString(4, block.getOwner().getName());
					pstatement.setString(5, block.getBlock().getType().name());
					pstatement.setInt(6, block.getBlock().getLocation().getBlockX());
					pstatement.setInt(7, block.getBlock().getLocation().getBlockY());
					pstatement.setInt(8, block.getBlock().getLocation().getBlockZ());
					pstatement.setString(9, block.getBlock().getWorld().getName());
					pstatement.execute();
				}
				plugin.getLSDatabase().closePreparedStatement(pstatement);
				
				plugin.getLSDatabase().closeConnection(connection);
			}catch(SQLException e){ e.printStackTrace(); }
			if (logInfo) plugin.getLogger().info("... Done...");
		}
		isDirty = false;
	}
}