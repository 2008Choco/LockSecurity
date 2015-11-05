package me.choco.locks.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.choco.locks.LockSecurity;

public class LockedBlockAccessor {
	LockSecurity plugin;
	Keys keys;
	LockStorageHandler ram;
	public LockedBlockAccessor(LockSecurity plugin){
		this.plugin = plugin;
		this.keys = new Keys(plugin);
		this.ram = new LockStorageHandler(plugin);
	}
	
	/** This will return whether the block is locked or not.
	 * @see {@link LockSecurity#isLockable} method first
	 * @param block - The block to receive the state of
	 * @return LockState - The state of the block
	 */
	public LockState getLockedState(Block block){
		Location location = block.getLocation();
		if (ram.isStored(location))
			return LockState.LOCKED;
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
		owner.getInventory().addItem(keys.convertToLockedKey(owner.getItemInHand(), getNextKeyID()));
		removeCurrentItem(owner);
		owner.playSound(owner.getLocation(), Sound.DOOR_CLOSE, 1, 2);
		
		//Add information to config
		addLockedYMLInformation(playerUUID, playerName, getNextKeyID(), blockType, block.getLocation());
		setNextLockID();
		dualComponentBlockHandler(block, owner);
		setNextKeyID();
		plugin.locked.saveConfig();
		plugin.locked.reloadConfig();
	}
	
	/** Set the specified block to be unlocked
	 * @param block - The block to unlock
	 */
	public void setUnlocked(Block block){
		int keyID = getBlockKeyID(block);
		Set<String> keys = plugin.locked.getConfig().getKeys(false);
		keys.remove("NextLockID");
		keys.remove("NextKeyID");
		for (String key : keys){
			if (plugin.locked.getConfig().getInt(key + ".KeyID") == keyID){
				String worldName = plugin.locked.getConfig().getString(key + ".Location.World");
				int x = plugin.locked.getConfig().getInt(key + ".Location.X");
				int y = plugin.locked.getConfig().getInt(key + ".Location.Y");
				int z = plugin.locked.getConfig().getInt(key + ".Location.Z");
				
				ram.removeLock(new Location(Bukkit.getWorld(worldName), x, y, z));
				plugin.locked.getConfig().set(key, null);
				
				plugin.locked.saveConfig();
				plugin.locked.reloadConfig();
			}
		}
	}
	
	/** Set the specified LockID to be unlocked
	 * @param lockID - The LockID to unlock
	 */
	public void setUnlocked(int lockID){
		String worldName = plugin.locked.getConfig().getString(lockID + ".Location.World");
		int x = plugin.locked.getConfig().getInt(lockID + ".Location.X");
		int y = plugin.locked.getConfig().getInt(lockID + ".Location.Y");
		int z = plugin.locked.getConfig().getInt(lockID + ".Location.Z");
		
		ram.removeLock(new Location(Bukkit.getWorld(worldName), x, y, z));
		plugin.locked.getConfig().set(String.valueOf(lockID), null);
		
		plugin.locked.saveConfig();
		plugin.locked.reloadConfig();
	}
	
	/** Transfer a locked block to another player
	 * @param block - The block to transfer
	 * @param player - The player to transfer the block to
	 */
	public void transferLock(Block block, OfflinePlayer player){
		int id = getBlockLockID(block);
		plugin.locked.getConfig().set(id + "OwnerUUID", player.getUniqueId().toString());
		plugin.locked.getConfig().set(id + ".PlayerName", player.getName().toString());
		
		plugin.locked.saveConfig();
		plugin.locked.reloadConfig();
	}
	
	/** A boolean method to determine whether the Key ID matches the Block Lock ID
	 * @param block - The block to reference an ID from
	 * @param player - The player that is right clicking the locked block
	 * @return boolean - Whether the player has the right key in their hand or not
	 */
	public boolean playerHasCorrectKey(Block block, Player player){
		if (player.getItemInHand().getType().equals(Material.TRIPWIRE_HOOK)){
			List<Integer> keyIDs = getKeyIDs(player.getItemInHand());
			
			if (keyIDs == null)
				return false;
			
			for (int id : keyIDs){
				if (id == getBlockKeyID(block))
					return true;
			}
		}
		return false;
	}
	
	/** Get the ID for the next key
	 * @return int - The ID of the next Key
	 */
	public int getNextLockID(){
		return plugin.locked.getConfig().getInt("NextLockID");
	}
	
	/** Increment 1 to the next key ID */
	private void setNextLockID(){
		plugin.locked.getConfig().set("NextLockID", getNextLockID() + 1);
	}
	
	/** Get the ID for the next key
	 * @return int - The ID of the next Key
	 */
	public int getNextKeyID(){
		return plugin.locked.getConfig().getInt("NextKeyID");
	}
	
	/** Increment 1 to the next key ID */
	private void setNextKeyID(){
		plugin.locked.getConfig().set("NextKeyID", getNextKeyID() + 1);
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
	
	/** Get the Unbinded Lock ID of the block
	 * @param block - The block to gather information from
	 * @deprecated This will return an unbinded String variation of the LockID (Not the KeyID). 
	 * Do not use to check lock owners
	 * @return int - ID binded to the chest
	 */
	@Deprecated
	public int getBlockLockID(Block block){
		return ram.getLockID(block.getLocation());
	}
	
	/** Get the Key ID of the block
	 * @param block - The block to gather information from
	 * @return int - Key ID binded to the chest
	 */
	public int getBlockKeyID(Block block){
		return ram.getKeyID(block.getLocation());
	}
	
	/** Get the owner of the block
	 * @param block - The block to gather information from
	 * @deprecated This will only return the username. Do not use for lock checking purposes
	 * @return A String value of the Owner's last-seen username
	 */
	@Deprecated
	public String getBlockOwner(Block block){
		return plugin.locked.getConfig().getString(String.valueOf(getBlockLockID(block)) + ".PlayerName");
	}
	
	/** Get the owner of the block's UUID
	 * @param block - The block to gather information from
	 * @return A String value of the Owner's UUID
	 */
	public String getBlockOwnerUUID(Block block){
		return plugin.locked.getConfig().getString(String.valueOf(getBlockLockID(block)) + ".OwnerUUID");
	}
	
	/** Gather all the blocks that the player has locked
	 * @param player - The player to look up in the database
	 * @return An ArrayList of the Lock ID's that the player own's
	 */
	public ArrayList<Integer> getAllLocks(OfflinePlayer player){
		ArrayList<Integer> ids = new ArrayList<Integer>();
		Set<String> keys = plugin.locked.getConfig().getKeys(false);
		keys.remove("NextLockID");
		keys.remove("NextKeyID");
		for (String key : keys){
			int id;
			try{
				id = Integer.parseInt(key);
				if (plugin.locked.getConfig().getString(id + ".OwnerUUID").equals(player.getUniqueId().toString()))
					ids.add(id);
			}catch(NumberFormatException e){
				continue;
			}
		}
		return ids;
	}
	
	private void dualComponentBlockHandler(Block block, Player owner){
		String blockType = block.getType().toString();
		String playerUUID = owner.getUniqueId().toString(); String playerName = owner.getName();
		
		Material type = block.getType();
		if (type.toString().contains("DOOR")){
			if (block.getRelative(BlockFace.UP).getType().equals(type)){
				addLockedYMLInformation(playerUUID, playerName, getNextKeyID(), blockType, block.getLocation().add(0, 1, 0));
				setNextLockID();
			}
			else if (block.getRelative(BlockFace.DOWN).getType().equals(type)){
				addLockedYMLInformation(playerUUID, playerName, getNextKeyID(), blockType, block.getLocation().add(0, -1, 0));
				setNextLockID();
			}
		}
		if (type.equals(Material.CHEST) || type.equals(Material.TRAPPED_CHEST)){
			if (block.getRelative(BlockFace.NORTH).getType().equals(type)){
				addLockedYMLInformation(playerUUID, playerName, getNextKeyID(), blockType, block.getLocation().add(0, 0, -1));
				setNextLockID();
			}
			else if (block.getRelative(BlockFace.SOUTH).getType().equals(type)){
				addLockedYMLInformation(playerUUID, playerName, getNextKeyID(), blockType, block.getLocation().add(1, 0, 0));
				setNextLockID();
			}
			else if (block.getRelative(BlockFace.EAST).getType().equals(type)){
				addLockedYMLInformation(playerUUID, playerName, getNextKeyID(), blockType, block.getLocation().add(0, 0, 1));
				setNextLockID();
			}
			else if (block.getRelative(BlockFace.WEST).getType().equals(type)){
				addLockedYMLInformation(playerUUID, playerName, getNextKeyID(), blockType, block.getLocation().add(-1, 0, 0));
				setNextLockID();
			}
		}
	}
	
	private void addLockedYMLInformation(String playerUUID, String playerName, int keyID, String blockType, Location location){
		plugin.locked.getConfig().set(getNextLockID() + ".OwnerUUID", playerUUID);
		plugin.locked.getConfig().set(getNextLockID() + ".PlayerName", playerName);
		plugin.locked.getConfig().set(getNextLockID() + ".KeyID", keyID);
		plugin.locked.getConfig().set(getNextLockID() + ".BlockType", blockType);
		plugin.locked.getConfig().set(getNextLockID() + ".Location.X", location.getBlockX());
		plugin.locked.getConfig().set(getNextLockID() + ".Location.Y", location.getBlockY());
		plugin.locked.getConfig().set(getNextLockID() + ".Location.Z", location.getBlockZ());
		plugin.locked.getConfig().set(getNextLockID() + ".Location.World", location.getWorld().getName());
		ram.addLockInformation(location, getNextLockID(), getNextKeyID());
	}
	
	private void removeCurrentItem(Player player){
		if (player.getItemInHand().getAmount() > 1){
			player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
		}
		else{
			player.getInventory().setItemInHand(new ItemStack(Material.AIR));
		}
	}
}