package wtf.choco.locksecurity.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.utils.localization.Locale;

public class LockSecurityCmd implements CommandExecutor {
	
	private final LockSecurity plugin;
	
	public LockSecurityCmd(LockSecurity plugin) {
		this.plugin = plugin;
	}
	
	/* Usage:
	 *   /locksecurity <reload|version>
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Locale locale = plugin.getLocale();
		
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (!sender.hasPermission("locks.reload")) {
					locale.sendMessage(sender, "command.general.nopermission");
					return true;
				}
				
				this.plugin.reloadConfig();
				this.plugin.getLocale().reloadMessages();
				locale.sendMessage(sender, "command.locksecurity.reloaded");
			}
			
			else if (args[0].equalsIgnoreCase("version")) {
				sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Version: " + ChatColor.RESET + ChatColor.GRAY  + plugin.getDescription().getVersion());
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Developer / Maintainer: " + ChatColor.RESET + ChatColor.GRAY + "2008Choco");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Development Page: " + ChatColor.RESET + ChatColor.GRAY + "http://dev.bukkit.org/bukkit-plugins/lock-security");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Report Bugs To: " + ChatColor.RESET + ChatColor.GRAY + "http://dev.bukkit.org/bukkit-plugins/lock-security/tickets");
				sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Add-Ons: " + ChatColor.RESET + ChatColor.GRAY + "Work In Progress - Please do create some, devs");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
			}
			
			else {
				this.plugin.sendMessage(sender, "/locksecurity <reload|version>");
			}
		}
		else{
			this.plugin.sendMessage(sender, "/locksecurity <reload|version>");
		}
		return true;
	}
	
}