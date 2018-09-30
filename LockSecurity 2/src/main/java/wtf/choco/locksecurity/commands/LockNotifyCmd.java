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

public class LockNotifyCmd implements CommandExecutor {
	
	private final LockSecurity plugin;
	private final PlayerRegistry playerRegistry;
	
	public LockNotifyCmd(LockSecurity plugin) {
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
		
		if (!sender.hasPermission("locks.locknotify")) {
			locale.sendMessage(sender, "command.general.nopermission");
			return true;
		}
		
		Player player = (Player) sender;
		LockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		locale.sendMessage(player, lsPlayer.toggleMode(LSMode.ADMIN_NOTIFY) ? "command.locknotify.enabled" : "command.locknotify.disabled");
		return true;
	}
	
}