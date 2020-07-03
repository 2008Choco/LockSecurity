package wtf.choco.locksecurity.player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import wtf.choco.locksecurity.block.LockedBlock;

public final class LockSecurityPlayer {

    private boolean ignoringLocks = false;
    private boolean lockNotifications = false;

    private final UUID playerUUID;
    private final Set<LockedBlock> ownedBlocks = new HashSet<>();

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

    public void trackOwned(LockedBlock block) {
        this.ownedBlocks.add(block);
    }

    public void untrackOwned(LockedBlock block) {
        this.ownedBlocks.remove(block);
    }

    public boolean owns(LockedBlock block) {
        return ownedBlocks.contains(block);
    }

    public Set<LockedBlock> getOwnedBlocks() {
        return Collections.unmodifiableSet(ownedBlocks);
    }

    public void untrackAllOwnedBlocks() {
        this.ownedBlocks.clear();
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
