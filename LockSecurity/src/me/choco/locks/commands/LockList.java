package me.choco.locks.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.LockedBlock;

public class LockList implements CommandExecutor{
	LockSecurity plugin;
	public LockList(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (cmd.getName().equals("locklist")){
			if (sender instanceof Player){
				Player player = (Player) sender;
				if (player.hasPermission("locks.locklist")){
					displayLockInformation(player, plugin.getLocalizedData().getAllLocks(player), player.getName());
				}else{
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
				}
			}else{
				plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.PlayersOnly"));
			}
			return true;
		}
		
		if (cmd.getName().equals("locklistother")){
			if (args.length == 0){
				plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.MustSpecifyPlayer"));
			}
			else if (args.length >= 1){
				if (sender.hasPermission("locks.locklistother")){
					OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
					displayLockInformation(sender, plugin.getLocalizedData().getAllLocks(target), target.getName());
				}else{
					plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
				}
			}
			return true;
		}
		return false;
	}
	
	private void displayLockInformation(CommandSender sender, List<LockedBlock> blocks, String targetName){
		sender.sendMessage(ChatColor.YELLOW + plugin.messages.getConfig().getString("Commands.LockList.ListIdentifier").replace("%player%", targetName));
		for (LockedBlock block : blocks){
			Location location = block.getBlock().getLocation();
			sender.sendMessage(formatListing(block.getLockId(), location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
		}
	}
	
	private String formatListing(int id, World world, double x, double y, double z){
		return (ChatColor.YELLOW + "[ID: " + id + "] " + world.getName().toString() + " x:" + (int)x + " y:" + (int)y + " z:" + (int)z);
	}
}