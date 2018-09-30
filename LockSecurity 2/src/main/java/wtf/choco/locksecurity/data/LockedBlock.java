package wtf.choco.locksecurity.data;

import java.util.Objects;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.api.KeyFactory;
import wtf.choco.locksecurity.api.exception.IllegalBlockPositionException;
import wtf.choco.locksecurity.registration.LockedBlockManager;
import wtf.choco.locksecurity.utils.JSONSerializable;

/**
 * Represents a block in which contains information about its owner, Lock ID, Key ID
 * and position in the world. This may take various forms as different types of blocks may be locked
 * 
 * @author Parker Hawke - 2008Choco
 */
public class LockedBlock implements JSONSerializable {
	
	private LockedBlock secondaryComponent;
	
	private LockSecurityPlayer owner;
	private Location location;
	private int lockID, keyID;
	private UUID uuid;
	
	/**
	 * Construct a new LockedBlock. This will not register the block in the {@link LockedBlockManager},
	 * but will add this to the owner's possession
	 * 
	 * @param owner the owner of the block
	 * @param location the location of the locked block
	 * @param lockID the lock ID to associate with the block
	 * @param keyID the key ID to associate with the block
	 */
	public LockedBlock(LockSecurityPlayer owner, Location location, int lockID, int keyID) {
		Preconditions.checkArgument(owner != null, "Locked blocks cannot have null owners");
		Preconditions.checkArgument(location != null, "Invalid location specified: null");
		
		this.owner = owner;
		this.location = location;
		this.lockID = lockID;
		this.keyID = keyID;
		this.uuid = UUID.randomUUID();
		
		if (!owner.ownsBlock(this)) {
			owner.addBlockToOwnership(this);
		}
	}
	
	/**
	 * Construct a new LockedBlock with a secondary component (i.e. a door's upper half or a
	 * double chest's second half). This will not register the block in the {@link LockedBlockManager},
	 * but will add this to the owner's possession
	 * 
	 * @param owner the owner of the block
	 * @param location the location of the locked block
	 * @param lockID the lock ID to associate with the block
	 * @param keyID the key ID to associate with the block
	 * @param secondaryComponent the secondary component
	 */
	public LockedBlock(LockSecurityPlayer owner, Location location, int lockID, int keyID, LockedBlock secondaryComponent) {
		this(owner, location, lockID, keyID);
		
		if (!canBeSecondaryComponent(secondaryComponent))
			throw new IllegalBlockPositionException("Block is not positioned correctly to be a secondary component (From [LockID] = " + lockID);
		this.secondaryComponent = secondaryComponent;
		
		if (!owner.ownsBlock(secondaryComponent)) {
			owner.addBlockToOwnership(secondaryComponent);
		}
	}
	
	/**
	 * Construct a new LockedBlock. This will not register the block in the {@link LockedBlockManager},
	 * but will add this to the owner's possession
	 * 
	 * @param owner the owner of the block
	 * @param block the block representing this locked block
	 * @param lockID the lock ID to associate with the block
	 * @param keyID the key ID to associate with the block
	 */
	public LockedBlock(LockSecurityPlayer owner, Block block, int lockID, int keyID) {
		this(owner, block.getLocation(), lockID, keyID);
	}
	
	/**
	 * Construct a new LockedBlock with a secondary component (i.e. a door's upper half or a
	 * double chest's second half). This will not register the block in the {@link LockedBlockManager},
	 * but will add this to the owner's possession
	 * 
	 * @param owner the owner of the block
	 * @param block the block representing this locked block
	 * @param lockID the lock ID to associate with the block
	 * @param keyID the key ID to associate with the block
	 * @param secondaryComponent the secondary component
	 */
	public LockedBlock(LockSecurityPlayer owner, Block block, int lockID, int keyID, LockedBlock secondaryComponent) {
		this(owner, block.getLocation(), lockID, keyID, secondaryComponent);
	}
	
	/**
	 * Construct a new LockedBlock from JSON data. This should only be used internally, and
	 * is not available for public use outside of the me.choco.locksecurity.LockSecurity class
	 * 
	 * @param data the LockedBlock JSON data
	 */
	public LockedBlock(JsonObject data) {
		if (!this.read(data))
			throw new JsonParseException("LockedBlock data parsing failed for LockID=" + lockID);
	}
	
	/**
	 * Set the owner of this block. This will also modify the {@link LockSecurityPlayer#getOwnedBlocks()}
	 * list to remove from the old owner's blocks, and add it to the new owner's blocks
	 * 
	 * @param owner the owner to set
	 */
	public void setOwner(LockSecurityPlayer owner) {
		Preconditions.checkArgument(owner != null, "Owner must not be null");
		this.owner.removeBlockFromOwnership(this);
		
		this.owner = owner;
		this.owner.addBlockToOwnership(this);
	}
	
	/**
	 * Get the owner of the block
	 * 
	 * @return the owner of the block
	 */
	public LockSecurityPlayer getOwner() {
		return owner;
	}

	/**
	 * Check if the specified player is the owner of the block or not
	 * 
	 * @param player the player to check
	 * @return true if the player owns this block, false otherwise
	 */
	public boolean isOwner(LockSecurityPlayer player) {
		return (player == owner);
	}

	/**
	 * Check whether the provided player is the owner of the block or not
	 * 
	 * @param player the player to check
	 * @return true if the player owns this block, false otherwise
	 */
	public boolean isOwner(OfflinePlayer player) {
		return player != null && isOwner(player.getUniqueId());
	}

	/**
	 * Check whether the provided player UUID is the owner of the block
	 * or not
	 * 
	 * @param player the player UUID to check
	 * @return true if the player UUID owns this block, false otherwise
	 */
	public boolean isOwner(UUID player) {
		return player != null && owner.getUniqueId().equals(player);
	}

	/**
	 * Get the location in which this locked block is located
	 * 
	 * @return the location of the block
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Get the block in which this locked block represents
	 * 
	 * @return the block this locked block represents
	 */
	public Block getBlock() {
		return location.getBlock();
	}

	/**
	 * Get the Lock ID value that identifies this block
	 * 
	 * @return the Lock ID value
	 */
	public int getLockID() {
		return lockID;
	}

	/**
	 * Get the Key ID value required to open this block
	 * 
	 * @return the required Key ID value
	 */
	public int getKeyID() {
		return keyID;
	}

	/**
	 * Get the unique string of characters that represent this block
	 * 
	 * @return the UUID of the block
	 */
	public UUID getUniqueId() {
		return uuid;
	}

	/**
	 * Set the secondary component of this block. The secondary component MUST be
	 * of the same type and must be a valid contendor, such as DoubleChest or Door components.
	 * This locked block and the specified component will be linked together
	 * 
	 * @param component the block to set as a secondary component
	 * @throws IllegalBlockPositionException if the block is not positioned correctly
	 */
	public void setSecondaryComponent(LockedBlock component) {
		this.setSecondaryComponent(component, false);
	}

	/**
	 * Set the secondary component of this block. The secondary component MUST be
	 * of the same type and must be a valid contendor, such as DoubleChest or Door components.
	 * This locked block and the specified component will be linked together.
	 * <br> If forced, the components will be linked regardless of their block position / state
	 * 
	 * @param component the block to set as a secondary component
	 * @param force if true, the blocks will be linked together regardless of their position
	 * 
	 * @throws IllegalBlockPositionException if the block is not positioned correctly (and "force" is false)
	 */
	public void setSecondaryComponent(LockedBlock component, boolean force) {
		if (!force && !canBeSecondaryComponent(component)) {
			throw new IllegalBlockPositionException("Block is not positioned correctly to be a secondary component (From [LockID] = " + lockID);
		}
		
		this.secondaryComponent = component;
		if (secondaryComponent.getSecondaryComponent() != null)
			this.secondaryComponent.setSecondaryComponent(this);
	}
	
	private static final BlockFace[] FACES = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
	private static final BlockFace[] FACES_DOORS = new BlockFace[] { BlockFace.UP, BlockFace.DOWN };

	/**
	 * Check whether a block can successfully be a secondary component or not
	 * 
	 * @param block the block to check
	 * @return true if it can be a secondary component
	 */
	public boolean canBeSecondaryComponent(LockedBlock block) {
		if (block == null || this.getBlock().getType() != block.getBlock().getType()) return false;
		
		Material material = this.getBlock().getType();
		for (BlockFace face : material.name().contains("DOOR") ? FACES_DOORS : FACES)
			if (this.getBlock().getRelative(face).equals(block.getBlock())) return true;
		return false;
	}

	/**
	 * Get the secondary component for this locked block (if any)
	 * 
	 * @return the secondary component. null if none is set
	 * @see #hasSecondaryComponent()
	 */
	public LockedBlock getSecondaryComponent() {
		return secondaryComponent;
	}

	/**
	 * Check whether this block has a secondary component or not
	 * 
	 * @return true if this block has a secondary component
	 */
	public boolean hasSecondaryComponent() {
		return secondaryComponent != null;
	}

	/**
	 * Check whether the specified smithedkey is a valid key or not. A key is
	 * considered valid if its Key ID is similar to that of this block
	 * 
	 * @param key the key to check
	 * @return true if the Key ID values are similar
	 */
	public boolean isValidKey(ItemStack key) {
		return !KeyFactory.isUnsmithedKey(key) && ArrayUtils.contains(KeyFactory.getIDs(key), keyID);
	}
	
	@Override
	public JsonObject write(JsonObject data) {
		if (data.size() > 0) return data;
		
		data.addProperty("uuid", uuid.toString());
		data.addProperty("lockID", lockID);
		data.addProperty("keyID", keyID);
		data.addProperty("owner", owner.getPlayer().getUniqueId().toString());
		
		JsonObject locationData = new JsonObject();
		locationData.addProperty("world", location.getWorld().getName());
		locationData.addProperty("x", location.getBlockX());
		locationData.addProperty("y", location.getBlockY());
		locationData.addProperty("z", location.getBlockZ());
		
		data.add("location", locationData);
		if (secondaryComponent != null) data.addProperty("secondaryComponent", secondaryComponent.getUniqueId().toString());
		return data;
	}

	@Override
	public boolean read(JsonObject data) {
		this.uuid = UUID.fromString(data.get("uuid").getAsString());
		this.lockID = data.get("lockID").getAsInt();
		this.keyID = data.get("keyID").getAsInt();
		this.owner = LockSecurity.getPlugin().getPlayer(UUID.fromString(data.get("owner").getAsString()));
		
		JsonObject locationData = data.getAsJsonObject("location");
		World world = Bukkit.getWorld(locationData.get("world").getAsString());
		if (world == null) return false;
		
		int x = locationData.get("x").getAsInt();
		int y = locationData.get("y").getAsInt();
		int z = locationData.get("z").getAsInt();
		this.location = new Location(world, x, y, z);
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int result = 31 + keyID;
		
		result = 31 * result + lockID;
		result = 31 * result + ((owner == null) ? 0 : owner.hashCode());
		result = 31 * result + ((uuid == null) ? 0 : uuid.hashCode());
		
		return result;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof LockedBlock)) return false;
		
		LockedBlock other = (LockedBlock) object;
		return keyID == other.keyID && lockID == other.lockID
			&& Objects.equals(owner, other.owner) && Objects.equals(uuid, other.uuid);
	}
	
}