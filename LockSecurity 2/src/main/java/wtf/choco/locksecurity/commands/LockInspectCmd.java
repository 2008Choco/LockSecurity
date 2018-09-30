package wtf.choco.locksecurity.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.api.LSMode;
import wtf.choco.locksecurity.data.LockSecurityPlayer;
import wtf.choco.locksecurity.data.LockedBlock;
import wtf.choco.locksecurity.registration.LockedBlockManager;
import wtf.choco.locksecurity.registration.PlayerRegistry;
import wtf.choco.locksecurity.utils.localization.Locale;

public class LockInspectCmd implements CommandExecutor {
	
	private final LockSecurity plugin;
	private final LockedBlockManager lockedBlockManager;
	private final PlayerRegistry playerRegistry;
	
	public LockInspectCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.lockedBlockManager = plugin.getLockedBlockManager();
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Locale locale = plugin.getLocale();
		if (!(sender instanceof Player)) {
			locale.sendMessage(sender, "command.general.onlyplayers");
			return true;
		}
		
		if (!sender.hasPermission("locks.lockinspect")) {
			locale.sendMessage(sender, "command.general.nopermission");
			return true;
		}
		
		Player player = (Player) sender;
		LockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		if (args.length >= 1) {
			int lockID = -1;
			try {
				lockID = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				locale.getMessage(player, "command.general.invalidlockid")
					.param("%ID%", args[0]);
			}
			
			LockedBlock lBlock = lockedBlockManager.getLockedBlock(lockID);
			
			if (lBlock == null) {
				locale.getMessage(player, "command.general.idnotlocked")
					.param("%ID%", lockID);
				return true;
			}
			
			this.displayInformation(player, lBlock);
			return true;
		}
		
		locale.sendMessage(player, lsPlayer.toggleMode(LSMode.LOCK_INSPECT) ? "command.lockinspect.enabled" : "command.lockinspect.disabled");
		return true;
	}
	
	private void displayInformation(Player player, LockedBlock block) {
		OfflinePlayer owner = block.getOwner().getPlayer();
		Location location = block.getLocation();
		
		player.sendMessage(ChatColor.GOLD + "- - - - - - " + ChatColor.DARK_AQUA + "Lock information " + ChatColor.GOLD + "- - - - - -");
		player.sendMessage(ChatColor.GOLD + "Lock ID: " + ChatColor.AQUA + block.getLockID());
		player.sendMessage(ChatColor.GOLD + "Key ID: " + ChatColor.AQUA + block.getKeyID());
		player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + owner.getName() + " (" + ChatColor.GOLD + owner.getUniqueId() + ChatColor.AQUA + ")");
		player.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.AQUA + location.getWorld().getName() + " x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ());
	}
	
}