package me.choco.locksecurity.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.ILockSecurityPlayer;
import me.choco.locksecurity.api.IPlayerRegistry;
import me.choco.locksecurity.api.utils.LSMode;

public class TransferLockCmd implements CommandExecutor {
	
	private final LockSecurity plugin;
	private final IPlayerRegistry playerRegistry;
	
	public TransferLockCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			this.plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.onlyplayers"));
			return true;
		}
		
		if (!sender.hasPermission("locks.transferlock")) {
			this.plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.nopermission"));
			return true;
		}
		
		if (args.length == 0) {
			this.plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.specifyplayer"));
			return true;
		}
		
		Player player = (Player) sender;
		ILockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		@SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if (!target.hasPlayedBefore()) {
			this.plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.neverplayed")
					.replace("%target%", args[0]));
			return true;
		}
		
		lsPlayer.setTransferTarget(playerRegistry.getPlayer(target));
		
		this.plugin.sendMessage(player, plugin.getLocale().getMessage(lsPlayer.toggleMode(LSMode.TRANSFER_LOCK)
				? "command.transferlock.enabled"
				: "command.transferlock.disabled"));
		return true;
	}
	
}