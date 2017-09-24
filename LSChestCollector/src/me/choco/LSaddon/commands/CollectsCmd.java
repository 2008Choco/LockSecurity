package me.choco.LSaddon.commands;

import java.util.Arrays;

import org.apache.commons.lang3.EnumUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.LSaddon.ChestCollector;

public class CollectsCmd implements CommandExecutor {
	
	private final ChestCollector plugin;
	
	public CollectsCmd(ChestCollector plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players are capable of creating collectors!");
			return true;
		}
		
		Player player = (Player) sender;
		if (!player.hasPermission("collectors.command")) {
			player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.RED + 
					"You do not have the sufficient privileges to run this command");
		}
		
		if (args.length == 0) {
			if (plugin.hasCommandItems(player)) {
				this.plugin.clearCommandItems(player);
				player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
						"Collector Creation Mode cancelled");
			}
			else {
				player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
						"Please specify which items you would like your chest to collect");
			}
			
			return true;
		}
		
		Material[] materials = Arrays.stream(args[0].split(","))
				.map(m -> EnumUtils.getEnum(Material.class, m.toUpperCase()))
				.filter(m -> m != null)
				.toArray(Material[]::new);
		
		this.plugin.setCommandItems(player, materials);
		player.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + "Collector" + ChatColor.GOLD + "] " + ChatColor.GRAY + 
				"Click on the chest you would like to make a collector");
		return true;
	}
}