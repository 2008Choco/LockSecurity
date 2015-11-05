package me.choco.LSaddon.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.LSaddon.ChestCollector;

public class CollectsCmd implements CommandExecutor{
	ChestCollector plugin;
	public CollectsCmd(ChestCollector plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (player.hasPermission("collectors.command")){
				if (args.length >= 1){
					String playerName = player.getName();
					String[] items = args[0].split(",");
					for (int i = 0; i < items.length; i++){items[i] = items[i].toUpperCase();}
					
					plugin.setCommandItems(playerName, items);
					plugin.collectorCreationMode.add(playerName);
					player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
							"Click on the chest you would like to make a converter");
				}else{
					if (plugin.collectorCreationMode.contains(player.getName())){
						plugin.collectorCreationMode.remove(player.getName());
						player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
								"Collector Creation Mode cancelled");
					}else{
						player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
								"Please specify which items you would like your chest to collect");
					}
				}
			}
			return true;
		}else{
			sender.sendMessage("This is a player owner command");
			return true;
		}
	}
}