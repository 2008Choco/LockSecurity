package wtf.choco.locksecurity.api;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;

import wtf.choco.locksecurity.api.block.ILockedBlockManager;
import wtf.choco.locksecurity.api.key.IKeyBuilder;
import wtf.choco.locksecurity.api.key.IKeyFactory;
import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;

/**
 * The LockSecurity API utility class. All methods in this class mirror that of
 * {@link ILockSecurity} for the sake of utility.
 * <p>
 * This API is provided free of charge to developers
 * as a means of integrating with the LockSecurity plugin without having to purchase and
 * install the plugin from SpigotMC. The API may be distributed independently.
 * <p>
 * This API (including any interfaces found in the wtf.choco.locksecurity.api package)
 * are not meant to be implemented.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
public final class LockSecurityAPI {

    private static ILockSecurity plugin;

    private LockSecurityAPI() { }

    /**
     * Set the implementation for LockSecurity's API.
     * <p>
     * <strong>WARNING:</strong> This is for implementation use <strong>ONLY</strong>
     *
     * @param plugin the API implementation
     */
    public static void setPlugin(ILockSecurity plugin) {
        LockSecurityAPI.plugin = plugin;
    }

    /**
     * Get an instance of the LockSecurity plugin API.
     * <p>
     * Note that this call is redundant in most cases. Where possible, you should prefer
     * making calls to the static methods mirrored in {@link LockSecurityAPI}.
     *
     * @return the plugin API
     */
    @NotNull
    public static ILockSecurity getPlugin() {
        return plugin;
    }

    /**
     * Get LockSecurity's plugin version.
     *
     * @return the plugin version
     *
     * @since 3.0.1
     */
    @NotNull
    public static String getVersion() {
        return plugin.getVersion();
    }

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
    public static <T extends IKeyBuilder> IKeyFactory<T> getKeyFactory(@NotNull Class<T> type) {
        return plugin.getKeyFactory(type);
    }

    /**
     * Get an {@link ILockSecurityPlayer} wrapper for the given {@link OfflinePlayer}.
     *
     * @param player the player whose wrapper to get. Must not be null
     *
     * @return the lock security player wrapper
     */
    @NotNull
    public static ILockSecurityPlayer getLockSecurityPlayer(@NotNull OfflinePlayer player) {
        return plugin.getLockSecurityPlayer(player);
    }

    /**
     * Get the {@link ILockedBlockManager} instance.
     *
     * @return the locked block manager
     */
    @NotNull
    public static ILockedBlockManager getLockedBlockManager() {
        return plugin.getLockedBlockManager();
    }

    /**
     * Get whether the supplied {@link Material} is lockable according to the LockSecurity
     * configuration file.
     *
     * @param material the material to check. Must not be null
     *
     * @return true if lockable, false otherwise
     */
    public static boolean isLockable(@NotNull Material material) {
        return plugin.isLockable(material);
    }

}
