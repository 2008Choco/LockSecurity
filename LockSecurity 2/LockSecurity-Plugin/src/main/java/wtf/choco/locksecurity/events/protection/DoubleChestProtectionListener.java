package wtf.choco.locksecurity.events.protection;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import wtf.choco.locksecurity.LockSecurityPlugin;
import wtf.choco.locksecurity.api.data.ILockSecurityPlayer;
import wtf.choco.locksecurity.api.data.ILockedBlock;
import wtf.choco.locksecurity.api.registration.ILockedBlockManager;
import wtf.choco.locksecurity.registration.PlayerRegistry;

public class DoubleChestProtectionListener implements Listener {
	
	private static final BlockFace[] FACES = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	
	private final LockSecurityPlugin plugin;
	private final ILockedBlockManager lockedBlockManager;
	private final PlayerRegistry playerRegistry;
	
	public DoubleChestProtectionListener(LockSecurityPlugin plugin) {
		this.plugin = plugin;
		this.lockedBlockManager = plugin.getLockedBlockManager();
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (block.getType().name().contains("CHEST")) {
			for (BlockFace face : FACES) {
				Block relative = block.getRelative(face);
				
				if (!relative.getType().equals(block.getType()) || !lockedBlockManager.isLockedBlock(block)) continue;
				
				ILockSecurityPlayer lPlayer = playerRegistry.getPlayer(player);
				ILockedBlock lBlock = lockedBlockManager.getLockedBlock(block);
				if (!lBlock.getOwner().equals(lPlayer)) {
					event.setCancelled(true);
					this.plugin.getLocale().getMessage("event.lock.cannotplace")
						.param("%type%", block.getType())
						.param("%player%", lBlock.getOwner().getPlayer().getName()).send();
					return;
				}
				
				this.lockedBlockManager.registerBlock(lockedBlockManager.createNewLock(lPlayer, block, lBlock));
				break;
			}
		}
	}
	
}