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
import me.choco.locks.api.LockedMultiBlock;

public class LocalizedDataHandler {
	
	private final List<LockedBlock> lockedBlocks = new ArrayList<LockedBlock>();
	private boolean isDirty = false;
	
	LockSecurity plugin;
	public LocalizedDataHandler(LockSecurity plugin) {
		this.plugin = plugin;
	}
	
	public List<LockedBlock> getLockedBlocks(){
		return lockedBlocks;
	}

	public void registerLockedBlock(LockedBlock block){
		this.lockedBlocks.add(block);
		isDirty = true;
	}
	
	public void registerLockedBlock(LockedMultiBlock block){
		this.lockedBlocks.add(block);
		isDirty = true;
	}
	
	public void unregisterLockedBlock(LockedBlock block){
		this.lockedBlocks.remove(block);
		isDirty = true;
	}
	
	public void unregisterLockedBlock(LockedMultiBlock block){
		this.lockedBlocks.remove(block);
		isDirty = true;
	}
	
	public void unregisterLockedBlock(Block block){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getBlock().equals(block)) lockedBlocks.remove(block);
		isDirty = true;
	}
	
	@Deprecated
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
	
	public boolean isLockedBlock(int lockID){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getLockId() == lockID) return true;
		return false;
	}
	
	public LockedBlock getLockedBlock(Block block){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getBlock().equals(block)) return lockedBlock;
		return null;
	}
	
	public LockedBlock getLockedBlock(int lockID){
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getLockId() == lockID) return lockedBlock;
		return null;
	}
	
	public List<LockedBlock> getAllLocks(OfflinePlayer player){
		List<LockedBlock> blocks = new ArrayList<LockedBlock>();
		for (LockedBlock lockedBlock : lockedBlocks)
			if (lockedBlock.getOwner().getUniqueId().equals(player.getUniqueId())) blocks.add(lockedBlock);
		return blocks;
	}
	
	/** Get the next LockID
	 * @return int - The ID of the lock
	 */
	public int getNextLockID(){ //TODO: Non-database-oriented
		Connection connection = plugin.getLSDatabase().openConnection();
		Statement statement = plugin.getLSDatabase().createStatement(connection);
		ResultSet set = plugin.getLSDatabase().queryDatabase(statement, "select * from LockedBlocks order by LockID desc limit 1");
		int lockID = 1;
		try{
			lockID = set.getInt("LockID") + lockID;
		}catch (SQLException e){}
		plugin.getLSDatabase().closeResultSet(set); plugin.getLSDatabase().closeStatement(statement); plugin.getLSDatabase().closeConnection(connection);
		return lockID;
	}
	
	/** Get the next KeyID
	 * @return int - The ID of the next key
	 */
	public int getNextKeyID(){ //TODO: Non-database-oriented
		Connection connection = plugin.getLSDatabase().openConnection();
		Statement statement = plugin.getLSDatabase().createStatement(connection);
		ResultSet set = plugin.getLSDatabase().queryDatabase(statement, "select * from LockedBlocks order by KeyID desc limit 1");
		int keyID = 1;
		try{
			keyID = set.getInt("KeyID") + keyID;
		}catch (SQLException e){}
		plugin.getLSDatabase().closeResultSet(set); plugin.getLSDatabase().closeStatement(statement); plugin.getLSDatabase().closeConnection(connection);
		return keyID;
	}
	
	public void saveLocalizedDataToDatabase(boolean logInfo){
		if (isDirty){
			if (logInfo) plugin.getLogger().info("... Saving localized data to the database ...");
			try{
				Connection connection = plugin.getLSDatabase().openConnection();
				PreparedStatement statement = plugin.getLSDatabase().createPreparedStatement(connection, 
						"INSERT OR REPLACE INTO LockedBlocks VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
				for (LockedBlock block : getLockedBlocks()){
					statement.setInt(1, block.getLockId());
					statement.setInt(2, block.getKeyId());
					statement.setString(3, block.getOwner().getUniqueId().toString());
					statement.setString(4, block.getOwner().getName());
					statement.setString(5, block.getBlock().getType().name());
					statement.setInt(6, block.getBlock().getLocation().getBlockX());
					statement.setInt(7, block.getBlock().getLocation().getBlockY());
					statement.setInt(8, block.getBlock().getLocation().getBlockZ());
					statement.setString(9, block.getBlock().getWorld().getName());
					statement.execute();
				}
			}catch(SQLException e){ e.printStackTrace(); }
			if (logInfo) plugin.getLogger().info("    Done...");
		}
		isDirty = false;
	}
}