package me.choco.locksecurity.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.LSPlayer;

public class LockNotifyCmd implements CommandExecutor {

	private LockSecurity plugin;
	private PlayerRegistry playerRegistry;
	
	public LockNotifyCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)){
			plugin.sendMessage(sender, "The console is not permitted to toggle lock notifications");
			return true;
		}
		
		Player player = (Player) sender;
		LSPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		plugin.sendMessage(player, LSMode.ADMIN_NOTIFY.getName() + " mode " + 
				(lsPlayer.toggleMode(LSMode.ADMIN_NOTIFY) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
		return true;
	}
}