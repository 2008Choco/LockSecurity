package me.choco.locksecurity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.ILockSecurityPlayer;
import me.choco.locksecurity.api.IPlayerRegistry;
import me.choco.locksecurity.api.utils.LSMode;

public class LockNotifyCmd implements CommandExecutor {

	private final LockSecurity plugin;
	private final IPlayerRegistry playerRegistry;
	
	public LockNotifyCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.onlyplayers"));
			return true;
		}
		
		if (!sender.hasPermission("locks.locknotify")) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.nopermission"));
			return true;
		}
		
		Player player = (Player) sender;
		ILockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);

		plugin.sendMessage(player, plugin.getLocale().getMessage(lsPlayer.toggleMode(LSMode.ADMIN_NOTIFY)
				? "command.locknotify.enabled"
				: "command.locknotify.disabled"));
		return true;
	}
}