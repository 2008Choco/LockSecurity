package wtf.choco.locksecurity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.api.LSMode;
import wtf.choco.locksecurity.data.LockSecurityPlayer;
import wtf.choco.locksecurity.registration.PlayerRegistry;
import wtf.choco.locksecurity.utils.localization.Locale;

public class IgnoreLocksCmd implements CommandExecutor {
	
	private final LockSecurity plugin;
	private final PlayerRegistry playerRegistry;
	
	public IgnoreLocksCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Locale locale = plugin.getLocale();
		if (!(sender instanceof Player)) {
			locale.sendMessage(sender, "command.general.onlyplayers");
			return true;
		}
		
		if (!sender.hasPermission("locks.ignorelocks")) {
			locale.sendMessage(sender, "command.genreal.nopermission");
			return true;
		}
		
		Player player = (Player) sender;
		LockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		locale.sendMessage(sender, lsPlayer.toggleMode(LSMode.IGNORE_LOCKS) ? "command.ignorelocks.enabled" : "command.ignorelocks.disabled");
		return true;
	}
	
}