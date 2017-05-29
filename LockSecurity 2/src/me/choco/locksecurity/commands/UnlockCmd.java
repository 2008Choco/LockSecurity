package me.choco.locksecurity.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.api.event.PlayerUnlockBlockEvent;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.registration.LockedBlockManager;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.LSPlayer;

public class UnlockCmd implements CommandExecutor {
	
	private final LockSecurity plugin;
	private final PlayerRegistry playerRegistry;
	private final LockedBlockManager lockedBlockManager;
	
	public UnlockCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.onlyplayers"));
			return true;
		}
		
		Player player = (Player) sender;
		LSPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		if (args.length >= 1) {
			if (!sender.hasPermission("locks.unlock.id") || !sender.hasPermission("locks.unlock.admin")) {
				plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.nopermission"));
				return true;
			}
			
			int lockID = 0;
			try{
				lockID = Integer.parseInt(args[0]);
			}catch(NumberFormatException e) {
				plugin.sendMessage(player, plugin.getLocale().getMessage("command.general.invalidlockid")
						.replace("%ID%", args[0]));
				return true;
			}
			
			LockedBlock lBlock = lockedBlockManager.getLockedBlock(lockID);
			
			if (lBlock == null) {
				plugin.sendMessage(player, plugin.getLocale().getMessage("command.general.idnotlocked")
						.replace("%ID%", String.valueOf(lockID)));
				return true;
			}
			
			if (!lsPlayer.ownsBlock(lBlock) && !sender.hasPermission("locks.unlock.admin")) {
				plugin.sendMessage(player, plugin.getLocale().getMessage("command.unlock.notowner"));
				return true;
			}
			
			// PlayerUnlockBlockEvent
			PlayerUnlockBlockEvent plube = new PlayerUnlockBlockEvent(lsPlayer, lBlock, true);
			Bukkit.getPluginManager().callEvent(plube);
			if (plube.isCancelled()) return true;
			
			if (lockedBlockManager.isRegistered(lBlock)) lockedBlockManager.unregisterBlock(lBlock);
			lBlock.getOwner().removeBlockFromOwnership(lBlock);
			plugin.sendMessage(player, plugin.getLocale().getMessage("command.unlock.unlocked")
					.replace("%lockID%", String.valueOf(lockID)));
			return true;
		}
		
		if (!sender.hasPermission("locks.unlock.self")) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.nopermission"));
			return true;
		}
		
		plugin.sendMessage(player, plugin.getLocale().getMessage(lsPlayer.toggleMode(LSMode.UNLOCK)
				? "command.unlock.enabled"
				: "command.unlock.disabled"));
		return true;
	}
}