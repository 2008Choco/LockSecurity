package me.choco.locksecurity.events.protection;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.ILockSecurityPlayer;
import me.choco.locksecurity.api.ILockedBlock;
import me.choco.locksecurity.api.ILockedBlockManager;
import me.choco.locksecurity.api.IPlayerRegistry;
import me.choco.locksecurity.data.LockedBlock;

public class DoubleChestProtectionListener implements Listener {
	
	private static final BlockFace[] FACES = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	
	private final LockSecurity plugin;
	private final IPlayerRegistry playerRegistry;
	private final ILockedBlockManager lockedBlockManager;
	
	public DoubleChestProtectionListener(LockSecurity plugin) {
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
				
				this.lockedBlockManager.registerBlock(new LockedBlock(lPlayer, block, lockedBlockManager.getNextLockID(), lBlock.getKeyID(), lBlock));
				break;
			}
		}
	}
	
}