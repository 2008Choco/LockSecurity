package me.choco.locksecurity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.KeyFactory;
import me.choco.locksecurity.api.KeyFactory.KeyType;

public class ForgeKeyCmd implements CommandExecutor {
	
	private LockSecurity plugin;
	
	public ForgeKeyCmd(LockSecurity plugin) {
		this.plugin = plugin;
	}
	
	/* Usage:
	 *   /forgekey <id>,[id],[id]...
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)){
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.general.onlyplayers"));
			return true;
		}
		
		if (args.length == 0) {
			plugin.sendMessage(sender, plugin.getLocale().getMessage("command.forgekey.noid"));
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args.length >= 1){
			String[] stringIDs = args[0].split(",");
			int[] IDs = new int[stringIDs.length];
			
			for (int i = 0; i < stringIDs.length; i++){
				String ID = stringIDs[i];
				try{
					if (ID.equals("")) continue;
					IDs[i] = Integer.parseInt(ID);
				}catch(NumberFormatException e){
					plugin.sendMessage(player, plugin.getLocale().getMessage("command.general.invalidinteger")
							.replace("%param%", ID));
					return true;
				}
			}
			
			player.getInventory().addItem(KeyFactory.buildKey(KeyType.SMITHED).withIDs(IDs).build());
			plugin.sendMessage(player, plugin.getLocale().getMessage("command.forgekey.givenkey")
					.replace("ID", args[0]));
		}
		
		return true;
	}
}