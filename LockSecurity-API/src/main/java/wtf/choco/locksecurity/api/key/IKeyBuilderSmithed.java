package wtf.choco.locksecurity.api.key;

import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import wtf.choco.locksecurity.api.block.ILockedBlock;

/**
 * An {@link IKeyBuilder} implementation for smithed keys.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
public interface IKeyBuilderSmithed extends IKeyBuilder {

    /**
     * Copy the unlockable blocks from the supplied key.
     *
     * @param key the key whose unlockable blocks to copy. Must not be null
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed unlocks(@NotNull ItemStack key);


    /**
     * Add the specified blocks to the built key's unlockable blocks.
     *
     * @param blocks the blocks to unlock. Must not be null
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed unlocks(@NotNull Iterable<? extends ILockedBlock> blocks);

    /**
     * Add the specified blocks to the built key's unlockable blocks.
     *
     * @param blocks the blocks to unlock. Must not be null
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed unlocks(@NotNull ILockedBlock... blocks);

    /**
     * Add the specified block to the built key's unlockable blocks.
     *
     * @param block the block to unlock. Must not be null
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed unlocks(@NotNull ILockedBlock block);

    /**
     * Assign the specified flags to the built key.
     *
     * @param flags the flags to assign. Must not be null
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed withFlags(@NotNull KeyFlag... flags);

    /**
     * Assign the specified flag to the built key.
     *
     * @param flag the flags to assign
     * @param value the value to set for this flag
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed withFlag(@NotNull KeyFlag flag, boolean value);

    /**
     * Assign the specified flag to the built key.
     *
     * @param flag the flag to assign. Must not be null
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed withFlag(@NotNull KeyFlag flag);

    /**
     * Apply the {@link KeyFlag#PREVENT_DUPLICATION} flag to the built key.
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed preventDuplication();

    /**
     * Apply the {@link KeyFlag#PREVENT_MERGING} flag to the built key.
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed preventMerging();

    /**
     * Apply the {@link KeyFlag#PREVENT_RESETTING} flag to the built key.
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed preventResetting();

    /**
     * Apply the {@link KeyFlag#BREAK_ON_USE} flag to the built key.
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed breakOnUse();

    /**
     * Apply the {@link KeyFlag#HIDE_BLOCK_COORDINATES} flag to the built key.
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed hideBlockCoordinates();

    /**
     * Hide the lore from flags that would otherwise be applied on the built key.
     *
     * @param hide whether or not to hide the flag lore
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed hideFlagLore(boolean hide);

    /**
     * Hide the lore from flags that would otherwise be applied on the built key.
     *
     * @return this instance. Allows for chained method calls
     */
    @NotNull
    public IKeyBuilderSmithed hideFlagLore();

}
