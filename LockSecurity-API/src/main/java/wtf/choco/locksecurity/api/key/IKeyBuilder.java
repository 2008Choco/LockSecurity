package wtf.choco.locksecurity.api.key;

import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

public interface IKeyBuilder {

    @NotNull
    public ItemStack build(int amount);

    @NotNull
    public default ItemStack build() {
        return build(1);
    }

}
