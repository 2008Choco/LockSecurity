package me.choco.locks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.utils.LSMode;

public class TransferLock implements CommandExecutor{
	
	private LockSecurity plugin;
	public TransferLock(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (player.hasPermission("locks.transferlock")){
				if (!plugin.isInMode(player, LSMode.TRANSFER_LOCK)){
					if (args.length == 1){
						if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()){
							plugin.addMode(player, LSMode.TRANSFER_LOCK);
							plugin.transferTo.put(player.getName(), args[0]);
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.TransferLock.TransferModeEnabled"));
						}else{
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.PlayerNeverPlayedBefore")
									.replace("%targetPlayer%", args[0]));
						}
						return true;
					}else if (args.length == 2){
						//TODO /transferlock <player> <LockID>
					}
				}else{
					plugin.removeMode(player, LSMode.TRANSFER_LOCK);
					plugin.transferTo.remove(player.getName());
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.TransferLock.TransferModeDisabled"));
					return true;
				}
			}
		}else{
			plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.OnlyPlayers"));
			return true;
		}
		return false;
	}
}