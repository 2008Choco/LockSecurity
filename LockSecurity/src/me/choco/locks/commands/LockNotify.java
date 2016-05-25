package me.choco.locks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.utils.LSMode;

public class LockNotify implements CommandExecutor{
	
	private LockSecurity plugin;
	public LockNotify(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args){
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (player.hasPermission("locks.locknotify")){
				if (!plugin.isInMode(player, LSMode.ADMIN_NOTIFY)){
					plugin.addMode(player, LSMode.ADMIN_NOTIFY);
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockNotify.LockNotifyEnabled"));
					return true;
				}else{
					plugin.removeMode(player, LSMode.ADMIN_NOTIFY);
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockNotify.LockNotifyDisabled"));
					return true;
				}
			}else{
				plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
				return true;
			}
		}else{
			plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.OnlyPlayers"));
			return true;
		}
	}
}