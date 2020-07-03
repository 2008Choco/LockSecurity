package wtf.choco.locksecurity.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.block.LockedBlockManager;

public final class LockedBlockProtectionListener implements Listener {

    private static final Map<UUID, WarningData> LAST_WARNING = new HashMap<>();
    private static final long WARNING_TIME_MILLIS = TimeUnit.SECONDS.toMillis(5);

    private final LockSecurity plugin;

    public LockedBlockProtectionListener(LockSecurity plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreakLockedBlock(BlockBreakEvent event) {
        LockedBlockManager manager = plugin.getLockedBlockManager();
        Block block = event.getBlock();
        LockedBlock lockedBlock = manager.getLockedBlock(block);

        if (lockedBlock == null) {
            return;
        }

        // Owner is allowed to break the block. It will unlock it
        Player player = event.getPlayer();
        if (lockedBlock.isOwner(player)) {
            manager.unregisterLockedBlock(lockedBlock);
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1.5F);
            return;
        }

        // Non-owners cannot break locked blocks
        LAST_WARNING.computeIfAbsent(player.getUniqueId(), u -> new WarningData()).warnIfNecessary(player, block, "You cannot destroy a locked block you do not own");
        event.setCancelled(true);
    }

    @EventHandler
    public void onExplodeLockedBlock(BlockExplodeEvent event) {
        LockedBlockManager manager = plugin.getLockedBlockManager();
        event.blockList().removeIf(manager::isLocked);
    }

    @EventHandler
    public void onExplodeLockedBlock(EntityExplodeEvent event) {
        LockedBlockManager manager = plugin.getLockedBlockManager();
        event.blockList().removeIf(manager::isLocked);
    }

    @EventHandler
    public void onLockedBlockBurn(BlockBurnEvent event) {
        if (plugin.getLockedBlockManager().isLocked(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonMoveLockedBlock(BlockPistonExtendEvent event) {
        this.cancelPistonMovement(event, event.getBlocks());
    }

    @EventHandler
    public void onPistonMoveLockedBlock(BlockPistonRetractEvent event) {
        this.cancelPistonMovement(event, event.getBlocks());
    }

    private void cancelPistonMovement(Cancellable event, List<Block> blocks) {
        LockedBlockManager manager = plugin.getLockedBlockManager();

        for (Block block : blocks) {
            if (manager.isLocked(block)) {
                event.setCancelled(true);
                break;
            }
        }
    }

    private final class WarningData {

        private final Map<Block, Long> lastWarning = new HashMap<>();

        private WarningData() { }

        public void warnIfNecessary(Player player, Block block, String warning) {
            long now = System.currentTimeMillis();
            if (now - lastWarning.computeIfAbsent(block, b -> 0L) >= WARNING_TIME_MILLIS) {
                this.lastWarning.put(block, now);
                player.sendMessage(warning);
            }
        }

    }

}
