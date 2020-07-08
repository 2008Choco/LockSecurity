package wtf.choco.locksecurity.api.impl.block;

import java.time.ZonedDateTime;
import java.util.Objects;

import com.google.common.base.Preconditions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.api.block.ILockedBlock;
import wtf.choco.locksecurity.api.impl.player.LockSecurityPlayerWrapper;
import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;
import wtf.choco.locksecurity.block.LockedBlock;

public class LockedBlockWrapper implements ILockedBlock {

    protected final LockedBlock handle;

    public LockedBlockWrapper(LockedBlock handle) {
        this.handle = handle;
    }

    @Override
    public Block getBlock() {
        return getHandle().getBlock();
    }

    @Override
    public Material getType() {
        return getHandle().getType();
    }

    @Override
    public Location getLocation() {
        return getHandle().getLocation();
    }

    @Override
    public World getWorld() {
        return getHandle().getWorld();
    }

    @Override
    public int getX() {
        return getHandle().getX();
    }

    @Override
    public int getY() {
        return getHandle().getY();
    }

    @Override
    public int getZ() {
        return getHandle().getZ();
    }

    @Override
    public void setOwner(ILockSecurityPlayer owner) {
        Preconditions.checkArgument(owner != null, "owner must not be null");
        this.getHandle().setOwner(((LockSecurityPlayerWrapper) owner).getHandle());
    }

    @Override
    public ILockSecurityPlayer getOwner() {
        return getHandle().getOwner().getAPIWrapper();
    }

    @Override
    public boolean isOwner(OfflinePlayer player) {
        return getHandle().isOwner(player);
    }

    @Override
    public boolean isOwner(ILockSecurityPlayer player) {
        return player != null && getHandle().isOwner(((LockSecurityPlayerWrapper) player).getHandle());
    }

    @Override
    public ZonedDateTime getLockTime() {
        return getHandle().getLockTime();
    }

    @Override
    public void setNickname(String nickname) {
        this.getHandle().setNickname(nickname);
    }

    @Override
    public String getNickname() {
        return getHandle().getNickname();
    }

    @Override
    public boolean hasNickname() {
        return getHandle().hasNickname();
    }

    @Override
    public boolean isValidKey(ItemStack key) {
        return getHandle().isValidKey(key);
    }

    @Override
    public int hashCode() {
        return getHandle().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof LockedBlockWrapper && Objects.equals(handle, ((LockedBlockWrapper) obj).handle));
    }

    public LockedBlock getHandle() {
        return handle;
    }

}
