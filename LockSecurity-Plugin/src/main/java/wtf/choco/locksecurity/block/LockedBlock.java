package wtf.choco.locksecurity.block;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.api.block.ILockedBlock;
import wtf.choco.locksecurity.api.impl.block.LockedBlockWrapper;
import wtf.choco.locksecurity.key.KeyFactory;
import wtf.choco.locksecurity.player.LockSecurityPlayer;

public class LockedBlock {

    private Block block;
    private LockSecurityPlayer owner;
    private ZonedDateTime lockTime;
    private String nickname;

    protected ILockedBlock apiWrapper;

    public LockedBlock(Block block, LockSecurityPlayer owner, ZonedDateTime lockTime, String nickname) {
        Preconditions.checkArgument(block != null, "block cannot be null");
        Preconditions.checkArgument(owner != null, "owner cannot be null");
        Preconditions.checkArgument(lockTime != null, "lockTime cannot be null");

        this.block = block;
        this.owner = owner;
        this.lockTime = lockTime;
        this.nickname = nickname;

        this.apiWrapper = initAPIWrapper();
    }

    public LockedBlock(Block block, LockSecurityPlayer owner, ZonedDateTime lockTime) {
        this(block, owner, lockTime, null);
    }

    public LockedBlock(JsonObject object) {
        this.read(object);
        this.apiWrapper = initAPIWrapper();
    }

    public Block getBlock() {
        return block;
    }

    public Material getType() {
        return block.getType();
    }

    public Location getLocation() {
        return block.getLocation();
    }

    public World getWorld() {
        return block.getWorld();
    }

    public int getX() {
        return block.getX();
    }

    public int getY() {
        return block.getY();
    }

    public int getZ() {
        return block.getZ();
    }

    public void setOwner(LockSecurityPlayer owner) {
        Preconditions.checkArgument(owner != null, "owner must not be null");
        this.owner = owner;
    }

    public LockSecurityPlayer getOwner() {
        return owner;
    }

    public boolean isOwner(OfflinePlayer player) {
        return owner.is(player);
    }

    public boolean isOwner(LockSecurityPlayer player) {
        return owner.equals(player);
    }

    public ZonedDateTime getLockTime() {
        return lockTime;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean hasNickname() {
        return nickname != null;
    }

    public boolean isValidKey(ItemStack key) {
        return key != null && ArrayUtils.contains(KeyFactory.SMITHED.getUnlocks(key), this);
    }

    public JsonObject write(JsonObject object) {
        object.addProperty("owner", owner.getUniqueId().toString());

        JsonObject locationObject = new JsonObject();
        {
            locationObject.addProperty("world", getWorld().getUID().toString());
            locationObject.addProperty("x", getX());
            locationObject.addProperty("y", getY());
            locationObject.addProperty("z", getZ());
        }

        object.add("location", locationObject);
        object.addProperty("lockTime", lockTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

        if (nickname != null) {
            object.addProperty("nickname", nickname);
        }

        return object;
    }

    public LockedBlock read(JsonObject object) {
        Preconditions.checkState(object.has("owner"), "Attempted to read locked block with missing owner");
        Preconditions.checkState(object.has("location"), "Attempted to read locked block with missing location");

        UUID playerUUID = UUID.fromString(object.get("owner").getAsString());
        this.owner = LockSecurity.getInstance().getPlayer(Bukkit.getOfflinePlayer(playerUUID));

        JsonObject locationObject = object.getAsJsonObject("location");
        {
            Preconditions.checkArgument(locationObject.has("world"), "Attempted to read locked block with invalid location. Missing world");
            Preconditions.checkArgument(locationObject.has("x"), "Attempted to read locked block with invalid location. Missing x");
            Preconditions.checkArgument(locationObject.has("y"), "Attempted to read locked block with invalid location. Missing y");
            Preconditions.checkArgument(locationObject.has("z"), "Attempted to read locked block with invalid location. Missing z");

            UUID worldUUID = UUID.fromString(locationObject.get("world").getAsString());
            World world = Bukkit.getWorld(worldUUID);
            Preconditions.checkArgument(world != null, "Missing world with UUID " + worldUUID);

            int x = locationObject.get("x").getAsInt();
            int y = locationObject.get("y").getAsInt();
            int z = locationObject.get("z").getAsInt();
            this.block = world.getBlockAt(x, y, z);
        }

        if (object.has("lockTime")) {
            try {
                this.lockTime = ZonedDateTime.parse(object.get("lockTime").getAsString());
            } catch (DateTimeParseException e) {
                this.lockTime = ZonedDateTime.now();

                Logger logger = LockSecurity.getInstance().getLogger();
                logger.warning("Invalid ISO zoned date specification while loading block at (" + getX() + ", " + getY() + ", " + getZ() + ") in world " + getWorld().getName());
                logger.warning("Ignoring... assuming block was locked now instead (" + lockTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME) + ")");
            }
        }

        if (object.has("nickname")) {
            this.nickname = object.get("nickname").getAsString();
        }

        return this;
    }

    protected ILockedBlock initAPIWrapper() {
        return new LockedBlockWrapper(this);
    }

    public ILockedBlock getAPIWrapper() {
        return apiWrapper;
    }

    @Override
    public int hashCode() {
        return 31 * block.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof LockedBlock && Objects.equals(block, ((LockedBlock) other).block));
    }

    @Override
    public String toString() {
        return String.format("LockedBlock:{OwnerUUID:\"%s\", Location:{World:\"%s\", X:%d, Y:%d, Z:%d}}", owner, getWorld().getName(), getX(), getY(), getZ());
    }

}
