package wtf.choco.locksecurity.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wtf.choco.locksecurity.LockSecurityPlugin;
import wtf.choco.locksecurity.api.utils.KeyFactory;
import wtf.choco.locksecurity.utils.localization.Locale;

public class GiveKeyCmd implements CommandExecutor {
	
	private final LockSecurityPlugin plugin;
	
	public GiveKeyCmd(LockSecurityPlugin plugin) {
		this.plugin = plugin;
	}
	
	/* Usage:
	 *   /givekey [player] [count]
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player target = (sender instanceof Player ? (Player) sender : null);
		Locale locale = plugin.getLocale();
		
		if (!sender.hasPermission("locks.givekey")) {
			locale.sendMessage(sender, "command.general.nopermission");
			return true;
		}
		
		int amount = 1;
		
		if (args.length >= 1) {
			target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				locale.getMessage(sender, "command.general.playeroffline")
					.param("%target%", args[0]).send();
				return true;
			}
			
			if (args.length >= 2) {
				try {
					amount = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					locale.getMessage(sender, "command.general.invalidinteger")
						.param("%param%", args[1]).send();
					return true;
				}
			}
		}
		
		if (target == null) {
			locale.sendMessage(sender, "command.general.specifyplayer");
			return true;
		}
		
		boolean isSelf = target.equals(sender);
		if (!isSelf && args.length == 1) {
			locale.getMessage(sender, "command.givekey.sentkey")
				.param("%target%", target.getName())
				.param("%count%", amount).send();
		}
		
		target.getInventory().addItem(KeyFactory.getUnsmithedKey(amount));
		locale.getMessage(target, isSelf ? "command.givekey.receivedkey" : "command.givekey.receivedkey.target")
			.param("%count%", amount)
			.param("%target%", sender.getName()).send();
		return true;
	}
	
}