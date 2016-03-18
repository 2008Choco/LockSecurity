package me.choco.locks.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.choco.locks.LockSecurity;

public class LockedBlockAccessor {
	LockSecurity plugin;
	Keys keys;
	public LockedBlockAccessor(LockSecurity plugin){
		this.plugin = plugin;
		this.keys = new Keys(plugin);
	}
	
	/** This will return whether the block is locked or not.
	 * @see {@link LockSecurity#isLockable} method first
	 * @param block - The block to receive the state of
	 * @return LockState - The state of the block
	 */
	public LockState getLockedState(Block block){
		int x = block.getLocation().getBlockX();
		int y = block.getLocation().getBlockY();
		int z = block.getLocation().getBlockZ();
		String world = block.getWorld().getName();
		
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select * from LockedBlocks");
		
		try{
			while (set.next())
				if (set.getInt("LocationX") == x && set.getInt("LocationY") == y && set.getInt("LocationZ") == z 
						&& set.getString("LocationWorld").equals(world)){
					return LockState.LOCKED;
				}
		}catch (SQLException e){e.printStackTrace();}
		finally{plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);}
		return LockState.UNLOCKED;
	}
	
	/** Set the specified block to be locked. 
	 * This will store all block related information in the locked.yml file, and declare it as LockState.LOCKED
	 * @param block - The block to lock
	 * @param owner - The owner of the block lock
	 */
	public void setLocked(Block block, Player owner){
		String blockType = block.getType().toString();
		String playerUUID = owner.getUniqueId().toString(); String playerName = owner.getName();
		int nextID = getNextKeyID();
		
		owner.getInventory().addItem(keys.convertToLockedKey(owner.getInventory().getItemInMainHand(), nextID));
		insertDatabaseInfo(nextID, playerUUID, playerName, blockType, block.getLocation());
		dualComponentBlockHandler(block, owner, nextID);
	}
	
	/** Set the specified block to be unlocked
	 * @param block - The block to unlock
	 */
	public void setUnlocked(Block block){
		int x = block.getLocation().getBlockX();
		int y = block.getLocation().getBlockY();
		int z = block.getLocation().getBlockZ();
		String world = block.getWorld().getName();
		
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		plugin.executeStatement(statement, "delete from LockedBlocks " +
			"where LocationX = " + x + " and LocationY = " + y + " and LocationZ = " + z + " and LocationWorld = '" + world + "'");
		plugin.closeStatement(statement); plugin.closeConnection(connection);
	}
	
	/** Set the specified LockID to be unlocked
	 * @param lockID - The LockID to unlock
	 */
	public void setUnlocked(int lockID){
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		plugin.executeStatement(statement, "delete from LockedBlocks where LockID = " + lockID);
		plugin.closeStatement(statement); plugin.closeConnection(connection);
	}
	
	/** Transfer a locked block to another player
	 * @param block - The block to transfer
	 * @param player - The player to transfer the block to
	 */
	public void transferLock(Block block, OfflinePlayer player){
		int x = block.getLocation().getBlockX();
		int y = block.getLocation().getBlockY();
		int z = block.getLocation().getBlockZ();
		String world = block.getWorld().getName();
		
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		plugin.executeStatement(statement, "update LockedBlocks set OwnerName = '" + player.getName()
				+ "' where LocationX = " + x + " and LocationY = " + y + " and LocationZ = " + z + " and LocationWorld = '" + world + "'");
		plugin.executeStatement(statement, "update LockedBlocks set OwnerUUID = '" + player.getUniqueId().toString()
				+ "' where LocationX = " + x + " and LocationY = " + y + " and LocationZ = " + z + " and LocationWorld = '" + world + "'");
		plugin.closeStatement(statement); plugin.closeConnection(connection);
	}
	
	/** A boolean method to determine whether the Key ID matches the Block Lock ID
	 * @param block - The block to reference an ID from
	 * @param player - The player that is right clicking the locked block
	 * @return boolean - Whether the player has the right key in their hand or not
	 */
	public boolean playerHasCorrectKey(Block block, Player player){
		if (player.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK)){
			List<Integer> keyIDs = getKeyIDs(player.getInventory().getItemInMainHand());
			if (keyIDs == null)
				return false;
			
			for (int id : keyIDs){
				if (id == getBlockKeyID(block))
					return true;
			}
		}
		return false;
	}
	
	/** Get the next LockID
	 * @return int - The ID of the lock
	 */
	public int getNextLockID(){
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select * from LockedBlocks order by LockID desc limit 1");
		int lockID = 1;
		try{
			lockID = set.getInt("LockID") + lockID;
		}catch (SQLException e){}
		plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
		return lockID;
	}
	
	/** Get the next KeyID
	 * @return int - The ID of the next key
	 */
	public int getNextKeyID(){
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select * from LockedBlocks order by KeyID desc limit 1");
		int keyID = 1;
		try{
			keyID = set.getInt("KeyID") + keyID;
		}catch (SQLException e){}
		plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
		return keyID;
	}
	
	/** Get the IDs of the Key in the players hand
	 * @param player - The player to reference the item in hand
	 * @return String - String value of the ID binded to the key
	 */
	public List<Integer> getKeyIDs(ItemStack key){
		if (key.getType().equals(Material.TRIPWIRE_HOOK) && key.hasItemMeta()){
			if (key.getItemMeta().hasLore()){
				String[] ids = key.getItemMeta().getLore().toString().replaceAll("Key ID: ", "").replaceAll("\\[", "").replaceAll("\\]", "").split(", ");
				List<Integer> intIDs = new ArrayList<Integer>();
				for (String currentID : ids){
					try{
						intIDs.add(Integer.parseInt(ChatColor.stripColor(currentID)));
					}catch(NumberFormatException e){
						continue;
					}
				}
				return intIDs;
			}
			return null;
		}
		return null;
	}
	
	/** Get the Lock ID of the block
	 * @param block - The block to gather information from
	 * Do not use to check lock owners
	 * @return int - ID binded to the chest
	 */
	public int getBlockLockID(Block block){
		int x = block.getLocation().getBlockX();
		int y = block.getLocation().getBlockY();
		int z = block.getLocation().getBlockZ();
		String world = block.getWorld().getName();
		
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select * from LockedBlocks");
		try{
			while (set.next()){
				if (set.getInt("LocationX") == x && set.getInt("LocationY") == y && set.getInt("LocationZ") == z 
						&& set.getString("LocationWorld").equals(world)){
					int lockID = set.getInt("LockID");
					plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
					return lockID;
				}
			}
			
		}catch (SQLException e){e.printStackTrace();}
		plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
		return 0;
	}
	
	/** Get the Key ID of the block
	 * @param block - The block to gather information from
	 * @return int - Key ID binded to the chest
	 */
	public int getBlockKeyID(Block block){
		int x = block.getLocation().getBlockX();
		int y = block.getLocation().getBlockY();
		int z = block.getLocation().getBlockZ();
		String world = block.getWorld().getName();
		
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select * from LockedBlocks");
		try{
			while (set.next()){
				if (set.getInt("LocationX") == x && set.getInt("LocationY") == y && set.getInt("LocationZ") == z 
						&& set.getString("LocationWorld").equals(world)){
					int lockID = set.getInt("KeyID");
					plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
					return lockID;
				}
			}
			
		}catch (SQLException e){e.printStackTrace();}
		plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
		return 0;
	}
	
	/** Get the owner of the block
	 * @param block - The block to gather information from
	 * @return A String value of the Owner's last-seen username
	 */
	public String getBlockOwner(Block block){
		int x = block.getLocation().getBlockX();
		int y = block.getLocation().getBlockY();
		int z = block.getLocation().getBlockZ();
		String world = block.getWorld().getName();
		
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select * from LockedBlocks");
		try{
			while (set.next()){
				if (set.getInt("LocationX") == x && set.getInt("LocationY") == y && set.getInt("LocationZ") == z 
						&& set.getString("LocationWorld").equals(world)){
					String name = set.getString("OwnerName");
					plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
					return name;
				}
			}
			
		}catch (SQLException e){e.printStackTrace();}
		plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
		return null;
	}
	
	/** Get the owner of the block's UUID
	 * @param block - The block to gather information from
	 * @return A String value of the Owner's UUID
	 */
	public String getBlockOwnerUUID(Block block){
		int x = block.getLocation().getBlockX();
		int y = block.getLocation().getBlockY();
		int z = block.getLocation().getBlockZ();
		String world = block.getWorld().getName();
		
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select * from LockedBlocks");
		try{
			while (set.next()){
				if (set.getInt("LocationX") == x && set.getInt("LocationY") == y && set.getInt("LocationZ") == z 
						&& set.getString("LocationWorld").equals(world)){
					String uuid = set.getString("OwnerUUID");
					plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
					return uuid;
				}
			}
			
		}catch (SQLException e){e.printStackTrace();}
		plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
		return null;
	}
	
	public Location getLocationFromLockID(int lockID){
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select * from LockedBlocks where LockID = " + lockID);
		try {
			int x = set.getInt("LocationX"); int y = set.getInt("LocationY"); int z = set.getInt("LocationZ");
			String world = set.getString("LocationWorld");
			plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
			return new Location(Bukkit.getWorld(world), x, y, z);
		}catch (SQLException e){e.printStackTrace(); return null;}
		finally{plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);}
	}
	
	/** Gather all the blocks that the player has locked
	 * @param player - The player to look up in the database
	 * @return An ArrayList of the Lock ID's that the player own's
	 */
	public ArrayList<Integer> getAllLocks(OfflinePlayer player){
		ArrayList<Integer> ids = new ArrayList<Integer>();
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select * from LockedBlocks");
		try{
			while (set.next())
				if (set.getString("OwnerUUID").equals(player.getUniqueId().toString()))
					ids.add(set.getInt("LockID"));
		}catch (SQLException e){e.printStackTrace();}
		plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
		return ids;
	}
	
	/** Get the amount of locks that the player has
	 * @param player - The player to look up in the database
	 * @return The amount of locks the player has
	 */
	public int getLockCount(OfflinePlayer player){
		int amount = 0;
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select * from LockedBlocks");
		try{
			while (set.next())
				if (set.getString("OwnerUUID").equals(player.getUniqueId().toString()))
					amount++;
		}catch (SQLException e){e.printStackTrace();}
		plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
		return amount;
	}
	
	/** Insert information into the SQLite Database
	 * @param keyID - The KeyID for the block
	 * @param ownerUUID - The owner's UUID
	 * @param ownerName - The owner's Name
	 * @param blockType - The locked block type
	 * @param location - The location of the block
	 */
	public void insertDatabaseInfo(int keyID, String ownerUUID, String ownerName, String blockType, Location location){
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		plugin.executeStatement(statement, "insert into LockedBlocks values(null, " + keyID + ", '" + ownerUUID + "', '" + ownerName + "', '" 
				+ blockType + "', " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ", '" + location.getWorld().getName() + "')");
		plugin.closeStatement(statement); plugin.closeConnection(connection);
	}
	
	/** Insert information into the SQLite Database with a specific lock ID (Used in startup, should not be used otherwise)
	 * @param lockID - The LockID for the block
	 * @param keyID - The KeyID for the block
	 * @param ownerUUID - The owner's UUID
	 * @param ownerName - The owner's Name
	 * @param blockType - The locked block type
	 * @param location - The location of the block
	 */
	public void insertDatabaseInfo(int lockID, int keyID, String ownerUUID, String ownerName, String blockType, Location location){
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		plugin.executeStatement(statement, "insert into LockedBlocks values(" + lockID + ", " + keyID + ", '" + ownerUUID + "', '" + ownerName + "', '" 
				+ blockType + "', " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ", '" + location.getWorld().getName() + "')");
		plugin.closeStatement(statement); plugin.closeConnection(connection);
	}
	
	public boolean isInDatabase(int lockID){
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select LockID from LockedBlocks where LockID = " + lockID);
		try {
			if (set.next()){
				plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
				return true;
			}
		}
		catch (SQLException e){return false;}
		plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
		return false;
	}
	
	private void dualComponentBlockHandler(Block block, Player owner, int nextID){
		String blockType = block.getType().toString();
		String playerUUID = owner.getUniqueId().toString(); String playerName = owner.getName();
		Material type = block.getType();
		
		if (type.toString().contains("DOOR")){
			if (block.getRelative(BlockFace.UP).getType().equals(type)){
				insertDatabaseInfo(nextID, playerUUID, playerName, blockType, block.getLocation().add(0, 1, 0));
			}
			else if (block.getRelative(BlockFace.DOWN).getType().equals(type)){
				insertDatabaseInfo(nextID, playerUUID, playerName, blockType, block.getLocation().add(0, -1, 0));
			}
		}
		if (type.equals(Material.CHEST) || type.equals(Material.TRAPPED_CHEST)){
			if (block.getRelative(BlockFace.NORTH).getType().equals(type)){
				insertDatabaseInfo(nextID, playerUUID, playerName, blockType, block.getLocation().add(0, 0, -1));
			}
			else if (block.getRelative(BlockFace.SOUTH).getType().equals(type)){
				insertDatabaseInfo(nextID, playerUUID, playerName, blockType, block.getLocation().add(0, 0, 1));
			}
			else if (block.getRelative(BlockFace.EAST).getType().equals(type)){
				insertDatabaseInfo(nextID, playerUUID, playerName, blockType, block.getLocation().add(1, 0, 0));
			}
			else if (block.getRelative(BlockFace.WEST).getType().equals(type)){
				insertDatabaseInfo(nextID, playerUUID, playerName, blockType, block.getLocation().add(-1, 0, 0));
			}
		}
	}
}