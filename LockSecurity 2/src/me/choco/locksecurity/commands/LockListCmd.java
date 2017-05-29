package me.choco.locksecurity.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.LSPlayer;

public class LockListCmd implements CommandExecutor {
	
	private final LockSecurity plugin;
	private final PlayerRegistry playerRegistry;
	
	public LockListCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		OfflinePlayer target = (sender instanceof Player ? (Player) sender : null);
		
		if (args.length >= 1) {
			target = Bukkit.getOfflinePlayer(args[0]);
			if (target == null) {
				plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.playeroffline")
						.replace("%target%", args[0]));
				return true;
			}
			
			if (!target.hasPlayedBefore()) {
				plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.neverplayed")
						.replace("%target%", args[0]));
				return true;
			}
		}
		
		if (target == null) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.onlyplayers"));
			return true;
		}
		
		// Permission check
		if (args.length == 0 && !sender.hasPermission("locks.locklist")) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.nopermission"));
			return true;
		}
		else if (args.length >= 1 && sender.hasPermission("locks.locklistother")) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.nopermission"));
			return true;
		}
		
		this.displayLockInformation(sender, playerRegistry.getPlayer(target));
		return true;
	}
	
	private void displayLockInformation(CommandSender sender, LSPlayer player) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocale().getMessage("command.locklist.identifier"))
				.replace("%player%", player.getPlayer().getName()));
		for (LockedBlock block : player.getOwnedBlocks()) {
			Location location = block.getLocation();
			sender.sendMessage(ChatColor.YELLOW + "[ID: " + block.getLockID() + "] " + 
					location.getWorld().getName() + 
					" x:" + location.getBlockX() + 
					" y:" + location.getBlockY() + 
					" z:" + location.getBlockZ());
		}
	}
}