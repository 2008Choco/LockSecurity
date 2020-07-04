package wtf.choco.locksecurity.key;

import java.util.Set;

import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.api.key.KeyFlag;
import wtf.choco.locksecurity.block.LockedBlock;

public interface KeyFactoryType<T extends KeyBuilder<?>> {

    public boolean isKey(ItemStack item);

    public T builder();

    public T modify(ItemStack item);

    public ItemStack merge(ItemStack firstKey, ItemStack secondKey, int amount);

    public default ItemStack merge(ItemStack firstKey, ItemStack secondKey) {
        return merge(firstKey, secondKey, 1);
    }

    public LockedBlock[] getUnlocks(ItemStack item);

    public boolean hasFlag(ItemStack item, KeyFlag flag);

    public Set<KeyFlag> getFlags(ItemStack item);

    public ItemStack refresh(ItemStack key);

}
