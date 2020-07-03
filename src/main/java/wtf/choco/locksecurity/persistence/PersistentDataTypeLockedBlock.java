package wtf.choco.locksecurity.persistence;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.block.LockedBlock;

public final class PersistentDataTypeLockedBlock implements PersistentDataType<PersistentDataContainer, LockedBlock> {

    private static final NamespacedKey KEY_WORLD = LockSecurity.key("world");
    private static final NamespacedKey KEY_X = LockSecurity.key("x");
    private static final NamespacedKey KEY_Y = LockSecurity.key("y");
    private static final NamespacedKey KEY_Z = LockSecurity.key("z");

    PersistentDataTypeLockedBlock() { }

    @Override
    public LockedBlock fromPrimitive(PersistentDataContainer container, PersistentDataAdapterContext context) {
        // If any of these components are missing, it should be considered invalid (and hopefully ignored by the caller
        if (!container.has(KEY_WORLD, LSPersistentDataTypes.UUID) || !container.has(KEY_X, PersistentDataType.INTEGER)
                || !container.has(KEY_Y, PersistentDataType.INTEGER) || !container.has(KEY_Z, PersistentDataType.INTEGER)) {
            return null;
        }

        World world = Bukkit.getWorld(container.get(KEY_WORLD, LSPersistentDataTypes.UUID));
        int x = container.get(KEY_X, PersistentDataType.INTEGER);
        int y = container.get(KEY_Y, PersistentDataType.INTEGER);
        int z = container.get(KEY_Z, PersistentDataType.INTEGER);
        Block block = world.getBlockAt(x, y, z);

        return LockSecurity.getInstance().getLockedBlockManager().getLockedBlock(block);
    }

    @Override
    public PersistentDataContainer toPrimitive(LockedBlock block, PersistentDataAdapterContext context) {
        PersistentDataContainer container = context.newPersistentDataContainer();

        container.set(KEY_WORLD, LSPersistentDataTypes.UUID, block.getWorld().getUID());
        container.set(KEY_X, PersistentDataType.INTEGER, block.getX());
        container.set(KEY_Y, PersistentDataType.INTEGER, block.getY());
        container.set(KEY_Z, PersistentDataType.INTEGER, block.getZ());

        return container;
    }

    @Override
    public Class<LockedBlock> getComplexType() {
        return LockedBlock.class;
    }

    @Override
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

}
