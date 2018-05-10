package me.choco.locksecurity.events;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Door;

import me.choco.locksecurity.LockSecurityPlugin;
import me.choco.locksecurity.api.data.ILockSecurityPlayer;
import me.choco.locksecurity.api.data.ILockedBlock;
import me.choco.locksecurity.api.event.PlayerUnlockBlockEvent;
import me.choco.locksecurity.api.registration.ILockedBlockManager;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.registration.PlayerRegistry;

public class BlockBreakListener implements Listener {
	
	private final LockSecurityPlugin plugin;
	private final ILockedBlockManager lockedBlockManager;
	private final PlayerRegistry playerRegistry;
	
	public BlockBreakListener(LockSecurityPlugin plugin) {
		this.plugin = plugin;
		this.lockedBlockManager = plugin.getLockedBlockManager();
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@EventHandler
	public void onBreakLockedBlock(BlockBreakEvent event) {
		Block block = event.getBlock();
		
		if (!lockedBlockManager.isLockable(block)) return;
		if (!lockedBlockManager.isRegistered(block)) return;
		
		Player player = event.getPlayer();
		ILockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		ILockedBlock lBlock = lockedBlockManager.getLockedBlock(block);
		if (!lBlock.isOwner(lsPlayer) && !player.hasPermission("locks.admin")) {
			if (!lsPlayer.isModeEnabled(LSMode.IGNORE_LOCKS) && !plugin.getConfig().getBoolean("Griefing.IgnorelocksCanBreakLocks")) {
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
		
		this.lockedBlockManager.unregisterBlock(lBlock);
		lsPlayer.removeBlockFromOwnership(lBlock);
		
		if (block.getState().getData() instanceof Door) {
			if (!lBlock.hasSecondaryComponent()) return;
			
			ILockedBlock lBlockSecondary = lBlock.getSecondaryComponent();
			this.lockedBlockManager.unregisterBlock(lBlockSecondary);
			lsPlayer.removeBlockFromOwnership(lBlockSecondary);
		}
		
		this.plugin.getLocale().getMessage(player, "command.unlock.unlocked")
			.param("%lockID%", lBlock.getLockID()).send();
	}
	
}