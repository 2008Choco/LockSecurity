package me.choco.locksecurity.events.protection;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.choco.locksecurity.LockSecurityPlugin;
import me.choco.locksecurity.api.data.ILockSecurityPlayer;
import me.choco.locksecurity.api.data.ILockedBlock;
import me.choco.locksecurity.api.registration.ILockedBlockManager;
import me.choco.locksecurity.api.registration.IPlayerRegistry;

public class DoubleChestProtectionListener implements Listener {
	
	private static final BlockFace[] FACES = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	
	private final LockSecurityPlugin plugin;
	private final IPlayerRegistry playerRegistry;
	private final ILockedBlockManager lockedBlockManager;
	
	public DoubleChestProtectionListener(LockSecurityPlugin plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (block.getType().name().contains("CHEST")) {
			for (BlockFace face : FACES) {
				Block relative = block.getRelative(face);
				
				if (!relative.getType().equals(block.getType()) || !lockedBlockManager.isRegistered(block)) continue;
				
				ILockSecurityPlayer lPlayer = playerRegistry.getPlayer(player);
				ILockedBlock lBlock = lockedBlockManager.getLockedBlock(block);
				if (!lBlock.getOwner().equals(lPlayer)) {
					event.setCancelled(true);
					this.plugin.sendMessage(player, plugin.getLocale().getMessage("event.lock.cannotplace")
							.replace("%type%", block.getType().name())
							.replace("%player%", lBlock.getOwner().getPlayer().getName()));
					return;
				}
				
				this.lockedBlockManager.registerBlock(lockedBlockManager.createNewLock(lPlayer, block, lBlock));
				break;
			}
		}
	}
	
}