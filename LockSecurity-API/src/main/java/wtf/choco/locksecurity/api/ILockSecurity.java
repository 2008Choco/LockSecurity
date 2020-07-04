package wtf.choco.locksecurity.api;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;

import wtf.choco.locksecurity.api.block.ILockedBlockManager;
import wtf.choco.locksecurity.api.key.IKeyBuilder;
import wtf.choco.locksecurity.api.key.IKeyFactory;
import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;

/**
 * Represents the LockSecurity plugin's core functionality
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
public interface ILockSecurity {

    /**
     * Get a key factory instance according to its type.
     *
     * @param type the type of key factory
     * @param <T> the type of key factory
     *
     * @return the key factory. Must not be null
     *
     * @throws UnsupportedOperationException if the provided {@link IKeyBuilder} is not
     * supported by LockSecurity
     */
    @NotNull
    public <T extends IKeyBuilder> IKeyFactory<T> getKeyFactory(@NotNull Class<T> type);

    /**
     * Get an {@link ILockSecurityPlayer} wrapper for the given {@link OfflinePlayer}.
     *
     * @param player the player whose wrapper to get. Must not be null
     *
     * @return the lock security player wrapper
     */
    @NotNull
    public ILockSecurityPlayer getLockSecurityPlayer(@NotNull OfflinePlayer player);

    /**
     * Get the {@link ILockedBlockManager} instance.
     *
     * @return the locked block manager
     */
    @NotNull
    public ILockedBlockManager getLockedBlockManager();

    /**
     * Get whether the supplied {@link Material} is lockable according to the LockSecurity
     * configuration file.
     *
     * @param material the material to check. Must not be null
     *
     * @return true if lockable, false otherwise
     */
    public boolean isLockable(@NotNull Material material);

}
