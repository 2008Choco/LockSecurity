package me.choco.locks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locks.LockSecurity;
import me.choco.locks.utils.Keys;

public class ForgeKey implements CommandExecutor{
	
	private LockSecurity plugin;
	private Keys keys;
	public ForgeKey(LockSecurity plugin){
		this.plugin = plugin;
		this.keys = plugin.getKeyManager();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (player.hasPermission("locks.forgekey")){
				if (args.length == 0){
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.ForgeKey.NoIDSpecified"));
					return true;
				}
				if (args.length == 1){
					int ID = 0;
					try{
						ID = Integer.parseInt(args[0]);
					}catch(NumberFormatException e){
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.General.InvalidInteger").replaceAll("%param%", args[0]));
						return true;
					}
					
					player.getInventory().addItem(keys.createLockedKey(1, ID));
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.ForgeKey.GivenKey").replaceAll("%ID%", String.valueOf(ID)));
					return true;
				}
			}
		}else{
			plugin.sendPathMessage(sender, "Commands.General.OnlyPlayers");
			return true;
		}
		return false;
	}
}