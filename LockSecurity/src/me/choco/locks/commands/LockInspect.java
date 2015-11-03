package me.choco.locks.commands;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;
import me.choco.locks.utils.LockStorageHandler;
import me.choco.locks.utils.LockedBlockAccessor;

public class LockInspect implements CommandExecutor{
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	LockStorageHandler ram;
	public LockInspect(LockSecurity plugin){
		this.plugin = plugin;
		this.lockedAccessor = new LockedBlockAccessor(plugin);
		this.ram = new LockStorageHandler(plugin);
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (args.length == 0){
				if (player.hasPermission("locks.lockinspect")){
					if (plugin.inspectLockMode.contains(player.getName())){
						plugin.inspectLockMode.remove(player.getName());
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockInspect.LockInspectDisabled"));
					}else{
						plugin.inspectLockMode.add(player.getName());
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
					
					if (ram.isStored(ram.getLocationFromLockID(ID))){
						Block block = ram.getLocationFromLockID(ID).getBlock();
						player.sendMessage(ChatColor.GOLD + "- - - - - - " + ChatColor.DARK_AQUA + "Lock information " + ChatColor.GOLD + "- - - - - -");
						player.sendMessage(ChatColor.GOLD + "Lock ID: " + ChatColor.AQUA + lockedAccessor.getBlockLockID(block));
						player.sendMessage(ChatColor.GOLD + "Key ID: " + ChatColor.AQUA + lockedAccessor.getBlockKeyID(block));
						player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + lockedAccessor.getBlockOwner(block) + " (" + ChatColor.GOLD + lockedAccessor.getBlockOwnerUUID(block) + ChatColor.AQUA + ")");
						player.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.AQUA + block.getLocation().getWorld().getName() + " x:" + block.getLocation().getBlockX() + " y:" + block.getLocation().getBlockY() + " z:" + block.getLocation().getBlockZ());
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