package wtf.choco.locksecurity.api.impl.block;

import java.time.ZonedDateTime;
import java.util.Collection;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;

import wtf.choco.locksecurity.api.block.ILockedBlock;
import wtf.choco.locksecurity.api.block.ILockedBlockManager;
import wtf.choco.locksecurity.api.block.ILockedMultiBlock;
import wtf.choco.locksecurity.api.impl.player.LockSecurityPlayerWrapper;
import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;
import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.block.LockedBlockManager;
import wtf.choco.locksecurity.block.LockedMultiBlock;

public final class LockedBlockManagerWrapper implements ILockedBlockManager {

    private final LockedBlockManager handle;

    public LockedBlockManagerWrapper(LockedBlockManager handle) {
        this.handle = handle;
    }

    @Override
    public ILockedMultiBlock lock(Block block, Block secondaryBlock, ILockSecurityPlayer owner, ZonedDateTime lockTime, String nickname) {
        Preconditions.checkArgument(block != null, "block must not be null");
        Preconditions.checkArgument(secondaryBlock != null, "secondaryBlock must not be null");
        Preconditions.checkArgument(owner != null, "owner must not be null");
        Preconditions.checkArgument(!getHandle().isLocked(block), "block is already locked");
        Preconditions.checkArgument(!getHandle().isLocked(secondaryBlock), "secondaryBlock is already locked");

        if (lockTime == null) {
            lockTime = ZonedDateTime.now();
        }

        LockedMultiBlock lockedBlock = new LockedMultiBlock(block, secondaryBlock, ((LockSecurityPlayerWrapper) owner).getHandle(), lockTime, nickname);
        this.getHandle().registerLockedBlock(lockedBlock);
        return lockedBlock.getAPIWrapper();
    }

    @Override
    public ILockedMultiBlock lock(Block block, Block secondaryBlock, ILockSecurityPlayer owner, ZonedDateTime lockTime) {
        return lock(block, secondaryBlock, owner, lockTime, null);
    }

    @Override
    public ILockedMultiBlock lock(Block block, Block secondaryBlock, ILockSecurityPlayer owner, String nickname) {
        return lock(block, secondaryBlock, owner, ZonedDateTime.now(), nickname);
    }

    @Override
    public ILockedMultiBlock lock(Block block, Block secondaryBlock, ILockSecurityPlayer owner) {
        return lock(block, secondaryBlock, owner, ZonedDateTime.now(), null);
    }

    @Override
    public ILockedBlock lock(Block block, ILockSecurityPlayer owner, ZonedDateTime lockTime, String nickname) {
        Preconditions.checkArgument(block != null, "block must not be null");
        Preconditions.checkArgument(owner != null, "owner must not be null");
        Preconditions.checkArgument(!getHandle().isLocked(block), "block is already locked");

        if (lockTime == null) {
            lockTime = ZonedDateTime.now();
        }

        LockedBlock lockedBlock = new LockedBlock(block, ((LockSecurityPlayerWrapper) owner).getHandle(), lockTime, nickname);
        this.getHandle().registerLockedBlock(lockedBlock);
        return lockedBlock.getAPIWrapper();
    }

    @Override
    public ILockedBlock lock(Block block, ILockSecurityPlayer owner, ZonedDateTime lockTime) {
        return lock(block, owner, lockTime, null);
    }

    @Override
    public ILockedBlock lock(Block block, ILockSecurityPlayer owner, String nickname) {
        return lock(block, owner, ZonedDateTime.now(), nickname);
    }

    @Override
    public ILockedBlock lock(Block block, ILockSecurityPlayer owner) {
        return lock(block, owner, ZonedDateTime.now(), null);
    }

    @Override
    public boolean unlock(Block block) {
        Preconditions.checkArgument(block != null, "block must not be null");

        LockedBlock lockedBlock = getHandle().getLockedBlock(block);
        if (lockedBlock == null) {
            return false;
        }

        this.getHandle().unregisterLockedBlock(lockedBlock);
        return true;
    }

    @Override
    public ILockedBlock getLockedBlock(Block block) {
        Preconditions.checkArgument(block != null, "block must not be null");

        LockedBlock lockedBlock = getHandle().getLockedBlock(block);
        return (lockedBlock != null) ? lockedBlock.getAPIWrapper() : null;
    }

    @Override
    public ILockedBlock getLockedBlock(Location location) {
        Preconditions.checkArgument(location != null, "location must not be null");
        Preconditions.checkArgument(location.getWorld() != null, "location's world must not be null");

        return getLockedBlock(location.getBlock());
    }

    @Override
    public ILockedBlock getLockedBlock(World world, int x, int y, int z) {
        Preconditions.checkArgument(world != null, "world must not be null");
        return getLockedBlock(world.getBlockAt(x, y, z));
    }

    @Override
    public boolean isLocked(Block block) {
        Preconditions.checkArgument(block != null, "block must not be null");
        return getHandle().isLocked(block);
    }

    @Override
    public boolean isLocked(Location location) {
        Preconditions.checkArgument(location != null, "location must not be null");
        Preconditions.checkArgument(location.getWorld() != null, "location's world must not be null");

        return isLocked(location.getBlock());
    }

    @Override
    public boolean isLocked(World world, int x, int y, int z) {
        Preconditions.checkArgument(world != null, "world must not be null");
        return isLocked(world.getBlockAt(x, y, z));
    }

    @Override
    public Collection<ILockedBlock> getLockedBlocks(ILockSecurityPlayer owner) {
        Preconditions.checkArgument(owner != null, "owner must not be null");
        return getLockedBlocks(owner.getBukkitPlayerOffline());
    }

    @Override
    public Collection<ILockedBlock> getLockedBlocks(OfflinePlayer owner) {
        Preconditions.checkArgument(owner != null, "owner must not be null");

        ImmutableSet.Builder<ILockedBlock> lockedBlocks = ImmutableSet.builder();
        getHandle().getLockedBlocks(owner).forEach(l -> lockedBlocks.add(l.getAPIWrapper()));
        return lockedBlocks.build();
    }

    @Override
    public Collection<ILockedBlock> getLockedBlocks() {
        ImmutableSet.Builder<ILockedBlock> lockedBlocks = ImmutableSet.builder();
        getHandle().getLockedBlocks().forEach(l -> lockedBlocks.add(l.getAPIWrapper()));
        return lockedBlocks.build();
    }

    public LockedBlockManager getHandle() {
        return handle;
    }

}
