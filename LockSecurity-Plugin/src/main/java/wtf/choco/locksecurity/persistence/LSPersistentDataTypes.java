package wtf.choco.locksecurity.persistence;

import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.block.LockedBlock;

public final class LSPersistentDataTypes {

    public static final PersistentDataType<byte[], UUID> UUID = new PersistentDataTypeUUID();
    public static final PersistentDataType<PersistentDataContainer, LockedBlock> LOCKED_BLOCK = new PersistentDataTypeLockedBlock();
    public static final PersistentDataType<PersistentDataContainer[], LockedBlock[]> LOCKED_BLOCK_ARRAY = new PersistentDataTypeLockedBlockArray();

    public static final NamespacedKey KEY_UNLOCKS = LockSecurity.key("unlocks");
    public static final NamespacedKey KEY_KEY_FLAGS = LockSecurity.key("key_flags");

    private LSPersistentDataTypes() { }

}
