package wtf.choco.locksecurity.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wtf.choco.locksecurity.LockSecurityPlugin;
import wtf.choco.locksecurity.api.data.ILockSecurityPlayer;
import wtf.choco.locksecurity.api.data.ILockedBlock;
import wtf.choco.locksecurity.api.event.PlayerUnlockBlockEvent;
import wtf.choco.locksecurity.api.registration.ILockedBlockManager;
import wtf.choco.locksecurity.api.utils.LSMode;
import wtf.choco.locksecurity.registration.PlayerRegistry;
import wtf.choco.locksecurity.utils.localization.Locale;

public class UnlockCmd implements CommandExecutor {
	
	private final LockSecurityPlugin plugin;
	private final ILockedBlockManager lockedBlockManager;
	private final PlayerRegistry playerRegistry;
	
	public UnlockCmd(LockSecurityPlugin plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Locale locale = plugin.getLocale();
		
		if (!(sender instanceof Player)) {
			locale.sendMessage(sender, "command.general.onlyplayers");
			return true;
		}
		
		Player player = (Player) sender;
		ILockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		if (args.length >= 1) {
			if (!player.hasPermission("locks.unlock.id") && !player.hasPermission("locks.unlock.admin")) {
				locale.sendMessage(player, "command.general.nopermission");
				return true;
			}
			
			int lockID = 0;
			try {
				lockID = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				locale.getMessage(player, "command.general.invalidlock")
					.param("%ID%", args[0]).send();
				return true;
			}
			
			ILockedBlock lBlock = lockedBlockManager.getLockedBlock(lockID);
			
			if (lBlock == null) {
				locale.getMessage(player, "command.general.idnotlocked")
					.param("%ID%", lockID).send();
				return true;
			}
			
			if (!lsPlayer.ownsBlock(lBlock) && !sender.hasPermission("locks.unlock.admin")) {
				locale.sendMessage(player, "command.unlock.notowner");
				return true;
			}
			
			// PlayerUnlockBlockEvent
			PlayerUnlockBlockEvent plube = new PlayerUnlockBlockEvent(lsPlayer, lBlock, true);
			Bukkit.getPluginManager().callEvent(plube);
			if (plube.isCancelled()) return true;
			
			if (lockedBlockManager.isRegistered(lBlock)) lockedBlockManager.unregisterBlock(lBlock);
			lBlock.getOwner().removeBlockFromOwnership(lBlock);
			locale.getMessage(player, "command.unlock.unlocked")
				.param("%lockID%", lockID).send();
			return true;
		}
		
		if (!sender.hasPermission("locks.unlock.self")) {
			locale.sendMessage(player, "command.general.nopermission");
			return true;
		}
		
		locale.sendMessage(player, lsPlayer.toggleMode(LSMode.UNLOCK) ? "command.unlock.enabled" : "command.unlock.disabled");
		return true;
	}
	
}