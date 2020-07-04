package wtf.choco.locksecurity.api;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;

import wtf.choco.locksecurity.api.block.ILockedBlockManager;
import wtf.choco.locksecurity.api.key.IKeyBuilder;
import wtf.choco.locksecurity.api.key.IKeyFactory;
import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;

public final class LockSecurityAPI {

    private static ILockSecurity plugin;

    private LockSecurityAPI() { }

    public static void setPlugin(ILockSecurity plugin) {
        LockSecurityAPI.plugin = plugin;
    }

    @NotNull
    public static ILockSecurity getPlugin() {
        return plugin;
    }

    @NotNull
    public static <T extends IKeyBuilder> IKeyFactory<T> getKeyFactory(@NotNull Class<T> type) {
        return plugin.getKeyFactory(type);
    }

    @NotNull
    public static ILockSecurityPlayer getLockSecurityPlayer(@NotNull OfflinePlayer player) {
        return plugin.getLockSecurityPlayer(player);
    }

    @NotNull
    public static ILockedBlockManager getLockedBlockManager() {
        return plugin.getLockedBlockManager();
    }

    public static boolean isLockable(@NotNull Material material) {
        return plugin.isLockable(material);
    }

}
