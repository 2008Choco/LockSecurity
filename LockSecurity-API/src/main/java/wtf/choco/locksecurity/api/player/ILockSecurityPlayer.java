package wtf.choco.locksecurity.api.player;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a wrapped Bukkit {@link OfflinePlayer} for LockSecurity's player state.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
public interface ILockSecurityPlayer {

    /**
     * Get the {@link Player} instance wrapped by this player. If the player is not
     * online, the returned optional will be empty.
     *
     * @return the wrapped {@link Player}
     */
    @NotNull
    public Optional<Player> getBukkitPlayer();

    /**
     * Get the {@link OfflinePlayer} instance wrapped by this player.
     *
     * @return the wrapped {@link OfflinePlayer}
     */
    @NotNull
    public OfflinePlayer getBukkitPlayerOffline();

    /**
     * Get the UUID of this wrapped player.
     *
     * @return the wrapped player UUID
     */
    @NotNull
    public UUID getUniqueId();

    /**
     * Check if this {@link ILockSecurityPlayer} represents the supplied player
     *
     * @param player the player to check
     *
     * @return true if the players are the same
     */
    public boolean is(@Nullable OfflinePlayer player);

    /**
     * Set whether or not this player is ignoring locks.
     *
     * @param ignoringLocks whether or not to ignore locks
     */
    public void setIgnoringLocks(boolean ignoringLocks);

    /**
     * Get whether or not this player is ignoring locks.
     *
     * @return true if ignoring locks, false otherwise
     */
    public boolean isIgnoringLocks();

    /**
     * Set whether or not this player should receive lock notifications.
     *
     * @param notifications whether or not to send lock notifications
     */
    public void setLockNotifications(boolean notifications);

    /**
     * Get whether or not this player should receive lock notifications.
     *
     * @return true if receiving lock notifications, false otherwise
     */
    public boolean hasLockNotifications();

}
