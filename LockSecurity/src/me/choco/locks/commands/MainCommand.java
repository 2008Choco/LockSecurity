package me.choco.locks.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;

import me.choco.locks.LockSecurity;

public class MainCommand implements CommandExecutor, TabCompleter{
	LockSecurity plugin;
	public MainCommand(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("reload")){
				if (sender.hasPermission("locks.reload")){
					plugin.reloadConfig();
					plugin.messages.reloadConfig();
					plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.LockSecurity.SuccessfullyReloaded"));
				}else{
					plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("version")){
				@SuppressWarnings("deprecation")
				YamlConfiguration pluginFile = YamlConfiguration.loadConfiguration(getClass().getResourceAsStream("/plugin.yml"));
				sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Version: " + ChatColor.RESET + ChatColor.GRAY  + plugin.getDescription().getVersion());
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "API Version: " + ChatColor.RESET + ChatColor.GRAY + pluginFile.getString("apiversion"));
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Developer / Maintainer: " + ChatColor.RESET + ChatColor.GRAY + "2008Choco");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Development Page: " + ChatColor.RESET + ChatColor.GRAY + "http://dev.bukkit.org/bukkit-plugins/lock-security");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Report Bugs To: " + ChatColor.RESET + ChatColor.GRAY + "http://dev.bukkit.org/bukkit-plugins/lock-security/tickets");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Add-Ons: " + ChatColor.RESET + ChatColor.GRAY + "Work In Progress - Please do create some, devs");
				sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
				return true;
			}
			else if (args[0].equalsIgnoreCase("forcesave")){
				if (sender.hasPermission("locks.forcesave")){
					plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.DataTransfer.Commencing"));
					plugin.getLocalizedData().saveLocalizedDataToDatabase(true);
					plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.DataTransfer.SuccessfulTransfer"));
				}else{
					plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		List<String> possibleArgs = new ArrayList<String>();
		if (args.length == 1){
			if (sender.hasPermission("locks.reload"))
				possibleArgs.add("reload");
			possibleArgs.add("version");
			return possibleArgs;
		}
		return null;
	}
}