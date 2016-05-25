package me.choco.locks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;
import me.choco.locks.utils.Keys;

public class GiveKey implements CommandExecutor{
	
	private LockSecurity plugin;
	private Keys keys;
	public GiveKey(LockSecurity plugin){
		this.plugin = plugin;
		this.keys = plugin.getKeyManager();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (player.hasPermission("locks.givekey")){
				if (args.length == 0){
					player.getInventory().addItem(keys.createUnsmithedKey(1));
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.GiveKey.ReceivedUnsmithedKey"));
				}
				if (args.length >= 1){
					if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0]))){
						Player targetPlayer = Bukkit.getPlayer(args[0]);
						int count = 1;
						if (args.length == 2){
							try {
								count = Integer.parseInt(args[1]);
							}catch(NumberFormatException e){
								plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.InvalidInteger").replaceAll("%param%", args[1]));
								return true;
							}
						}
						targetPlayer.getInventory().addItem(keys.createUnsmithedKey(count));
						plugin.sendPathMessage(targetPlayer, plugin.messages.getConfig().getString("Commands.GiveKey.TargetPlayerReceivedKey").replaceAll("%count%", String.valueOf(count)).replaceAll("%player%", player.getName()));
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.GiveKey.PlayerSentKey").replaceAll("%count%", String.valueOf(count)).replaceAll("%targetPlayer%", targetPlayer.getName()));
					}else{
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.PlayerOffline").replaceAll("%targetPlayer%", args[0]));
					}
				}
			}else{
				plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
			}
		}else{
			if (args.length == 0){
				plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.MustSpecifyPlayer"));
			}
			if (args.length >= 1){
				if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0]))){
					Player targetPlayer = Bukkit.getPlayer(args[0]);
					int count = 1;
					if (args.length == 2){
						try {
							count = Integer.parseInt(args[1]);
						}catch(NumberFormatException e){
							plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.InvalidInteger").replaceAll("%param%", args[1]));
							return true;
						}
					}
					targetPlayer.getInventory().addItem(keys.createUnsmithedKey(count));
					plugin.sendPathMessage(targetPlayer, plugin.messages.getConfig().getString("Commands.GiveKey.TargetPlayerReceivedKey").replaceAll("%count%", String.valueOf(count)).replaceAll("%player%", "CONSOLE"));
					plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.GiveKey.PlayerSentKey").replaceAll("%count%", String.valueOf(count)).replaceAll("%targetPlayer%", targetPlayer.getName()));
				}
			}
		}
		return true;
	}
}