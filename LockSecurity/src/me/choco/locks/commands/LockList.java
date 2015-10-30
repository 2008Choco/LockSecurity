package me.choco.locks.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;
import me.choco.locks.utils.LockedBlockAccessor;

public class LockList implements CommandExecutor{
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	public LockList(LockSecurity plugin){
		this.plugin = plugin;
		this.lockedAccessor = new LockedBlockAccessor(plugin);
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (cmd.getName().equals("locklist")){
			if (sender instanceof Player){
				Player player = (Player) sender;
				if (player.hasPermission("locks.locklist")){
					gatherLockInformation(player, lockedAccessor.getAllLocks(player), player.getName());
				}else{
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
				}
			}else{
				plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.PlayersOnly"));
			}
			return true;
		}
		
		if (cmd.getName().equals("locklistother")){
			if (sender instanceof Player){
				Player player = (Player) sender;
				if (args.length == 0){
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.MustSpecifyPlayer"));
					return true;
				}
				if (args.length >= 1){
					if (player.hasPermission("locks.locklistother")){
						if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0]))){
							Player target = Bukkit.getPlayer(args[0]);
							gatherLockInformation(player, lockedAccessor.getAllLocks(target), target.getName());
						}else{
							OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
							gatherLockInformation(player, lockedAccessor.getAllLocks(target), target.getName());
						}
					}else{
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
					}
					return true;
				}
			}else{
				if (args.length == 0){
					plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.MustSpecifyPlayer"));
				}
				if (args.length >= 1){
					if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0]))){
						Player target = Bukkit.getPlayer(args[0]);
						gatherLockInformation(sender, lockedAccessor.getAllLocks(target), target.getName());
					}else{
						OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
						gatherLockInformation(sender, lockedAccessor.getAllLocks(target), target.getName());
					}
					return true;
				}
			}
		}
		return false;
	}
	
	private void gatherLockInformation(CommandSender sender, ArrayList<Integer> ids, String targetName){
		sender.sendMessage(ChatColor.YELLOW + plugin.messages.getConfig().getString("Commands.LockList.ListIdentifier").replaceAll("%player%", targetName));
		for (int id : ids){
			World world = Bukkit.getServer().getWorld(plugin.locked.getConfig().getString(id + ".Location.World"));
			double x = plugin.locked.getConfig().getDouble(id + ".Location.X");
			double y = plugin.locked.getConfig().getDouble(id + ".Location.Y");
			double z = plugin.locked.getConfig().getDouble(id + ".Location.Z");
			
			sender.sendMessage(formatListing(id, world, x, y, z));
		}
	}
	
	private String formatListing(int id, World world, double x, double y, double z){
		return (ChatColor.YELLOW + "[ID: " + id + "] " + world.getName().toString() + " x:" + (int)x + " y:" + (int)y + " z:" + (int)z);
	}
}