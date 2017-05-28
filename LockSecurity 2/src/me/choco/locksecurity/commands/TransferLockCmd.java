package me.choco.locksecurity.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.LSPlayer;

public class TransferLockCmd implements CommandExecutor {
	
	private LockSecurity plugin;
	private PlayerRegistry playerRegistry;
	
	public TransferLockCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)){
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.onlyplayers"));
			return true;
		}
		
		if (args.length == 0){
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.specifyplayer"));
			return true;
		}
		
		Player player = (Player) sender;
		LSPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		@SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if (!target.hasPlayedBefore()){
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.neverplayed")
					.replace("%target%", args[0]));
			return true;
		}
		
		lsPlayer.setToTransferTo(playerRegistry.getPlayer(target));

		plugin.sendMessage(player, plugin.getLocale().getMessage(lsPlayer.toggleMode(LSMode.TRANSFER_LOCK)
				? "command.transferlock.enabled"
				: "command.transferlock.disabled"));
		return true;
	}
}