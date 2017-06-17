package me.choco.locksecurity.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.registration.LockedBlockManager;
import me.choco.locksecurity.registration.StatsHandler;
import me.choco.locksecurity.utils.json.JSONSerializable;
import me.choco.locksecurity.utils.json.JSONUtils;

/** 
 * A wrapper class for the OfflinePlayer interface containing information about a player's
 * LockSecurity details such as (but not limited to); their data file, their owned blocks, their
 * active modes ({@link LSMode}), etc.
 * <p>
 * <b>NOTE:</b> Information regarding owned locked blocks is a record of what locked blocks 
 * currently exist. Not all owned blocks are registered in the {@link LockedBlockManager}, 
 * meaning unregistered blocks will be ignored in a locked block lookup / protection listener
 * 
 * @author Parker Hawke - 2008Choco
 */
public class LSPlayer implements JSONSerializable {
	
	private static final LockSecurity plugin = LockSecurity.getPlugin();
	
	private final File jsonDataFile;
	
	private final Set<LockedBlock> ownedBlocks = new HashSet<>();
	private final Set<LSMode> activeModes = new HashSet<>();
	
	private StatsHandler statsHandler;
	
	private LSPlayer toTransferTo;
	
	private UUID uuid;
	
	/**
	 * Construct a new LSPlayer based on an existing Player's UUID
	 * 
	 * @param uuid the player UUID
	 */
	public LSPlayer(UUID uuid) {
		this.uuid = uuid;
		
		this.jsonDataFile = new File(plugin.playerdataDir, uuid + ".json");
		if (!jsonDataFile.exists()) {
			try{
				jsonDataFile.createNewFile();
				JSONUtils.writeJSON(jsonDataFile, this.write(new JsonObject()));
			}catch(IOException e) {};
		}
	}
	
	/**
	 * Construct a new LSPlayer based on an existing OfflinePlayer
	 * 
	 * @param player the player
	 */
	public LSPlayer(OfflinePlayer player) {
		this(player.getUniqueId());
	}
	
	/** 
	 * Get the player this object represents
	 * 
	 * @return the player
	 */
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}
	
	/**
	 * Get the UUID associated with this LSPlayer
	 * 
	 * @return the associated UUID
	 */
	public UUID getUUID() {
		return uuid;
	}
	
	/** 
	 * Get a set of all blocks owned by this player
	 * 
	 * @return a set of all owned blocks
	 */
	public Set<LockedBlock> getOwnedBlocks() {
		return ImmutableSet.copyOf(ownedBlocks);
	}
	
	/** 
	 * Add a block to this player's ownership. This does not register the block
	 * 
	 * @param block the block to add to ownership
	 */
	public void addBlockToOwnership(LockedBlock block) {
		if (!block.getOwner().equals(this))
			throw new IllegalStateException("Unable to register a locked block to a user that does not own it");
		
		if (ownedBlocks.contains(block)) return;
		this.ownedBlocks.add(block);
	}
	
	/** 
	 * Remove a block from this players ownership
	 * 
	 * @param block the block to remove
	 */
	public void removeBlockFromOwnership(LockedBlock block) {
		ownedBlocks.remove(block);
	}
	
	/** 
	 * Check if the player owns the specified block or not
	 * 
	 * @param block the block to check
	 * @return true if the player owns this block
	 */
	public boolean ownsBlock(LockedBlock block) {
		return ownedBlocks.contains(block);
	}
	
	/** 
	 * Enable a mode for the player
	 * 
	 * @param mode the mode to enable
	 */
	public void enableMode(LSMode mode) {
		this.activeModes.add(mode);
	}
	
	/** 
	 * Disable a mode for the player
	 * 
	 * @param mode the mode to disable
	 */
	public void disableMode(LSMode mode) {
		this.activeModes.remove(mode);
	}
	
	/** 
	 * Toggle the mode either enabled or disabled, depending on it's current state
	 * 
	 * @param mode the mode to toggle
	 * @return true if set enabled, false if set disabled
	 */
	public boolean toggleMode(LSMode mode) {
		if (activeModes.contains(mode)) this.activeModes.remove(mode);
		else this.activeModes.add(mode);
		
		return this.isModeActive(mode);
	}
	
	/** 
	 * Check if a mode is currently active for this player
	 * 
	 * @param mode the mode to check
	 * @return true if the specified mode is active
	 */
	public boolean isModeActive(LSMode mode) {
		return this.activeModes.contains(mode);
	}
	
	/** 
	 * Get a set of all currently active modes for this player
	 * 
	 * @return a set of active modes
	 */
	public Set<LSMode> getActiveModes() {
		return ImmutableSet.copyOf(activeModes);
	}
	
	/**
	 * Set the player that this player is going to transfer an active block to
	 * 
	 * @param toTransferTo the player to transfer to
	 */
	public void setToTransferTo(LSPlayer toTransferTo) {
		this.toTransferTo = toTransferTo;
	}
	
	/**
	 * Get the player that is in progress of being transfered to
	 * 
	 * @return the transfer to target
	 */
	public LSPlayer getToTransferTo() {
		return toTransferTo;
	}
	
	/** 
	 * Get the JSON data file that keeps track of offline information for this user
	 * 
	 * @return the player's JSON data file
	 */
	public File getJSONDataFile() {
		return jsonDataFile;
	}
	
	/**
	 * Get the statistics for this player
	 * 
	 * @return player statistics
	 */
	public StatsHandler getStatsHandler() {
		return statsHandler;
	}
	
	/**
	 * Clear all localized data for this player
	 */
	public void clearLocalData() {
		this.activeModes.clear();
		this.ownedBlocks.clear();
		this.toTransferTo = null;
	}

	@Override
	public JsonObject write(JsonObject data) {
		data.addProperty("uuid", uuid.toString());
		
		JsonArray activeModesData = new JsonArray();
		for (LSMode mode : this.activeModes) {
			activeModesData.add(mode.getName());
		}
		
		data.add("activeModes", activeModesData);
		
		JsonArray ownedBlocksData = new JsonArray();
		for (LockedBlock block : this.ownedBlocks) {
			ownedBlocksData.add(block.write(new JsonObject()));
		}
		
		data.add("ownedBlocks", ownedBlocksData);
		data.add("statistics", statsHandler.statsToJson());
		
		return data;
	}

	@Override
	public boolean read(JsonObject data) {
		this.uuid = UUID.fromString(data.get("uuid").getAsString());
		
		JsonArray activeModesData = data.has("activeModes") ? data.getAsJsonArray("activeModes") : new JsonArray();
		for (int i = 0; i < activeModesData.size(); i++) {
			LSMode mode = LSMode.getByName(activeModesData.get(i).getAsString());
			if (mode == null) continue;
			
			this.activeModes.add(mode);
		}
		
		JsonArray ownedBlocksData = data.has("ownedBlocks") ? data.getAsJsonArray("ownedBlocks") : new JsonArray();
		for (int i = 0; i < ownedBlocksData.size(); i++) {
			JsonObject blockData = ownedBlocksData.get(i).getAsJsonObject();
			LockedBlock block = new LockedBlock(blockData);
			
			this.ownedBlocks.add(block);
		}
		
		JsonObject statisticsData = data.has("statistics") ? data.getAsJsonObject("statistics") : new JsonObject();
		this.statsHandler = new StatsHandler(statisticsData);
		
		return true;
	}
}