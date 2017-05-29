package me.choco.locksecurity.events.protection;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.registration.LockedBlockManager;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.LSPlayer;

public class DoubleChestProtectionListener implements Listener {
	
	private final LockSecurity plugin;
	private final PlayerRegistry playerRegistry;
	private final LockedBlockManager lockedBlockManager;
	
	public DoubleChestProtectionListener(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (block.getType().name().contains("CHEST")) {
			for (BlockFace face : FACES) {
				Block relative = block.getRelative(face);
				
				if (!relative.getType().equals(block.getType()) || !lockedBlockManager.isRegistered(block)) continue;
				
				LSPlayer lPlayer = playerRegistry.getPlayer(player);
				LockedBlock lBlock = lockedBlockManager.getLockedBlock(block);
				if (!lBlock.getOwner().equals(lPlayer)) {
					event.setCancelled(true);
					plugin.sendMessage(player, plugin.getLocale().getMessage("event.lock.cannotplace")
							.replace("%type%", block.getType().name())
							.replace("%player%", lBlock.getOwner().getPlayer().getName()));
					return;
				}
				
				lockedBlockManager.registerBlock(new LockedBlock(lPlayer, block, lockedBlockManager.getNextLockID(), lBlock.getKeyID(), lBlock));
				break;
			}
		}
	}
}