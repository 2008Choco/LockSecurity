package me.choco.locksecurity.utils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.utils.ConfigOption;

public class LockSecurityCmd implements CommandExecutor {
	
	private YamlConfiguration pluginYML;
	
	private LockSecurity plugin;
	public LockSecurityCmd(LockSecurity plugin) {
		this.plugin = plugin;
	}
	
	/* Usage:
	 *   /locksecurity <reload|version>
	 */
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1){
			if (args[0].equalsIgnoreCase("reload")){
				plugin.reloadConfig();
				ConfigOption.loadConfigurationValues(plugin);
				plugin.sendMessage(sender, ChatColor.GREEN + "Configuration file successfully reloaded");
			}
			
			else if (args[0].equalsIgnoreCase("version")){
				if (pluginYML == null) 
					this.pluginYML = YamlConfiguration.loadConfiguration(getClass().getResourceAsStream("/plugin.yml"));
				
				sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Version: " + ChatColor.RESET + ChatColor.GRAY  + plugin.getDescription().getVersion());
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "API Version: " + ChatColor.RESET + ChatColor.GRAY + pluginYML.getString("apiversion"));
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Developer / Maintainer: " + ChatColor.RESET + ChatColor.GRAY + "2008Choco");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Development Page: " + ChatColor.RESET + ChatColor.GRAY + "http://dev.bukkit.org/bukkit-plugins/lock-security");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Report Bugs To: " + ChatColor.RESET + ChatColor.GRAY + "http://dev.bukkit.org/bukkit-plugins/lock-security/tickets");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Add-Ons: " + ChatColor.RESET + ChatColor.GRAY + "Work In Progress - Please do create some, devs");
				sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
			}
			
			else{ plugin.sendMessage(sender, "/locksecurity <reload|version>"); }
		}else{ plugin.sendMessage(sender, "/locksecurity <reload|version>"); }
		return true;
	}
}