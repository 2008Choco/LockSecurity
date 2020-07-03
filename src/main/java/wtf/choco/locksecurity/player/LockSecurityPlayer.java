package wtf.choco.locksecurity.player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class LockSecurityPlayer {

    private boolean ignoringLocks = false;
    private boolean lockNotifications = false;

    private final UUID playerUUID;

    public LockSecurityPlayer(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public Optional<Player> getBukkitPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(playerUUID));
    }

    public OfflinePlayer getOfflineBukkitPlayer() {
        return Bukkit.getOfflinePlayer(playerUUID);
    }

    public UUID getUniqueId() {
        return playerUUID;
    }

    public boolean is(OfflinePlayer player) {
        return player != null && player.getUniqueId().equals(playerUUID);
    }

    public void setIgnoringLocks(boolean ignoringLocks) {
        this.ignoringLocks = ignoringLocks;
    }

    public boolean isIgnoringLocks() {
        return ignoringLocks;
    }

    public void setLockNotifications(boolean lockNotifications) {
        this.lockNotifications = lockNotifications;
    }

    public boolean hasLockNotifications() {
        return lockNotifications;
    }

    @Override
    public int hashCode() {
        return playerUUID.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof LockSecurityPlayer && Objects.equals(playerUUID, ((LockSecurityPlayer) other).playerUUID));
    }

}
