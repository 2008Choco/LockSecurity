package wtf.choco.locksecurity.block;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import wtf.choco.locksecurity.api.block.ILockedBlock;
import wtf.choco.locksecurity.api.block.ILockedMultiBlock;
import wtf.choco.locksecurity.api.impl.block.LockedMultiBlockWrapper;
import wtf.choco.locksecurity.player.LockSecurityPlayer;

public class LockedMultiBlock extends LockedBlock {

    private Block secondaryBlock;

    public LockedMultiBlock(Block block, Block secondaryBlock, LockSecurityPlayer owner, ZonedDateTime lockTime, String nickname) {
        super(block, owner, lockTime, nickname);
        Preconditions.checkArgument(secondaryBlock != null, "secondaryBlock cannot be null");
        Preconditions.checkArgument(block.getLocation().distanceSquared(secondaryBlock.getLocation()) <= 1, "Locked multi blocks must have adjacent block components");

        this.secondaryBlock = secondaryBlock;
    }

    public LockedMultiBlock(Block block, Block secondaryBlock, LockSecurityPlayer owner, ZonedDateTime lockTime) {
        this(block, secondaryBlock, owner, lockTime, null);
    }

    public LockedMultiBlock(JsonObject object) {
        super(object);
        this.read(object);
    }

    public Block getSecondaryBlock() {
        return secondaryBlock;
    }

    public int getXSecondary() {
        return secondaryBlock.getX();
    }

    public int getYSecondary() {
        return secondaryBlock.getY();
    }

    public int getZSecondary() {
        return secondaryBlock.getZ();
    }

    @Override
    public JsonObject write(JsonObject object) {
        super.write(object);

        JsonObject secondaryLocationObject = new JsonObject();
        {
            secondaryLocationObject.addProperty("world", getWorld().getUID().toString());
            secondaryLocationObject.addProperty("x", getXSecondary());
            secondaryLocationObject.addProperty("y", getYSecondary());
            secondaryLocationObject.addProperty("z", getZSecondary());
        }

        object.add("secondaryLocation", secondaryLocationObject);
        return object;
    }

    @Override
    public LockedBlock read(JsonObject object) {
        Preconditions.checkState(object.has("secondaryLocation"), "Attempted to read locked block with missing secondary location");
        super.read(object);

        JsonObject secondaryLocationObject = object.getAsJsonObject("secondaryLocation");
        {
            Preconditions.checkArgument(secondaryLocationObject.has("world"), "Attempted to read locked block with invalid location. Missing world");
            Preconditions.checkArgument(secondaryLocationObject.has("x"), "Attempted to read locked block with invalid location. Missing x");
            Preconditions.checkArgument(secondaryLocationObject.has("y"), "Attempted to read locked block with invalid location. Missing y");
            Preconditions.checkArgument(secondaryLocationObject.has("z"), "Attempted to read locked block with invalid location. Missing z");

            UUID worldUUID = UUID.fromString(secondaryLocationObject.get("world").getAsString());
            World world = Bukkit.getWorld(worldUUID);
            Preconditions.checkArgument(world != null, "Missing world with UUID " + worldUUID);
            Preconditions.checkArgument(world.equals(getWorld()), "Secondary component world mismatch");

            int x = secondaryLocationObject.get("x").getAsInt();
            int y = secondaryLocationObject.get("y").getAsInt();
            int z = secondaryLocationObject.get("z").getAsInt();
            this.secondaryBlock = world.getBlockAt(x, y, z);
        }

        return this;
    }

    @Override
    protected ILockedBlock initAPIWrapper() {
        return new LockedMultiBlockWrapper(this);
    }

    @Override
    public ILockedMultiBlock getAPIWrapper() {
        return (ILockedMultiBlock) apiWrapper;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + secondaryBlock.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof LockedMultiBlock && super.equals(other) && Objects.equals(secondaryBlock, ((LockedMultiBlock) other).secondaryBlock));
    }

    @Override
    public String toString() {
        return String.format("LockedMultiBlock:{OwnerUUID:\"%s\", World:\"%s\", Positions:[{X:%d, Y:%d, Z:%d}, {X:%d, Y:%d, Z:%d}]}", getOwner().getUniqueId(), getWorld().getName(), getX(), getY(), getZ(), getXSecondary(), getYSecondary(), getZSecondary());
    }

}
