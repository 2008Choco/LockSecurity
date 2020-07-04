package wtf.choco.locksecurity.api.impl.block;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public boolean isOwner(OfflinePlayer player) {
        return getHandle().isOwner(player);
    }

    @Override
    public boolean isOwner(ILockSecurityPlayer player) {
        return getHandle().isOwner(((LockSecurityPlayerWrapper) player).getHandle());
    }

    @Override
    public @NotNull ZonedDateTime getLockTime() {
        return getHandle().getLockTime();
    }

    @Override
    public void setNickname(String nickname) {
        this.getHandle().setNickname(nickname);
    }

    @Override
    public @Nullable String getNickname() {
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
