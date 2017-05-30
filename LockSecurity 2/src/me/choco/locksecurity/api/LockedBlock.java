package me.choco.locksecurity.api;

import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.exception.IllegalBlockPositionException;
import me.choco.locksecurity.registration.LockedBlockManager;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.LSPlayer;
import me.choco.locksecurity.utils.json.JSONSerializable;

/** 
 * Represents a block in which contains information about its owner, Lock ID, Key ID
 * and position in the world. This may take various forms as different types of blocks may be locked
 * 
 * @author Parker Hawke - 2008Choco
 */
public class LockedBlock implements JSONSerializable {
	
	private static final PlayerRegistry PLAYER_REGISTRY = LockSecurity.getPlugin().getPlayerRegistry();
	
	private LockedBlock secondaryComponent;
	
	private LSPlayer owner;
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
	public LockedBlock(LSPlayer owner, Location location, int lockID, int keyID) {
		if (owner == null)
			throw new IllegalStateException("Locked blocks cannot have a null owner");
		
		this.owner = owner;
		this.location = location;
		this.lockID = lockID;
		this.keyID = keyID;
		if (!owner.ownsBlock(this)) owner.addBlockToOwnership(this);
		
		this.uuid = UUID.randomUUID();
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
	public LockedBlock(LSPlayer owner, Location location, int lockID, int keyID, LockedBlock secondaryComponent) {
		this(owner, location, lockID, keyID);
		
		if (!canBeSecondaryComponent(secondaryComponent))
			throw new IllegalBlockPositionException("Block is not positioned correctly to be a secondary component (From [LockID] = " + lockID);
		this.secondaryComponent = secondaryComponent;
		if (!owner.ownsBlock(secondaryComponent)) owner.addBlockToOwnership(secondaryComponent);
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
	public LockedBlock(LSPlayer owner, Block block, int lockID, int keyID) {
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
	public LockedBlock(LSPlayer owner, Block block, int lockID, int keyID, LockedBlock secondaryComponent) {
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
	 * Set the owner of this block. This will also modify the {@link LSPlayer#getOwnedBlocks()} list
	 * to remove from the old owner's blocks, and add it to the new owner's blocks
	 * 
	 * @param owner the owner to set
	 */
	public void setOwner(LSPlayer owner) {
		this.owner.getOwnedBlocks().remove(this);
		owner.getOwnedBlocks().add(this);
		
		this.owner = owner;
	}
	
	/** 
	 * Get the owner of the block
	 * 
	 * @return the owner of the block
	 */
	public LSPlayer getOwner() {
		return owner;
	}
	
	/** 
	 * Check if the specified player is the owner of the block
	 * 
	 * @param player the player to check
	 * @return true if the player owns this block
	 */
	public boolean isOwner(LSPlayer player) {
		return player.equals(owner);
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
	 * @param secondaryComponent the block to set as a secondary component
	 * @throws IllegalBlockPositionException if the block is not positioned correctly
	 */
	public void setSecondaryComponent(LockedBlock secondaryComponent) {
		setSecondaryComponent(secondaryComponent, false);
	}
	
	/** 
	 * Set the secondary component of this block. The secondary component MUST be
	 * of the same type and must be a valid contendor, such as DoubleChest or Door components. 
	 * This locked block and the specified component will be linked together. 
	 * <br> If forced, the components will be linked regardless of their block position / state
	 * 
	 * @param secondaryComponent the block to set as a secondary component
	 * @param force if true, the blocks will be linked together regardless of their position
	 * 
	 * @throws IllegalBlockPositionException if the block is not positioned correctly (and "force" is false)
	 */
	public void setSecondaryComponent(LockedBlock secondaryComponent, boolean force) {
		if (!force && !canBeSecondaryComponent(secondaryComponent))
			throw new IllegalBlockPositionException("Block is not positioned correctly to be a secondary component (From [LockID] = " + lockID);
		
		this.secondaryComponent = secondaryComponent;
		if (secondaryComponent.getSecondaryComponent() != null) 
			secondaryComponent.setSecondaryComponent(this);
	}
	
	private static final BlockFace[] FACES = new BlockFace[]{ BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
	private static final BlockFace[] FACES_DOORS = new BlockFace[]{ BlockFace.UP, BlockFace.DOWN };
	
	/** 
	 * Check whether a block can successfully be a secondary component or not
	 * 
	 * @param block the block to check
	 * @return true if it can be a secondary component
	 */
	public boolean canBeSecondaryComponent(LockedBlock block) {
		if (!this.getBlock().getType().equals(block.getBlock().getType())) return false;
		
		Material material = this.getBlock().getType();
		for (BlockFace face : material.name().contains("DOOR") ? FACES_DOORS : FACES)
			if (this.getBlock().getRelative(face).equals(block.getBlock())) return true;
		return false;
	}
	
	/** 
	 * Get the secondary component for this locked block (if any)
	 * 
	 * @return the secondary component. null if none is set
	 * @see {@link #hasSecondaryComponent()}
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
		return this.secondaryComponent != null;
	}
	
	/** 
	 * Check whether the specified smithedkey is a valid key or not. A key is 
	 * considered valid if its Key ID is similar to that of this block
	 * 
	 * @param key the key to check
	 * @return true if the Key ID values are similar
	 */
	public boolean isValidKey(ItemStack key) {
		if (KeyFactory.isUnsmithedKey(key)) return false;
			
		int[] IDs = KeyFactory.getIDs(key);
		for (int ID : IDs)
			if (ID == keyID) return true;
		return false;
	}
	
	/** 
	 * Display information to a player in the chat about this locked block
	 * 
	 * @param player the player to display information to
	 */
	public void displayInformation(Player player) {
		player.sendMessage(ChatColor.GOLD + "- - - - - - " + ChatColor.DARK_AQUA + "Lock information " + ChatColor.GOLD + "- - - - - -");
		player.sendMessage(ChatColor.GOLD + "Lock ID: " + ChatColor.AQUA + lockID);
		player.sendMessage(ChatColor.GOLD + "Key ID: " + ChatColor.AQUA + keyID);
		player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + owner.getPlayer().getName() + " (" + ChatColor.GOLD + owner.getPlayer().getUniqueId() + ChatColor.AQUA + ")");
		player.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.AQUA + location.getWorld().getName() + " x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ());
	}

	@Override
	public JsonObject write(JsonObject data) {
		if (data.size() > 0) return data;
		
		data.addProperty("uuid", uuid.toString());
		data.addProperty("lockID", lockID);
		data.addProperty("keyID", keyID);
		data.addProperty("owner", owner.getPlayer().getUniqueId().toString());
		
		JsonObject locationData = new JsonObject();
		locationData.addProperty("world", this.location.getWorld().getName());
		locationData.addProperty("x", this.location.getBlockX());
		locationData.addProperty("y", this.location.getBlockY());
		locationData.addProperty("z", this.location.getBlockZ());
		
		data.add("location", locationData);
		if (secondaryComponent != null) data.addProperty("secondaryComponent", secondaryComponent.getUniqueId().toString());
		return data;
	}

	@Override
	public boolean read(JsonObject data) {
		this.uuid = UUID.fromString(data.get("uuid").getAsString());
		this.lockID = data.get("lockID").getAsInt();
		this.keyID = data.get("keyID").getAsInt();
		this.owner = PLAYER_REGISTRY.getPlayer(UUID.fromString(data.get("owner").getAsString()));
		
		JsonObject locationData = data.getAsJsonObject("location");
		World world = Bukkit.getWorld(locationData.get("world").getAsString());
		if (world == null) return false;
		
		int x = locationData.get("x").getAsInt();
		int y = locationData.get("y").getAsInt();
		int z = locationData.get("z").getAsInt();
		this.location = new Location(world, x, y, z);
		
		return true;
	}
}