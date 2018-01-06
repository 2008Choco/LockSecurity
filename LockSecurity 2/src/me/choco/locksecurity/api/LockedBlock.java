package me.choco.locksecurity.api;

import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.exception.IllegalBlockPositionException;

public class LockedBlock implements ILockedBlock {
	
	private static final IPlayerRegistry PLAYER_REGISTRY = LockSecurity.getPlugin().getPlayerRegistry();
	
	private ILockedBlock secondaryComponent;
	
	private ILockSecurityPlayer owner;
	private Location location;
	private int lockID, keyID;
	private UUID uuid;
	
	/**
	 * Construct a new LockedBlock. This will not register the block in the {@link ILockedBlockManager},
	 * but will add this to the owner's possession
	 * 
	 * @param owner the owner of the block
	 * @param location the location of the locked block
	 * @param lockID the lock ID to associate with the block
	 * @param keyID the key ID to associate with the block
	 */
	public LockedBlock(ILockSecurityPlayer owner, Location location, int lockID, int keyID) {
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
	 * double chest's second half). This will not register the block in the {@link ILockedBlockManager},
	 * but will add this to the owner's possession
	 * 
	 * @param owner the owner of the block
	 * @param location the location of the locked block
	 * @param lockID the lock ID to associate with the block
	 * @param keyID the key ID to associate with the block
	 * @param secondaryComponent the secondary component
	 */
	public LockedBlock(ILockSecurityPlayer owner, Location location, int lockID, int keyID, ILockedBlock secondaryComponent) {
		this(owner, location, lockID, keyID);
		
		if (!canBeSecondaryComponent(secondaryComponent))
			throw new IllegalBlockPositionException("Block is not positioned correctly to be a secondary component (From [LockID] = " + lockID);
		this.secondaryComponent = secondaryComponent;
		if (!owner.ownsBlock(secondaryComponent)) owner.addBlockToOwnership(secondaryComponent);
	}
	
	/**
	 * Construct a new LockedBlock. This will not register the block in the {@link ILockedBlockManager},
	 * but will add this to the owner's possession
	 * 
	 * @param owner the owner of the block
	 * @param block the block representing this locked block
	 * @param lockID the lock ID to associate with the block
	 * @param keyID the key ID to associate with the block
	 */
	public LockedBlock(ILockSecurityPlayer owner, Block block, int lockID, int keyID) {
		this(owner, block.getLocation(), lockID, keyID);
	}
	
	/**
	 * Construct a new LockedBlock with a secondary component (i.e. a door's upper half or a 
	 * double chest's second half). This will not register the block in the {@link ILockedBlockManager},
	 * but will add this to the owner's possession
	 * 
	 * @param owner the owner of the block
	 * @param block the block representing this locked block
	 * @param lockID the lock ID to associate with the block
	 * @param keyID the key ID to associate with the block
	 * @param secondaryComponent the secondary component
	 */
	public LockedBlock(ILockSecurityPlayer owner, Block block, int lockID, int keyID, ILockedBlock secondaryComponent) {
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
	
	@Override
	public void setOwner(ILockSecurityPlayer owner) {
		this.owner.removeBlockFromOwnership(this);
		owner.addBlockToOwnership(this);
		
		this.owner = owner;
	}
	
	@Override
	public ILockSecurityPlayer getOwner() {
		return owner;
	}
	
	@Override
	public boolean isOwner(ILockSecurityPlayer player) {
		return (player == owner);
	}
	
	@Override
	public Location getLocation() {
		return location;
	}
	
	@Override
	public Block getBlock() {
		return location.getBlock();
	}
	
	@Override
	public int getLockID() {
		return lockID;
	}
	
	@Override
	public int getKeyID() {
		return keyID;
	}
	
	@Override
	public UUID getUniqueId() {
		return uuid;
	}
	
	@Override
	public void setSecondaryComponent(ILockedBlock component) {
		setSecondaryComponent(component, false);
	}
	
	@Override
	public void setSecondaryComponent(ILockedBlock component, boolean force) {
		if (!force && !canBeSecondaryComponent(component))
			throw new IllegalBlockPositionException("Block is not positioned correctly to be a secondary component (From [LockID] = " + lockID);
		
		this.secondaryComponent = component;
		if (secondaryComponent.getSecondaryComponent() != null) 
			this.secondaryComponent.setSecondaryComponent(this);
	}
	
	private static final BlockFace[] FACES = new BlockFace[]{ BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
	private static final BlockFace[] FACES_DOORS = new BlockFace[]{ BlockFace.UP, BlockFace.DOWN };
	
	@Override
	public boolean canBeSecondaryComponent(ILockedBlock block) {
		if (!this.getBlock().getType().equals(block.getBlock().getType())) return false;
		
		Material material = this.getBlock().getType();
		for (BlockFace face : material.name().contains("DOOR") ? FACES_DOORS : FACES)
			if (this.getBlock().getRelative(face).equals(block.getBlock())) return true;
		return false;
	}
	
	@Override
	public ILockedBlock getSecondaryComponent() {
		return secondaryComponent;
	}
	
	@Override
	public boolean hasSecondaryComponent() {
		return secondaryComponent != null;
	}
	
	@Override
	public boolean isValidKey(ItemStack key) {
		if (KeyFactory.isUnsmithedKey(key)) return false;
			
		int[] IDs = KeyFactory.getIDs(key);
		for (int ID : IDs)
			if (ID == keyID) return true;
		return false;
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