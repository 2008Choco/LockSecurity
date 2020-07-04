package wtf.choco.locksecurity.api.impl.key;

import java.util.Set;

import com.google.common.base.Preconditions;

import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;

import wtf.choco.locksecurity.api.block.ILockedBlock;
import wtf.choco.locksecurity.api.key.IKeyBuilder;
import wtf.choco.locksecurity.api.key.IKeyFactory;
import wtf.choco.locksecurity.api.key.KeyFlag;
import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.key.KeyBuilder;
import wtf.choco.locksecurity.key.KeyFactoryType;

public final class KeyFactoryWrapper<T extends IKeyBuilder, I extends KeyBuilder<T>> implements IKeyFactory<T> {

    private final KeyFactoryType<I> handle;

    public KeyFactoryWrapper(KeyFactoryType<I> handle) {
        this.handle = handle;
    }

    @Override
    public boolean isKey(@Nullable ItemStack item) {
        return getHandle().isKey(item);
    }

    @Override
    public T builder() {
        return getHandle().builder().getAPIWrapper();
    }

    @Override
    public T modify(ItemStack item) {
        return getHandle().modify(item).getAPIWrapper();
    }

    @Override
    public ItemStack merge(@Nullable ItemStack firstKey, @Nullable ItemStack secondKey, int amount) {
        return getHandle().merge(firstKey, secondKey, amount);
    }

    @Override
    public ItemStack merge(ItemStack firstKey, ItemStack secondKey) {
        return getHandle().merge(firstKey, secondKey);
    }

    @Override
    public ILockedBlock[] getUnlocks(ItemStack item) {
        Preconditions.checkArgument(item != null, "item cannot be null");

        LockedBlock[] unlocks = getHandle().getUnlocks(item);
        ILockedBlock[] unlocksWrapper = new ILockedBlock[unlocks.length];

        for (int i = 0; i < unlocks.length; i++) {
            unlocksWrapper[i] = unlocks[i].getAPIWrapper();
        }

        return unlocksWrapper;
    }

    @Override
    public boolean hasFlag(ItemStack item, KeyFlag flag) {
        Preconditions.checkArgument(item != null, "item cannot be null");
        Preconditions.checkArgument(flag != null, "flag cannot be null");

        return getHandle().hasFlag(item, flag);
    }

    @Override
    public Set<KeyFlag> getFlags(ItemStack item) {
        Preconditions.checkArgument(item != null, "item cannot be null");
        return getHandle().getFlags(item);
    }

    @Override
    public ItemStack refresh(ItemStack key) {
        Preconditions.checkArgument(key != null, "key cannot be null");
        return getHandle().refresh(key);
    }

    public KeyFactoryType<I> getHandle() {
        return handle;
    }

}
