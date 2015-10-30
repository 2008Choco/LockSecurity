package me.choco.locks.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.choco.locks.LockSecurity;
import me.choco.locks.utils.LockedBlockAccessor;

public class LoginNameCheck implements Listener{
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	public LoginNameCheck(LockSecurity plugin){
		this.plugin = plugin;
		this.lockedAccessor = new LockedBlockAccessor(plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		boolean refactoredName = false;
		int changes = 0;
		Player player = event.getPlayer(); 
		String oldName = "";
		String currentName = player.getName();
		for (int id : lockedAccessor.getAllLocks(player)){
			oldName = plugin.locked.getConfig().getString(id + ".PlayerName");
			if (!oldName.equals(currentName)){
				plugin.locked.getConfig().set(id + ".PlayerName", currentName);
				plugin.locked.saveConfig();
				plugin.locked.reloadConfig();
				refactoredName = true;
				changes++;
			}
		}
		if (refactoredName && plugin.getConfig().getBoolean("Aesthetics.DisplayNameChangeNotice")){
			plugin.getLogger().info("Player " + currentName + " refactored. Name has changed. (Old name: " + oldName + "). " + changes + " indexes changed");
		}
	}
}