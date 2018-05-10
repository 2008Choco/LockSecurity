package me.choco.locksecurity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurityPlugin;
import me.choco.locksecurity.api.data.ILockSecurityPlayer;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.registration.PlayerRegistry;

public class IgnoreLocksCmd implements CommandExecutor {
	
	private final LockSecurityPlugin plugin;
	private final PlayerRegistry playerRegistry;
	
	public IgnoreLocksCmd(LockSecurityPlugin plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			this.plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.onlyplayers"));
			return true;
		}
		
		if (!sender.hasPermission("locks.ignorelocks")) {
			this.plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.nopermission"));
			return true;
		}
		
		Player player = (Player) sender;
		ILockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		this.plugin.sendMessage(sender, plugin.getLocale().getMessage(lsPlayer.toggleMode(LSMode.IGNORE_LOCKS) 
				? "command.ignorelocks.enabled" 
				: "command.ignorelocks.disabled"));
		return true;
	}
	
}