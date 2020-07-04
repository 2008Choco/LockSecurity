package wtf.choco.locksecurity.api.block;

import java.time.ZonedDateTime;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;

/**
 * Represents a locked block in the world.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 *
 * @see ILockedMultiBlock
 */
public interface ILockedBlock {

    /**
     * Get the Bukkit {@link Block} represented by this locked block.
     *
     * @return the block
     */
    @NotNull
    public Block getBlock();

    /**
     * Get the Bukkit {@link Material} represented by this locked block.
     *
     * @return the material
     */
    @NotNull
    public Material getType();

    /**
     * Get the Bukkit {@link Location} at which this locked block is located.
     *
     * @return the location
     */
    @NotNull
    public Location getLocation();

    /**
     * Get the Bukkit {@link World} in which this locked block resides.
     *
     * @return the world
     */
    @NotNull
    public World getWorld();

    /**
     * Get the x coordinate at which this locked block resides.
     *
     * @return the x coordinate
     */
    public int getX();

    /**
     * Get the y coordinate at which this locked block resides.
     *
     * @return the y coordinate
     */
    public int getY();

    /**
     * Get the z coordinate at which this locked block resides.
     *
     * @return the z coordinate
     */
    public int getZ();

    /**
     * Check whether or not the specified player is the owner of this locked block.
     *
     * @param player the player to check
     *
     * @return true if the player owns this block, false otherwise or if null
     */
    public boolean isOwner(@Nullable OfflinePlayer player);

    /**
     * Check whether or not the specified player is the owner of this locked block.
     *
     * @param player the player to check
     *
     * @return true if the player owns this block, false otherwise or if null
     */
    public boolean isOwner(@Nullable ILockSecurityPlayer player);

    /**
     * Get the time at which this block was locked.
     *
     * @return the lock time
     */
    @NotNull
    public ZonedDateTime getLockTime();

    /**
     * Set this block's nickname.
     *
     * @param nickname the nickname to set or null if none
     */
    public void setNickname(@Nullable String nickname);

    /**
     * Get this block's nickname.
     *
     * @return the nickname. null if none
     */
    @Nullable
    public String getNickname();

    /**
     * Check whether or not this locked block has a nickname.
     *
     * @return true if this block has a nickname, false otherwise
     */
    public boolean hasNickname();

    /**
     * Check whether or not the supplied ItemStack is a valid key that will grant access
     * to this locked block.
     *
     * @param key the key to check
     *
     * @return true if a valid key, false otherwise or if null
     */
    public boolean isValidKey(@Nullable ItemStack key);

}
