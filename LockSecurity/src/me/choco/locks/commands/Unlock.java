package me.choco.locks.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.PlayerUnlockBlockEvent;
import me.choco.locks.utils.LockStorageHandler;
import me.choco.locks.utils.LockedBlockAccessor;

public class Unlock implements CommandExecutor{
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	LockStorageHandler ram;
	public Unlock(LockSecurity plugin){
		this.plugin = plugin;
		lockedAccessor = new LockedBlockAccessor(plugin);
		ram = new LockStorageHandler(plugin);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (args.length == 0){
				if (player.hasPermission("locks.unlock.self")){
					if (!plugin.unlockMode.contains(player.getName())){
						plugin.unlockMode.add(player.getName());
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.UnlockModeEnabled"));
						return true;
					}else{
						plugin.unlockMode.remove(player.getName());
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
					
					Location lockLocation = ram.getLocationFromLockID(ID);
					if (ram.isStored(lockLocation)){
						PlayerUnlockBlockEvent unlockEvent = new PlayerUnlockBlockEvent(plugin, player, lockLocation.getBlock());
						Bukkit.getPluginManager().callEvent(unlockEvent);
						if (!unlockEvent.isCancelled()){
							lockedAccessor.setUnlocked(lockLocation.getBlock());
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.BlockUnlocked").replaceAll("%lockID%", String.valueOf(ID)));
							player.playSound(lockLocation, Sound.DOOR_OPEN, 1, 2);
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