package wtf.choco.locksecurity.api.key;

import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a builder for key {@link ItemStack ItemStacks}.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 *
 * @see IKeyFactory
 */
public interface IKeyBuilder {

    /**
     * Build the key.
     *
     * @param amount the amount of keys to create
     *
     * @return the built key(s)
     */
    @NotNull
    public ItemStack build(int amount);

    /**
     * Build the key.
     *
     * @return the built key
     */
    @NotNull
    public default ItemStack build() {
        return build(1);
    }

}
