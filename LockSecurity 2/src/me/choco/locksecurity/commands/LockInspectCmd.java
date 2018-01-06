package me.choco.locksecurity.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.ILockSecurityPlayer;
import me.choco.locksecurity.api.ILockedBlock;
import me.choco.locksecurity.api.ILockedBlockManager;
import me.choco.locksecurity.api.IPlayerRegistry;
import me.choco.locksecurity.api.utils.LSMode;

public class LockInspectCmd implements CommandExecutor {
	
	private final LockSecurity plugin;
	private final IPlayerRegistry playerRegistry;
	private final ILockedBlockManager lockedBlockManager;
	
	public LockInspectCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.onlyplayers"));
			return true;
		}
		
		if (!sender.hasPermission("locks.lockinspect")) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.nopermission"));
			return true;
		}
		
		Player player = (Player) sender;
		ILockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		if (args.length >= 1) {
			int lockID = -1;
			try {
				lockID = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				plugin.sendMessage(player, plugin.getLocale().getMessage("command.general.invalidlockid")
						.replace("%ID%", args[0]));
			}
			
			ILockedBlock lBlock = lockedBlockManager.getLockedBlock(lockID);
			
			if (lBlock == null) {
				plugin.sendMessage(player, plugin.getLocale().getMessage("command.general.idnotlocked")
						.replace("%ID%", String.valueOf(lockID)));
				return true;
			}
			
			this.displayInformation(player, lBlock);
			return true;
		}
		
		plugin.sendMessage(player, plugin.getLocale().getMessage(lsPlayer.toggleMode(LSMode.LOCK_INSPECT)
				? "command.lockinspect.enabled"
				: "command.lockinspect.disabled"));
		return true;
	}
	
	private void displayInformation(Player player, ILockedBlock block) {
		OfflinePlayer owner = block.getOwner().getPlayer();
		Location location = block.getLocation();
		
		player.sendMessage(ChatColor.GOLD + "- - - - - - " + ChatColor.DARK_AQUA + "Lock information " + ChatColor.GOLD + "- - - - - -");
		player.sendMessage(ChatColor.GOLD + "Lock ID: " + ChatColor.AQUA + block.getLockID());
		player.sendMessage(ChatColor.GOLD + "Key ID: " + ChatColor.AQUA + block.getKeyID());
		player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + owner.getName() + " (" + ChatColor.GOLD + owner.getUniqueId() + ChatColor.AQUA + ")");
		player.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.AQUA + location.getWorld().getName() + " x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ());
	}
	
}