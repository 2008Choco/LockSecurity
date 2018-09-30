package wtf.choco.locksecurity.events.protection;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.data.LockSecurityPlayer;
import wtf.choco.locksecurity.data.LockedBlock;
import wtf.choco.locksecurity.registration.LockedBlockManager;
import wtf.choco.locksecurity.registration.PlayerRegistry;

public class DoubleChestProtectionListener implements Listener {
	
	private static final BlockFace[] FACES = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	
	private final LockSecurity plugin;
	private final LockedBlockManager lockedBlockManager;
	private final PlayerRegistry playerRegistry;
	
	public DoubleChestProtectionListener(LockSecurity plugin) {
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
				
				LockSecurityPlayer lPlayer = playerRegistry.getPlayer(player);
				LockedBlock lBlock = lockedBlockManager.getLockedBlock(block);
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