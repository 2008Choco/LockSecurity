package wtf.choco.locksecurity.api.impl.player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;
import wtf.choco.locksecurity.player.LockSecurityPlayer;

public final class LockSecurityPlayerWrapper implements ILockSecurityPlayer {

    private final LockSecurityPlayer handle;

    public LockSecurityPlayerWrapper(LockSecurityPlayer handle) {
        this.handle = handle;
    }

    @Override
    public Optional<Player> getBukkitPlayer() {
        return getHandle().getBukkitPlayer();
    }

    @Override
    public OfflinePlayer getBukkitPlayerOffline() {
        return getHandle().getBukkitPlayerOffline();
    }

    @Override
    public UUID getUniqueId() {
        return getHandle().getUniqueId();
    }

    @Override
    public boolean is(OfflinePlayer player) {
        return getHandle().is(player);
    }

    @Override
    public void setIgnoringLocks(boolean ignoringLocks) {
        this.getHandle().setIgnoringLocks(ignoringLocks);
    }

    @Override
    public boolean isIgnoringLocks() {
        return getHandle().isIgnoringLocks();
    }

    @Override
    public void setLockNotifications(boolean notifications) {
        this.getHandle().setLockNotifications(notifications);
    }

    @Override
    public boolean hasLockNotifications() {
        return getHandle().hasLockNotifications();
    }

    @Override
    public int hashCode() {
        return getHandle().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof LockSecurityPlayerWrapper && Objects.equals(handle, ((LockSecurityPlayerWrapper) obj).handle));
    }

    public LockSecurityPlayer getHandle() {
        return handle;
    }

}
