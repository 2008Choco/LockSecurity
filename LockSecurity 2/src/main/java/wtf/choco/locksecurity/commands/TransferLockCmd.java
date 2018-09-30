package wtf.choco.locksecurity.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.api.LSMode;
import wtf.choco.locksecurity.data.LockSecurityPlayer;
import wtf.choco.locksecurity.registration.PlayerRegistry;
import wtf.choco.locksecurity.utils.localization.Locale;

public class TransferLockCmd implements CommandExecutor {
	
	private final LockSecurity plugin;
	private final PlayerRegistry playerRegistry;
	
	public TransferLockCmd(LockSecurity plugin) {
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
		
		if (!sender.hasPermission("locks.transferlock")) {
			locale.sendMessage(sender, "command.general.nopermission");
			return true;
		}
		
		if (args.length == 0) {
			locale.sendMessage(sender, "command.general.specifyplayer");
			return true;
		}
		
		Player player = (Player) sender;
		LockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		@SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if (!target.hasPlayedBefore()) {
			locale.getMessage(sender, "command.general.neverplayed")
				.param("%target%", args[0]).send();
			return true;
		}
		
		lsPlayer.setTransferTarget(playerRegistry.getPlayer(target));
		locale.sendMessage(player, lsPlayer.toggleMode(LSMode.TRANSFER_LOCK) ? "command.transferlock.enabled" : "command.transferlock.disabled");
		return true;
	}
	
}