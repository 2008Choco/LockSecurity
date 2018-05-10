package me.choco.locksecurity.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurityPlugin;
import me.choco.locksecurity.api.data.ILockSecurityPlayer;
import me.choco.locksecurity.api.data.ILockedBlock;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.localization.Locale;

public class LockListCmd implements CommandExecutor {
	
	private final LockSecurityPlugin plugin;
	private final PlayerRegistry playerRegistry;
	
	public LockListCmd(LockSecurityPlugin plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		OfflinePlayer target = (sender instanceof Player ? (Player) sender : null);
		Locale locale = plugin.getLocale();
		
		if (args.length >= 1) {
			target = Bukkit.getOfflinePlayer(args[0]);
			if (target == null) {
				locale.getMessage(sender, "command.general.playeroffline")
					.param("%target%", args[0]).send();
				return true;
			}
			
			if (!target.hasPlayedBefore()) {
				locale.getMessage(sender, "command.general.neverplayed")
					.param("%target%", args[0]).send();
				return true;
			}
		}
		
		if (target == null) {
			locale.sendMessage(sender, "command.general.onlyplayers");
			return true;
		}
		
		// Permission check
		if (args.length == 0 && !sender.hasPermission("locks.locklist")) {
			locale.sendMessage(sender, "command.general.nopermission");
			return true;
		}
		else if (args.length >= 1 && !sender.hasPermission("locks.locklistother")) {
			locale.sendMessage(sender, "command.general.nopermission");
			return true;
		}
		
		this.displayLockInformation(sender, playerRegistry.getPlayer(target));
		return true;
	}
	
	private void displayLockInformation(CommandSender sender, ILockSecurityPlayer player) {
		this.plugin.getLocale().getMessage(sender, "command.locklist.identifier")
			.param("%player%", player.getPlayer().getName()).send();
		
		for (ILockedBlock block : player.getOwnedBlocks()) {
			Location location = block.getLocation();
			sender.sendMessage(ChatColor.YELLOW + "[ID: " + block.getLockID() + "] " + 
					location.getWorld().getName() + 
					" x:" + location.getBlockX() + 
					" y:" + location.getBlockY() + 
					" z:" + location.getBlockZ());
		}
	}
	
}