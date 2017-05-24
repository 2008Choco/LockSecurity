package me.choco.locksecurity.utils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.registration.LockedBlockManager;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.LSPlayer;

public class LockInspectCmd implements CommandExecutor {
	
	private LockSecurity plugin;
	private PlayerRegistry playerRegistry;
	private LockedBlockManager lockedBlockManager;
	
	public LockInspectCmd(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)){
			plugin.sendMessage(sender, "The console is not permitted to inspect locks");
			return true;
		}
		
		Player player = (Player) sender;
		LSPlayer lsPlayer = playerRegistry.getPlayer(player);
		
		if (args.length >= 1){
			int lockID = -1;
			try{
				lockID = Integer.parseInt(args[0]);
			}catch(NumberFormatException e){
				plugin.sendMessage(player, "Invalid Lock ID provided, \"" + args[0] + "\"");
			}
			
			LockedBlock lBlock = lockedBlockManager.getLockedBlock(lockID);
			
			if (lBlock == null){
				plugin.sendMessage(player, "No block is currently locked with the Lock ID " + lockID);
				return true;
			}
			
			lBlock.displayInformation(player);
			return true;
		}
		
		plugin.sendMessage(player, LSMode.LOCK_INSPECT.getName() + " mode " + 
				(lsPlayer.toggleMode(LSMode.LOCK_INSPECT) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
		return true;
	}
}