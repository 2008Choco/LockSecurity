package wtf.choco.locksecurity.key;

import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.api.key.IKeyBuilder;

public interface KeyBuilder<T extends IKeyBuilder> {

    public ItemStack build(int amount);

    public default ItemStack build() {
        return build(1);
    }

    public T getAPIWrapper();

}
