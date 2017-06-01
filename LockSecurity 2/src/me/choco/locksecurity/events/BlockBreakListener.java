package me.choco.locksecurity.events;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Door;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.api.event.PlayerUnlockBlockEvent;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.registration.LockedBlockManager;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.LSPlayer;

public class BlockBreakListener implements Listener {
	
	private final LockSecurity plugin;
	private final PlayerRegistry playerRegistry;
	private final LockedBlockManager lockedBlockManager;
	
	public BlockBreakListener(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@EventHandler
	public void onBreakLockedBlock(BlockBreakEvent event) {
		Block block = event.getBlock();
		
		if (!lockedBlockManager.isLockable(block)) return;
		if (!lockedBlockManager.isRegistered(block)) return;
		
		Player player = event.getPlayer();
		LSPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		LockedBlock lBlock = lockedBlockManager.getLockedBlock(block);
		if (!lBlock.isOwner(lsPlayer)) {
			if (!lsPlayer.isModeActive(LSMode.IGNORE_LOCKS) && !plugin.getConfig().getBoolean("Griefing.IgnorelocksCanBreakLocks")) {
				event.setCancelled(true);
				return;
			}
		}
		
		// PlayerUnlockBlockEvent
		PlayerUnlockBlockEvent plube = new PlayerUnlockBlockEvent(lsPlayer, lBlock);
		Bukkit.getPluginManager().callEvent(plube);
		if (plube.isCancelled()) {
			event.setCancelled(true);
			return;
		}
		
		lockedBlockManager.unregisterBlock(lBlock);
		lsPlayer.removeBlockFromOwnership(lBlock);
		if (block.getState().getData() instanceof Door) {
			if (!lBlock.hasSecondaryComponent()) return;
			
			LockedBlock lBlockSecondary = lBlock.getSecondaryComponent();
			lockedBlockManager.unregisterBlock(lBlockSecondary);
			lsPlayer.removeBlockFromOwnership(lBlockSecondary);
		}
		
		plugin.sendMessage(player, plugin.getLocale().getMessage("command.unlock.unlocked")
				.replace("%lockID%", String.valueOf(lBlock.getLockID())));
	}
}