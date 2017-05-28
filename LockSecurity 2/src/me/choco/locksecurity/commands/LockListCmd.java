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
	
	private LockSecurity plugin;
	private PlayerRegistry playerRegistry;
	
	public LockListCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		OfflinePlayer target = (sender instanceof Player ? (Player) sender : null);
		
		if (args.length >= 1){
			target = Bukkit.getOfflinePlayer(args[0]);
			if (target == null){
				plugin.sendMessage(sender, args[0] + " is not currently online");
				return true;
			}
			
			if (!target.hasPlayedBefore()){
				plugin.sendMessage(sender, args[0] + " has never played on this server before");
				return true;
			}
		}
		
		if (target == null){
			plugin.sendMessage(sender, "The console does not have any blocks to list. Please specify a player");
			return true;
		}
		
		this.displayLockInformation(sender, playerRegistry.getPlayer(target));
		return true;
	}
	
	private void displayLockInformation(CommandSender sender, LSPlayer player){
		sender.sendMessage(ChatColor.YELLOW + "Lock list for all worlds (player %player%):");
		for (LockedBlock block : player.getOwnedBlocks()){
			Location location = block.getLocation();
			sender.sendMessage(ChatColor.YELLOW + "[ID: " + block.getLockID() + "] " + 
					location.getWorld().getName() + 
					" x:" + location.getBlockX() + 
					" y:" + location.getBlockY() + 
					" z:" + location.getBlockZ());
		}
	}
}