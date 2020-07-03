package wtf.choco.locksecurity.key;

import org.bukkit.inventory.ItemStack;

public interface KeyBuilder {

    public ItemStack build(int amount);

    public default ItemStack build() {
        return build(1);
    }

}
