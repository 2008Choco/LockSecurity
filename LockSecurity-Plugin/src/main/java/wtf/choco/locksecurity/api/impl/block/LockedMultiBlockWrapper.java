package wtf.choco.locksecurity.api.impl.block;

import org.bukkit.block.Block;

import wtf.choco.locksecurity.api.block.ILockedMultiBlock;
import wtf.choco.locksecurity.block.LockedMultiBlock;

public final class LockedMultiBlockWrapper extends LockedBlockWrapper implements ILockedMultiBlock {

    public LockedMultiBlockWrapper(LockedMultiBlock handle) {
        super(handle);
    }

    @Override
    public Block getSecondaryBlock() {
        return getHandle().getSecondaryBlock();
    }

    @Override
    public int getXSecondary() {
        return getHandle().getXSecondary();
    }

    @Override
    public int getYSecondary() {
        return getHandle().getYSecondary();
    }

    @Override
    public int getZSecondary() {
        return getHandle().getZSecondary();
    }

    @Override
    public LockedMultiBlock getHandle() {
        return (LockedMultiBlock) handle;
    }

}
