package me.choco.locks.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.LockedBlock;
import me.choco.locks.api.event.PlayerUnlockBlockEvent;
import me.choco.locks.api.utils.LSMode;

public class Unlock implements CommandExecutor{
	LockSecurity plugin;
	public Unlock(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (args.length == 0){
				if (player.hasPermission("locks.unlock.self")){
					if (!LSMode.getMode(player).equals(LSMode.UNLOCK)){
						LSMode.setMode(player, LSMode.UNLOCK);
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.UnlockModeEnabled"));
						return true;
					}else{
						LSMode.setMode(player, LSMode.DEFAULT);
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.UnlockModeDisabled"));
						return true;
					}
				}else{
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
					return true;
				}
			}
			if (args.length == 1){
				if (player.hasPermission("locks.unlock.id")){
					int ID = 0;
					try{
						ID = Integer.parseInt(args[0]);
					}catch(NumberFormatException e){
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.InvalidInteger").replaceAll("%param%", args[0]));
						return true;
					}
					
					if (plugin.getLocalizedData().isLockedBlock(ID)){
						LockedBlock block = plugin.getLocalizedData().getLockedBlock(ID);
						PlayerUnlockBlockEvent unlockEvent = new PlayerUnlockBlockEvent(player, block);
						Bukkit.getPluginManager().callEvent(unlockEvent);
						if (!unlockEvent.isCancelled()){
							plugin.getLocalizedData().unregisterLockedBlock(block);
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.BlockUnlocked").replaceAll("%lockID%", String.valueOf(ID)));
							player.playSound(block.getBlock().getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1, 2);
						}
					}else{
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.BlockNotLocked").replaceAll("%lockID%", String.valueOf(ID)));
					}
					return true;
				}else{
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.NoPermission"));
					return true;
				}
			}
		}else{
			plugin.sendPathMessage(sender, plugin.messages.getConfig().getString("Commands.General.OnlyPlayers"));
			return true;
		}
		return false;
	}
}