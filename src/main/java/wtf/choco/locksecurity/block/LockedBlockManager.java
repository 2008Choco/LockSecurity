package wtf.choco.locksecurity.block;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import wtf.choco.locksecurity.LockSecurity;

public final class LockedBlockManager {

    private final Set<LockedBlock> registered = new HashSet<>();
    private final Map<Block, LockedBlock> fromBlock = new HashMap<>();
    private final Multimap<UUID, LockedBlock> fromPlayer = HashMultimap.create();

    public void registerLockedBlock(LockedBlock block) {
        Preconditions.checkArgument(block != null, "Cannot register null locked block");

        if (!registered.add(block)) {
            return;
        }

        this.fromPlayer.put(block.getOwner().getUniqueId(), block);
        this.fromBlock.put(block.getBlock(), block);
        if (block instanceof LockedMultiBlock) {
            this.fromBlock.put(((LockedMultiBlock) block).getSecondaryBlock(), block);
        }
    }

    public LockedBlock getLockedBlock(Block block) {
        return fromBlock.get(block);
    }

    public Collection<LockedBlock> getLockedBlocks(OfflinePlayer player) {
        return fromPlayer.get(player.getUniqueId());
    }

    public void unregisterLockedBlock(LockedBlock block) {
        Preconditions.checkArgument(block != null, "Cannot unregister null locked block");
        this.unregisterLockedBlock(block.getBlock());
    }

    public LockedBlock unregisterLockedBlock(Block block) {
        if (!registered.remove(getLockedBlock(block))) {
            return null;
        }

        LockedBlock removed = fromBlock.remove(block);
        if (removed instanceof LockedMultiBlock && removed != fromBlock.remove(((LockedMultiBlock) removed).getSecondaryBlock())) {
            throw new IllegalStateException("Invalid state removal of locked multi block. Mismatching block states?");
        }

        this.fromPlayer.remove(removed.getOwner().getUniqueId(), removed);
        return removed;
    }

    public boolean isLocked(Block block) {
        return fromBlock.containsKey(block);
    }

    public Set<LockedBlock> getLockedBlocks() {
        return Collections.unmodifiableSet(registered);
    }

    public void clear() {
        this.registered.clear();
        this.fromBlock.clear();
        this.fromPlayer.clear();
    }

    public JsonObject write(JsonObject object) {
        JsonArray blocksArray = new JsonArray();
        {
            this.registered.forEach(lockedBlock -> blocksArray.add(lockedBlock.write(new JsonObject())));
        }

        object.add("blocks", blocksArray);
        return object;
    }

    public void read(JsonObject object, boolean clearRegistry) {
        Preconditions.checkState(object.has("blocks"), "Missing blocks array");

        if (clearRegistry) {
            this.clear();
        }

        Logger logger = LockSecurity.getInstance().getLogger();
        JsonArray blocksArray = object.getAsJsonArray("blocks");
        for (JsonElement blockElement : blocksArray) {
            if (!blockElement.isJsonObject()) {
                logger.warning("Could not load block... expected object, got " + blockElement.getClass().getSimpleName() + " instead. Ignoring...");
                continue;
            }

            JsonObject blockObject = blockElement.getAsJsonObject();
            this.registerLockedBlock(blockObject.has("secondaryLocation") ? new LockedMultiBlock(blockObject) : new LockedBlock(blockObject));
        }
    }

}
