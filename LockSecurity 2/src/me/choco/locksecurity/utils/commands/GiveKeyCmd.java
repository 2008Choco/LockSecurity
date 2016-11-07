package me.choco.locksecurity.utils.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.KeyFactory;

public class GiveKeyCmd implements CommandExecutor {
	
	private LockSecurity plugin;
	public GiveKeyCmd(LockSecurity plugin) {
		this.plugin = plugin;
	}
	
	/* Usage:
	 *   /givekey [player] [count]
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player target = (sender instanceof Player ? (Player) sender : null);
		int amount = 1;
		
		if (args.length >= 1){
			target = Bukkit.getPlayer(args[0]);
			if (target == null){
				plugin.sendMessage(sender, args[0] + " is not currently online");
				return true;
			}
			
			if (args.length >= 2){
				try{
					amount = Integer.parseInt(args[1]);
				}catch(NumberFormatException e){
					plugin.sendMessage(sender, "The amount of keys must be an integer value");
					return true;
				}
			}
		}
		
		if (target == null){
			plugin.sendMessage(sender, "The console cannot receive keys. Please specify a player");
			return true;
		}
		
		boolean isSelf = target.equals(sender), plural = (amount > 1);
		if (!isSelf && args.length == 1)
			plugin.sendMessage(sender, "You have given " + target.getName() + " " + amount + " unsmithed key" + (plural ? "s" : ""));
		plugin.sendMessage(target, "You have received " + amount + " unsmithed key" + (plural ? "s" : "") + (!isSelf ? " from " + sender.getName() : ""));
		
		target.getInventory().addItem(KeyFactory.getUnsmithedKey(amount));
		return true;
	}
}