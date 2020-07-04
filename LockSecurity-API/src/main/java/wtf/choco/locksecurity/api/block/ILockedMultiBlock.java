package wtf.choco.locksecurity.api.block;

import org.bukkit.block.Block;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a more specific type of {@link ILockedBlock} that holds more than one
 * position in the world (such as doors or double chests).
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
public interface ILockedMultiBlock extends ILockedBlock {

    /**
     * Get the second Bukkit {@link Block} represented by this locked block.
     *
     * @return the second block
     */
    @NotNull
    public Block getSecondaryBlock();

    /**
     * Get the second x coordinate at which this locked block resides.
     *
     * @return the second x coordinate
     */
    public int getXSecondary();

    /**
     * Get the second y coordinate at which this locked block resides.
     *
     * @return the second y coordinate
     */
    public int getYSecondary();

    /**
     * Get the second z coordinate at which this locked block resides.
     *
     * @return the second z coordinate
     */
    public int getZSecondary();

}
