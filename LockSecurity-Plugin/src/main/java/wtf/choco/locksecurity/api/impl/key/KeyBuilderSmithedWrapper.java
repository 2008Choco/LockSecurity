package wtf.choco.locksecurity.api.impl.key;

import java.util.Arrays;

import com.google.common.base.Preconditions;

import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.api.block.ILockedBlock;
import wtf.choco.locksecurity.api.impl.block.LockedBlockWrapper;
import wtf.choco.locksecurity.api.key.IKeyBuilderSmithed;
import wtf.choco.locksecurity.api.key.KeyFlag;
import wtf.choco.locksecurity.key.KeyFactorySmithed.KeyBuilderSmithed;

public final class KeyBuilderSmithedWrapper implements IKeyBuilderSmithed {

    private final KeyBuilderSmithed handle;

    public KeyBuilderSmithedWrapper(KeyBuilderSmithed handle) {
        this.handle = handle;
    }

    @Override
    public IKeyBuilderSmithed unlocks(ItemStack key) {
        Preconditions.checkArgument(key != null, "key cannot be null");

        this.getHandle().unlocks(key);
        return this;
    }

    @Override
    public IKeyBuilderSmithed unlocks(Iterable<? extends ILockedBlock> blocks) {
        Preconditions.checkArgument(blocks != null, "blocks cannot be null");

        blocks.forEach(this::unlocks);
        return this;
    }

    @Override
    public IKeyBuilderSmithed unlocks(ILockedBlock... blocks) {
        Preconditions.checkArgument(blocks != null, "blocks cannot be null");

        Arrays.stream(blocks).forEach(this::unlocks);
        return this;
    }

    @Override
    public IKeyBuilderSmithed unlocks(ILockedBlock block) {
        Preconditions.checkArgument(block != null, "block cannot be null");

        this.getHandle().unlocks(((LockedBlockWrapper) block).getHandle());
        return this;
    }

    @Override
    public IKeyBuilderSmithed withFlags(KeyFlag... flags) {
        Preconditions.checkArgument(flags != null, "flags cannot be null");

        this.getHandle().withFlags(flags);
        return this;
    }

    @Override
    public IKeyBuilderSmithed withFlag(KeyFlag flag, boolean value) {
        Preconditions.checkArgument(flag != null, "flag cannot be null");

        this.getHandle().withFlag(flag, value);
        return this;
    }

    @Override
    public IKeyBuilderSmithed withFlag(KeyFlag flag) {
        Preconditions.checkArgument(flag != null, "flag cannot be null");

        this.getHandle().withFlag(flag);
        return this;
    }

    @Override
    public IKeyBuilderSmithed preventDuplication() {
        this.getHandle().preventDuplication();
        return this;
    }

    @Override
    public IKeyBuilderSmithed preventMerging() {
        this.getHandle().preventMerging();
        return this;
    }

    @Override
    public IKeyBuilderSmithed preventResetting() {
        this.getHandle().preventResetting();
        return this;
    }

    @Override
    public IKeyBuilderSmithed breakOnUse() {
        this.getHandle().breakOnUse();
        return this;
    }

    @Override
    public IKeyBuilderSmithed hideBlockCoordinates() {
        this.getHandle().hideBlockCoordinates();
        return this;
    }

    @Override
    public IKeyBuilderSmithed hideFlagLore(boolean hide) {
        this.getHandle().hideFlagLore(hide);
        return this;
    }

    @Override
    public IKeyBuilderSmithed hideFlagLore() {
        this.getHandle().hideFlagLore();
        return this;
    }

    @Override
    public ItemStack build(int amount) {
        return getHandle().build(amount);
    }

    public KeyBuilderSmithed getHandle() {
        return handle;
    }

}
