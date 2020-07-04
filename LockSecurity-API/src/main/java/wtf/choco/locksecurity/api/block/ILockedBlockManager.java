package wtf.choco.locksecurity.api.block;

import java.time.ZonedDateTime;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;

public interface ILockedBlockManager {

    @NotNull
    public ILockedMultiBlock lock(@NotNull Block block, @NotNull Block secondaryBlock, @NotNull ILockSecurityPlayer owner, @Nullable ZonedDateTime lockTime, @Nullable String nickname);

    @NotNull
    public ILockedMultiBlock lock(@NotNull Block block, @NotNull Block secondaryBlock, @NotNull ILockSecurityPlayer owner, @Nullable ZonedDateTime lockTime);

    @NotNull
    public ILockedMultiBlock lock(@NotNull Block block, @NotNull Block secondaryBlock, @NotNull ILockSecurityPlayer owner, @Nullable String nickname);

    @NotNull
    public ILockedMultiBlock lock(@NotNull Block block, @NotNull Block secondaryBlock, @NotNull ILockSecurityPlayer owner);

    @NotNull
    public ILockedBlock lock(@NotNull Block block, @NotNull ILockSecurityPlayer owner, @Nullable ZonedDateTime lockTime, @Nullable String nickname);

    @NotNull
    public ILockedBlock lock(@NotNull Block block, @NotNull ILockSecurityPlayer owner, @Nullable ZonedDateTime lockTime);

    @NotNull
    public ILockedBlock lock(@NotNull Block block, @NotNull ILockSecurityPlayer owner, @Nullable String nickname);

    @NotNull
    public ILockedBlock lock(@NotNull Block block, @NotNull ILockSecurityPlayer owner);

    public boolean unlock(@NotNull Block block);

    @Nullable
    public ILockedBlock getLockedBlock(@NotNull Block block);

    @Nullable
    public ILockedBlock getLockedBlock(@NotNull Location location);

    @Nullable
    public ILockedBlock getLockedBlock(@NotNull World world, int x, int y, int z);

    public boolean isLocked(@NotNull Block block);

    public boolean isLocked(@NotNull Location location);

    public boolean isLocked(@NotNull World world, int x, int y, int z);

    @NotNull
    public Collection<ILockedBlock> getLockedBlocks(ILockSecurityPlayer owner);

    @NotNull
    public Collection<ILockedBlock> getLockedBlocks(OfflinePlayer owner);

    @NotNull
    public Collection<ILockedBlock> getLockedBlocks();

}
