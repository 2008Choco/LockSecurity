package me.choco.locksecurity.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.KeyFactory;

public class GiveKeyCmd implements CommandExecutor {
	
	private final LockSecurity plugin;
	
	public GiveKeyCmd(LockSecurity plugin) {
		this.plugin = plugin;
	}
	
	/* Usage:
	 *   /givekey [player] [count]
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player target = (sender instanceof Player ? (Player) sender : null);
		
		if (!sender.hasPermission("locks.givekey")) {
			this.plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.nopermission"));
			return true;
		}
		
		int amount = 1;
		
		if (args.length >= 1) {
			target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				this.plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.playeroffline")
						.replace("%target%", args[0]));
				return true;
			}
			
			if (args.length >= 2) {
				try {
					amount = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					this.plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.invalidinteger")
							.replace("%param%", args[1]));
					return true;
				}
			}
		}
		
		if (target == null) {
			this.plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.specifyplayer"));
			return true;
		}
		
		boolean isSelf = target.equals(sender);
		if (!isSelf && args.length == 1)
			this.plugin.sendMessage(sender, plugin.getLocale().getMessage("command.givekey.sentkey")
					.replace("%target%", target.getName())
					.replace("%count%", String.valueOf(amount)));
		
		this.plugin.sendMessage(target, plugin.getLocale().getMessage(isSelf ? "command.givekey.receivedkey" : "command.givekey.receivedkey.target")
				.replace("%count%", String.valueOf(amount))
				.replace("%target%", sender.getName()));
		
		target.getInventory().addItem(KeyFactory.getUnsmithedKey(amount));
		return true;
	}
	
}