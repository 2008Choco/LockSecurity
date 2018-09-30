package wtf.choco.locksecurity.events;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Door;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.api.LSMode;
import wtf.choco.locksecurity.api.event.PlayerUnlockBlockEvent;
import wtf.choco.locksecurity.data.LockSecurityPlayer;
import wtf.choco.locksecurity.data.LockedBlock;
import wtf.choco.locksecurity.registration.LockedBlockManager;
import wtf.choco.locksecurity.registration.PlayerRegistry;

public class BlockBreakListener implements Listener {
	
	private final LockSecurity plugin;
	private final LockedBlockManager lockedBlockManager;
	private final PlayerRegistry playerRegistry;
	
	public BlockBreakListener(LockSecurity plugin) {
		this.plugin = plugin;
		this.lockedBlockManager = plugin.getLockedBlockManager();
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@EventHandler
	public void onBreakLockedBlock(BlockBreakEvent event) {
		Block block = event.getBlock();
		
		if (!lockedBlockManager.isLockable(block)) return;
		if (!lockedBlockManager.isLockedBlock(block)) return;
		
		Player player = event.getPlayer();
		LockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		LockedBlock lBlock = lockedBlockManager.getLockedBlock(block);
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
			
			LockedBlock lBlockSecondary = lBlock.getSecondaryComponent();
			this.lockedBlockManager.unregisterBlock(lBlockSecondary);
			lsPlayer.removeBlockFromOwnership(lBlockSecondary);
		}
		
		this.plugin.getLocale().getMessage(player, "command.unlock.unlocked")
			.param("%lockID%", lBlock.getLockID()).send();
	}
	
}