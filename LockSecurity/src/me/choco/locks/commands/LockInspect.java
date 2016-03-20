package me.choco.locks.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.LockedBlock;
import me.choco.locks.api.utils.LSMode;

public class LockInspect implements CommandExecutor{
	LockSecurity plugin;
	public LockInspect(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (args.length == 0){
				if (player.hasPermission("locks.lockinspect")){
					if (LSMode.getMode(player).equals(LSMode.INSPECT_LOCKS)){
						LSMode.setMode(player, LSMode.DEFAULT);
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockInspect.LockInspectDisabled"));
					}else{
						LSMode.setMode(player, LSMode.INSPECT_LOCKS);
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockInspect.LockInspectEnabled"));
					}
				}else{
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
				}
			}
			if (args.length == 1){
				if (player.hasPermission("locks.lockinspectother")){
					int ID = 0;
					try{
						ID = Integer.parseInt(args[0]);
					}catch(NumberFormatException e){
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.InvalidInteger").replaceAll("%param%", args[0]));
						return true;
					}
					
					if (plugin.getLocalizedData().isLockedBlock(ID)){
						LockedBlock block = plugin.getLocalizedData().getLockedBlock(ID);
						player.sendMessage(ChatColor.GOLD + "- - - - - - " + ChatColor.DARK_AQUA + "Lock information " + ChatColor.GOLD + "- - - - - -");
						player.sendMessage(ChatColor.GOLD + "Lock ID: " + ChatColor.AQUA + block.getLockId());
						player.sendMessage(ChatColor.GOLD + "Key ID: " + ChatColor.AQUA + block.getKeyId());
						player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + block.getOwner().getName() + " (" + ChatColor.GOLD + block.getOwner().getUniqueId() + ChatColor.AQUA + ")");
						player.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.AQUA + block.getBlock().getWorld().getName() + " x:" + block.getBlock().getX() + " y:" + block.getBlock().getY() + " z:" + block.getBlock().getZ());
					}else{
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.BlockNotLocked").replaceAll("%lockID%", String.valueOf(ID)));
					}
					return true;
				}else{
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
				}
			}
		}else{
			plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.OnlyPlayers"));
		}
		return true;
	}
}