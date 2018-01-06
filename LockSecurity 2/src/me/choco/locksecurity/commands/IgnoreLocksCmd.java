package me.choco.locksecurity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.ILockSecurityPlayer;
import me.choco.locksecurity.api.IPlayerRegistry;
import me.choco.locksecurity.api.utils.LSMode;

public class IgnoreLocksCmd implements CommandExecutor {
	
	private final LockSecurity plugin;
	private final IPlayerRegistry playerRegistry;
	
	public IgnoreLocksCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.onlyplayers"));
			return true;
		}
		
		if (!sender.hasPermission("locks.ignorelocks")) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.nopermission"));
			return true;
		}
		
		Player player = (Player) sender;
		ILockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		plugin.sendMessage(sender, plugin.getLocale().getMessage(lsPlayer.toggleMode(LSMode.IGNORE_LOCKS) 
				? "command.ignorelocks.enabled" 
				: "command.ignorelocks.disabled"));
		return true;
	}
	
}