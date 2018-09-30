package wtf.choco.locksecurity.data;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.api.LSMode;
import wtf.choco.locksecurity.registration.LockedBlockManager;
import wtf.choco.locksecurity.utils.JSONSerializable;
import wtf.choco.locksecurity.utils.JSONUtils;

/**
 * A wrapper class for the OfflinePlayer interface containing information about a player's
 * LockSecurity details such as (but not limited to) their data file, owned blocks, active
 * modes ({@link LSMode}), etc.
 * <p>
 * <b>NOTE:</b> Information regarding owned locked blocks is a record of what locked blocks
 * currently exist. Not all owned blocks are registered in the {@link LockedBlockManager},
 * meaning unregistered blocks will be ignored in a locked block lookup / protection listener
 * 
 * @author Parker Hawke - 2008Choco
 */
public class LockSecurityPlayer implements JSONSerializable {
	
	private static final LockSecurity plugin = LockSecurity.getPlugin();
	
	private final File jsonDataFile;
	
	private final Set<LockedBlock> ownedBlocks = new HashSet<>();
	private final Set<LSMode> activeModes = EnumSet.noneOf(LSMode.class);
	
	private LockSecurityPlayer transferTarget;
	private UUID uuid;
	
	/**
	 * Construct a new LSPlayer based on an existing Player's UUID
	 * 
	 * @param uuid the player UUID
	 */
	public LockSecurityPlayer(UUID uuid) {
		Preconditions.checkArgument(uuid != null, "Player UUID cannot be null");
		
		this.uuid = uuid;
		this.jsonDataFile = new File(plugin.playerdataDir, uuid + ".json");
		
		if (!jsonDataFile.exists()) {
			try {
				this.jsonDataFile.createNewFile();
				JSONUtils.writeJSON(jsonDataFile, this.write(new JsonObject()));
			} catch (IOException e) {}
		}
	}
	
	/**
	 * Construct a new LSPlayer based on an existing OfflinePlayer
	 * 
	 * @param player the player
	 */
	public LockSecurityPlayer(OfflinePlayer player) {
		this(player.getUniqueId());
	}
	
	/**
	 * Get the {@link OfflinePlayer} instance this object represents
	 * 
	 * @return the represented player
	 */
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	/**
	 * Get the UUID of the represented {@link OfflinePlayer} instance
	 * 
	 * @return the represented player UUID
	 */
	public UUID getUniqueId() {
		return uuid;
	}

	/**
	 * Get an immutable set of all blocks owned by this player
	 * 
	 * @return a set of all owned blocks
	 */
	public Set<LockedBlock> getOwnedBlocks() {
		return Collections.unmodifiableSet(ownedBlocks);
	}

	/**
	 * Add a block to this player's ownership. This does not register the block to
	 * the {@link LockedBlockManager}
	 * 
	 * @param block the block to add to ownership
	 */
	public void addBlockToOwnership(LockedBlock block) {
		Preconditions.checkArgument(block != null, "Null blocks cannot be added to ownership");
		Preconditions.checkArgument(block.getOwner() == this, "Unable to register a locked block to a user that does not own it");
		
		this.ownedBlocks.add(block);
	}

	/**
	 * Remove a block from this players ownership
	 * 
	 * @param block the block to remove
	 */
	public void removeBlockFromOwnership(LockedBlock block) {
		this.ownedBlocks.remove(block);
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
	 * Enable a mode for this player
	 * 
	 * @param mode the mode to enable
	 */
	public void enableMode(LSMode mode) {
		Preconditions.checkArgument(mode != null, "Cannot enable a null mode");
		this.activeModes.add(mode);
	}

	/**
	 * Disable a mode for this player
	 * 
	 * @param mode the mode to disable
	 */
	public void disableMode(LSMode mode) {
		Preconditions.checkArgument(mode != null, "Cannot disable a null mode");
		this.activeModes.remove(mode);
	}

	/**
	 * Toggle the enable / disable state of the specified mode for this player
	 * 
	 * @param mode the mode to toggle
	 * @return true if set enabled, false if set disabled
	 */
	public boolean toggleMode(LSMode mode) {
		Preconditions.checkArgument(mode != null, "Cannot toggle a null mode");
		
		if (activeModes.contains(mode)) {
			this.activeModes.remove(mode);
		} else {
			this.activeModes.add(mode);
		}
		
		return activeModes.contains(mode);
	}

	/**
	 * Check if a mode is currently enabled for this player
	 * 
	 * @param mode the mode to check
	 * @return true if the specified mode is enabled
	 */
	public boolean isModeEnabled(LSMode mode) {
		return this.activeModes.contains(mode);
	}

	/**
	 * Get an immutable set of all currently enabled modes for this player
	 * 
	 * @return a set of enabled modes
	 */
	public Set<LSMode> getEnabledModes() {
		return Collections.unmodifiableSet(activeModes);
	}

	/**
	 * Set the player that this player is going to transfer the selected block to
	 * 
	 * @param target the player to transfer to
	 */
	public void setTransferTarget(LockSecurityPlayer target) {
		this.transferTarget = target;
	}

	/**
	 * Get the player that will receive any transferred blocks from this player
	 * 
	 * @return the transfer target
	 */
	public LockSecurityPlayer getTransferTarget() {
		return transferTarget;
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
	 * Clear all localized data for this player
	 */
	public void clearLocalData() {
		this.activeModes.clear();
		this.ownedBlocks.clear();
		this.transferTarget = null;
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
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return 31 + ((uuid == null) ? 0 : uuid.hashCode());
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof LockSecurityPlayer)) return false;
		
		LockSecurityPlayer other = (LockSecurityPlayer) object;
		return Objects.equals(uuid, other.uuid);
	}
	
}