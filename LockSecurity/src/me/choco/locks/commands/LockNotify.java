package me.choco.locks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;

public class LockNotify implements CommandExecutor{
	LockSecurity plugin;
	public LockNotify(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args){
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (player.hasPermission("locks.locknotify")){
				if (plugin.adminNotify.contains(player.getName())){
					plugin.adminNotify.remove(player.getName());
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockNotify.LockNotifyDisabled"));
					return true;
				}else{
					plugin.adminNotify.add(player.getName());
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockNotify.LockNotifyEnabled"));
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