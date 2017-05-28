package me.choco.locksecurity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.LSPlayer;

public class IgnoreLocksCmd implements CommandExecutor {
	
	private LockSecurity plugin;
	private PlayerRegistry playerRegistry;
	
	public IgnoreLocksCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)){
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.onlyplayers"));
			return true;
		}
		
		Player player = (Player) sender;
		LSPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		plugin.sendMessage(sender, plugin.getLocale().getMessage(lsPlayer.toggleMode(LSMode.IGNORE_LOCKS) 
				? "command.ignorelocks.enabled" 
				: "command.ignorelocks.disabled"));
		return true;
	}
}