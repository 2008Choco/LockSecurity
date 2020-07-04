package wtf.choco.locksecurity.api;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;

import wtf.choco.locksecurity.api.block.ILockedBlockManager;
import wtf.choco.locksecurity.api.key.IKeyBuilder;
import wtf.choco.locksecurity.api.key.IKeyFactory;
import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;

public interface ILockSecurity {

    @NotNull
    public <T extends IKeyBuilder> IKeyFactory<T> getKeyFactory(@NotNull Class<T> type);

    @NotNull
    public ILockSecurityPlayer getLockSecurityPlayer(@NotNull OfflinePlayer player);

    @NotNull
    public ILockedBlockManager getLockedBlockManager();

    public boolean isLockable(@NotNull Material material);

}
