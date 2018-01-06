package me.choco.locksecurity.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.ILockSecurityPlayer;
import me.choco.locksecurity.api.ILockedBlock;
import me.choco.locksecurity.api.LSMode;
import me.choco.locksecurity.registration.LockedBlockManager;
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
public class LockSecurityPlayer implements ILockSecurityPlayer {
	
	private static final LockSecurity plugin = LockSecurity.getPlugin();
	
	private final File jsonDataFile;
	
	private final List<ILockedBlock> ownedBlocks = new ArrayList<>();
	private final Set<LSMode> activeModes = new HashSet<>();
	
	private ILockSecurityPlayer transferTarget;
	private UUID uuid;
	
	/**
	 * Construct a new LSPlayer based on an existing Player's UUID
	 * 
	 * @param uuid the player UUID
	 */
	public LockSecurityPlayer(UUID uuid) {
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
	
	@Override
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}
	
	@Override
	public UUID getUniqueId() {
		return uuid;
	}
	
	@Override
	public List<ILockedBlock> getOwnedBlocks() {
		return ImmutableList.copyOf(ownedBlocks);
	}
	
	@Override
	public void addBlockToOwnership(ILockedBlock block) {
		if (!block.getOwner().equals(this))
			throw new IllegalStateException("Unable to register a locked block to a user that does not own it");
		
		if (ownedBlocks.contains(block)) return;
		this.ownedBlocks.add(block);
	}
	
	@Override
	public void removeBlockFromOwnership(ILockedBlock block) {
		this.ownedBlocks.remove(block);
	}
	
	@Override
	public boolean ownsBlock(ILockedBlock block) {
		return ownedBlocks.contains(block);
	}
	
	@Override
	public void enableMode(LSMode mode) {
		this.activeModes.add(mode);
	}
	
	@Override
	public void disableMode(LSMode mode) {
		this.activeModes.remove(mode);
	}
	
	@Override
	public boolean toggleMode(LSMode mode) {
		if (activeModes.contains(mode)) this.activeModes.remove(mode);
		else this.activeModes.add(mode);
		
		return this.isModeEnabled(mode);
	}
	
	@Override
	public boolean isModeEnabled(LSMode mode) {
		return this.activeModes.contains(mode);
	}
	
	@Override
	public Set<LSMode> getEnabledModes() {
		return ImmutableSet.copyOf(activeModes);
	}
	
	@Override
	public void setTransferTarget(ILockSecurityPlayer target) {
		this.transferTarget = target;
	}
	
	@Override
	public ILockSecurityPlayer getTransferTarget() {
		return transferTarget;
	}
	
	@Override
	public File getJSONDataFile() {
		return jsonDataFile;
	}
	
	@Override
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
		for (ILockedBlock block : this.ownedBlocks) {
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
		if (!(object instanceof LockSecurityPlayer)) return false;
		
		LockSecurityPlayer other = (LockSecurityPlayer) object;
		if (uuid == null) {
			if (other.uuid != null) return false;
		} else if (!uuid.equals(other.uuid)) return false;
		
		return true;
	}
	
}