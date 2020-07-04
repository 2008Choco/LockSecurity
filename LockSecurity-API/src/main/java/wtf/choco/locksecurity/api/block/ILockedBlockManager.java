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

/**
 * Represents a manager to handle all {@link ILockedBlock ILockedBlocks} on the server.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
public interface ILockedBlockManager {

    /**
     * Lock the blocks at the given locations with the supplied data.
     *
     * @param block the primary block to lock
     * @param secondaryBlock the secondary block to lock
     * @param owner the owner of the locked block
     * @param lockTime the time at which the block was locked. If null, now will be used
     * @param nickname the nickname for this block. Can be null
     *
     * @return the {@link ILockedMultiBlock} instance
     *
     * @throws IllegalArgumentException if either block or secondaryBlock is already locked
     */
    @NotNull
    public ILockedMultiBlock lock(@NotNull Block block, @NotNull Block secondaryBlock, @NotNull ILockSecurityPlayer owner, @Nullable ZonedDateTime lockTime, @Nullable String nickname);

    /**
     * Lock the blocks at the given locations with the supplied data.
     *
     * @param block the primary block to lock
     * @param secondaryBlock the secondary block to lock
     * @param owner the owner of the locked block
     * @param lockTime the time at which the block was locked. If null, now will be used
     *
     * @return the {@link ILockedMultiBlock} instance
     *
     * @throws IllegalArgumentException if either block or secondaryBlock is already locked
     */
    @NotNull
    public ILockedMultiBlock lock(@NotNull Block block, @NotNull Block secondaryBlock, @NotNull ILockSecurityPlayer owner, @Nullable ZonedDateTime lockTime);

    /**
     * Lock the blocks at the given locations with the supplied data.
     *
     * @param block the primary block to lock
     * @param secondaryBlock the secondary block to lock
     * @param owner the owner of the locked block
     * @param nickname the nickname for this block. Can be null
     *
     * @return the {@link ILockedMultiBlock} instance
     *
     * @throws IllegalArgumentException if either block or secondaryBlock is already locked
     */
    @NotNull
    public ILockedMultiBlock lock(@NotNull Block block, @NotNull Block secondaryBlock, @NotNull ILockSecurityPlayer owner, @Nullable String nickname);

    /**
     * Lock the blocks at the given locations with the supplied data.
     *
     * @param block the primary block to lock
     * @param secondaryBlock the secondary block to lock
     * @param owner the owner of the locked block
     *
     * @return the {@link ILockedMultiBlock} instance
     *
     * @throws IllegalArgumentException if either block or secondaryBlock is already locked
     */
    @NotNull
    public ILockedMultiBlock lock(@NotNull Block block, @NotNull Block secondaryBlock, @NotNull ILockSecurityPlayer owner);

    /**
     * Lock the block at the given location with the supplied data.
     *
     * @param block the primary block to lock
     * @param owner the owner of the locked block
     * @param lockTime the time at which the block was locked. If null, now will be used
     * @param nickname the nickname for this block. Can be null
     *
     * @return the {@link ILockedMultiBlock} instance
     *
     * @throws IllegalArgumentException if either block or secondaryBlock is already locked
     */
    @NotNull
    public ILockedBlock lock(@NotNull Block block, @NotNull ILockSecurityPlayer owner, @Nullable ZonedDateTime lockTime, @Nullable String nickname);

    /**
     * Lock the block at the given location with the supplied data.
     *
     * @param block the primary block to lock
     * @param owner the owner of the locked block
     * @param lockTime the time at which the block was locked. If null, now will be used
     *
     * @return the {@link ILockedMultiBlock} instance
     *
     * @throws IllegalArgumentException if either block or secondaryBlock is already locked
     */
    @NotNull
    public ILockedBlock lock(@NotNull Block block, @NotNull ILockSecurityPlayer owner, @Nullable ZonedDateTime lockTime);

    /**
     * Lock the block at the given location with the supplied data.
     *
     * @param block the primary block to lock
     * @param owner the owner of the locked block
     * @param nickname the nickname for this block. Can be null
     *
     * @return the {@link ILockedMultiBlock} instance
     *
     * @throws IllegalArgumentException if either block or secondaryBlock is already locked
     */
    @NotNull
    public ILockedBlock lock(@NotNull Block block, @NotNull ILockSecurityPlayer owner, @Nullable String nickname);

    /**
     * Lock the block at the given location with the supplied data.
     *
     * @param block the primary block to lock
     * @param owner the owner of the locked block
     *
     * @return the {@link ILockedMultiBlock} instance
     *
     * @throws IllegalArgumentException if either block or secondaryBlock is already locked
     */
    @NotNull
    public ILockedBlock lock(@NotNull Block block, @NotNull ILockSecurityPlayer owner);

    /**
     * Unlock the block at the given location.
     *
     * @param block the block to unlock
     *
     * @return true if successfully unlocked, false if the block was not locked
     */
    public boolean unlock(@NotNull Block block);

    /**
     * Get the {@link ILockedBlock} at the given {@link Block} if one is present.
     *
     * @param block the block to get
     *
     * @return the locked block. null if not locked
     */
    @Nullable
    public ILockedBlock getLockedBlock(@NotNull Block block);

    /**
     * Get the {@link ILockedBlock} at the given {@link Location} if one is present.
     *
     * @param location the location whose block to get
     *
     * @return the locked block. null if not locked
     */
    @Nullable
    public ILockedBlock getLockedBlock(@NotNull Location location);

    /**
     * Get the {@link ILockedBlock} at the given position if one is present.
     *
     * @param world the world in which the block should be fetched. Must not be null
     * @param x the x coordinate of the block
     * @param y the y coordinate of the block
     * @param z the z coordinate of the block
     *
     * @return the locked block. null if not locked
     */
    @Nullable
    public ILockedBlock getLockedBlock(@NotNull World world, int x, int y, int z);

    /**
     * Check whether or not the provided block has been locked.
     *
     * @param block the block to check
     *
     * @return true if locked, false otherwise
     */
    public boolean isLocked(@NotNull Block block);

    /**
     * Check whether or not the block at the provided location has been locked.
     *
     * @param location the location to check
     *
     * @return true if locked, false otherwise
     */
    public boolean isLocked(@NotNull Location location);

    /**
     * Check whether or not the block at the provided position has been locked.
     *
     * @param world the world to check
     * @param x the x coordinate to check
     * @param y the y coordinate to check
     * @param z the z coordinate to check
     *
     * @return true if locked, false otherwise
     */
    public boolean isLocked(@NotNull World world, int x, int y, int z);

    /**
     * Get an immutable collection of all {@link ILockedBlock ILockedBlocks} owned by the specified
     * player.
     *
     * @param owner the player whose blocks to get
     *
     * @return all locked blocks owned by the player
     */
    @NotNull
    public Collection<ILockedBlock> getLockedBlocks(@NotNull ILockSecurityPlayer owner);

    /**
     * Get an immutable collection of all {@link ILockedBlock ILockedBlocks} owned by the specified
     * player.
     *
     * @param owner the player whose blocks to get
     *
     * @return all locked blocks owned by the player
     */
    @NotNull
    public Collection<ILockedBlock> getLockedBlocks(@NotNull OfflinePlayer owner);

    /**
     * Get an immutable collection of all {@link ILockedBlock ILockedBlocks} on the server.
     *
     * @return all locked blocks.
     */
    @NotNull
    public Collection<ILockedBlock> getLockedBlocks();

}
