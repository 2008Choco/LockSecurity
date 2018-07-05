package wtf.choco.locksecurity.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wtf.choco.locksecurity.LockSecurityPlugin;
import wtf.choco.locksecurity.api.utils.KeyFactory;
import wtf.choco.locksecurity.api.utils.KeyFactory.KeyType;
import wtf.choco.locksecurity.utils.localization.Locale;

public class ForgeKeyCmd implements CommandExecutor {
	
	private final LockSecurityPlugin plugin;
	
	public ForgeKeyCmd(LockSecurityPlugin plugin) {
		this.plugin = plugin;
	}
	
	/* Usage:
	 *   /forgekey <id>,[id],[id]...
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Locale locale = plugin.getLocale();
		if (!(sender instanceof Player)) {
			locale.sendMessage(sender, "command.general.onlyplayers");
			return true;
		}
		
		if (!sender.hasPermission("locks.forgekey")) {
			locale.sendMessage(sender, "command.general.nopermission");
			return true;
		}
		
		if (args.length == 0) {
			locale.sendMessage(sender, "command.forgekey.noid");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args.length >= 1) {
			String[] stringIDs = args[0].split(",");
			int[] IDs = new int[stringIDs.length];
			
			for (int i = 0; i < stringIDs.length; i++) {
				String ID = stringIDs[i];
				try {
					if (ID.equals("")) continue;
					IDs[i] = Integer.parseInt(ID);
				} catch (NumberFormatException e) {
					locale.getMessage(player, "command.general.invalidinteger")
						.param("%param%", ID).send();
					return true;
				}
			}
			
			player.getInventory().addItem(KeyFactory.buildKey(KeyType.SMITHED).withIDs(IDs).build());
			locale.getMessage(player, "command.forgekey.givenkey")
				.param("%ID%", args[0]).send();
		}
		
		return true;
	}
	
}