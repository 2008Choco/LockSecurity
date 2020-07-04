package wtf.choco.locksecurity.api.key;

import java.util.Set;

import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import wtf.choco.locksecurity.api.LockSecurityAPI;
import wtf.choco.locksecurity.api.block.ILockedBlock;
import wtf.choco.locksecurity.api.block.ILockedBlockManager;

/**
 * A factory class used to create {@link ItemStack ItemStacks} used to lock or open blocks.
 *
 * @param <T> the {@link IKeyBuilder} type supplied by this factory
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
public interface IKeyFactory<T extends IKeyBuilder> {

    /**
     * The unsmithed key factory.
     */
    public static final IKeyFactory<@NotNull IKeyBuilderUnsmithed> UNSMITHED = LockSecurityAPI.getKeyFactory(IKeyBuilderUnsmithed.class);

    /**
     * The smithed key factory.
     */
    public static final IKeyFactory<@NotNull IKeyBuilderSmithed> SMITHED = LockSecurityAPI.getKeyFactory(IKeyBuilderSmithed.class);

    /**
     * Check whether or not the supplied {@link ItemStack} is a valid key for this factory.
     *
     * @param item the item to check
     *
     * @return true if the key is producable using this factory, false otherwise or if null
     */
    public boolean isKey(@Nullable ItemStack item);

    /**
     * Get an {@link IKeyBuilder} instance to build a new key {@link ItemStack}.
     *
     * @return a key builder
     */
    @NotNull
    public T builder();

    /**
     * Get an {@link IKeyBuilder} instance to modify an existing key {@link ItemStack}.
     *
     * @param item the key to modify. Must not be null
     *
     * @return a key builder with pre-defined values
     */
    @NotNull
    public T modify(@NotNull ItemStack item);

    /**
     * Merge two smithed keys into one. The resultant {@link ItemStack} will:
     * <ul>
     *   <li>Unlock the blocks from either key
     *   <li>Have the merged flags from each key
     * </ul>
     *
     * @param firstKey the first smithed key. Must not be null
     * @param secondKey the second smithed key. Must not be null
     * @param amount the amount of keys to create
     *
     * @return the merged key
     */
    @NotNull
    public ItemStack merge(@NotNull ItemStack firstKey, @NotNull ItemStack secondKey, int amount);

    /**
     * Merge two smithed keys into one. The resultant {@link ItemStack} will:
     * <ul>
     *   <li>Unlock the blocks from either key
     *   <li>Have the merged flags from each key
     * </ul>
     *
     * @param firstKey the first smithed key. Must not be null
     * @param secondKey the second smithed key. Must not be null
     *
     * @return the merged key
     *
     * @see #merge(ItemStack, ItemStack, int)
     */
    @NotNull
    public ItemStack merge(@NotNull ItemStack firstKey, @NotNull ItemStack secondKey);

    /**
     * Get an array of {@link ILockedBlock ILockedBlocks} that the provided key unlocks.
     * Changes made to the returned array will not be reflected on the key. In order to change
     * the blocks a key may lock, see {@link #modify(ItemStack)}.
     *
     * @param item the item whose blocks to get. Must not be null
     *
     * @return the locked blocks
     */
    @NotNull
    public ILockedBlock[] getUnlocks(@NotNull ItemStack item);

    /**
     * Check whether or not the supplied key has the specified flag.
     * <p>
     * If more than one call to this method is being made, it is recommended instead to use
     * {@link #getFlags(ItemStack)} and {@link Set#contains(Object)} on the result as this method
     * will fetch flags for each call.
     *
     * @param item the item to check. Must not be null
     * @param flag the flag for which to check.
     *
     * @return true if the item contains the flag, false otherwise or if is not a key
     */
    public boolean hasFlag(@NotNull ItemStack item, @NotNull KeyFlag flag);

    /**
     * Get an immutable Set of all flags present on the supplied key.
     *
     * @param item the item whose flags to get. Must not be null
     *
     * @return the key flags
     */
    @NotNull
    public Set<KeyFlag> getFlags(@NotNull ItemStack item);

    /**
     * Refresh the supplied key. A refreshed key will have its lore and name updated according
     * to data fetched from other sources (i.e. the {@link ILockedBlockManager} for locked blocks).
     *
     * @param key the key to refresh. Must not be null
     *
     * @return the refreshed key
     */
    @NotNull
    public ItemStack refresh(@NotNull ItemStack key);

}
