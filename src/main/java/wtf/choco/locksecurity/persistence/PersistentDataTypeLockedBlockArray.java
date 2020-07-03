package wtf.choco.locksecurity.persistence;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import wtf.choco.locksecurity.block.LockedBlock;

public final class PersistentDataTypeLockedBlockArray implements PersistentDataType<PersistentDataContainer[], LockedBlock[]> {

    PersistentDataTypeLockedBlockArray() { }

    @Override
    public LockedBlock[] fromPrimitive(PersistentDataContainer[] containers, PersistentDataAdapterContext context) {
        return Arrays.stream(containers).map(b -> LSPersistentDataTypes.LOCKED_BLOCK.fromPrimitive(b, context)).filter(Objects::nonNull).toArray(LockedBlock[]::new);
    }

    @Override
    public PersistentDataContainer[] toPrimitive(LockedBlock[] blocks, PersistentDataAdapterContext context) {
        return Arrays.stream(blocks).map(b -> LSPersistentDataTypes.LOCKED_BLOCK.toPrimitive(b, context)).toArray(PersistentDataContainer[]::new);
    }

    @Override
    public Class<LockedBlock[]> getComplexType() {
        return LockedBlock[].class;
    }

    @Override
    public Class<PersistentDataContainer[]> getPrimitiveType() {
        return PersistentDataContainer[].class;
    }

}
