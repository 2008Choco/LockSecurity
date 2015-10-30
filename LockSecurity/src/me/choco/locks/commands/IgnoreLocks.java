package me.choco.locks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;

public class IgnoreLocks implements CommandExecutor{
	LockSecurity plugin;
	public IgnoreLocks(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (player.hasPermission("locks.ignorelocks")){
				if (plugin.ignoresLocks.contains(player.getName())){
					plugin.ignoresLocks.remove(player.getName());
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.IgnoreLocks.NoLongerIgnoringLocks"));
				}else{
					plugin.ignoresLocks.add(player.getName());
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.IgnoreLocks.IgnoringLocks"));
				}
			}
			else{
				plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
			}
			return true;
		}else{
			plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.OnlyPlayers"));
			return true;
		}
	}
}