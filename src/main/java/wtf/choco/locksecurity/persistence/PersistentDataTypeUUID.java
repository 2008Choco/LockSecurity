package wtf.choco.locksecurity.persistence;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public final class PersistentDataTypeUUID implements PersistentDataType<byte[], UUID> {

    PersistentDataTypeUUID() { }

    @Override
    public byte[] toPrimitive(UUID uuid, PersistentDataAdapterContext context) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    @Override
    public UUID fromPrimitive(byte[] uuidBytes, PersistentDataAdapterContext context) {
        ByteBuffer buffer = ByteBuffer.wrap(uuidBytes);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<UUID> getComplexType() {
        return UUID.class;
    }

}
