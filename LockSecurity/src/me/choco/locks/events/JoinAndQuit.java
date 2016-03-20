package me.choco.locks.events;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.choco.locks.LockSecurity;

public class JoinAndQuit implements Listener{
	LockSecurity plugin;
	public JoinAndQuit(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent event){
		final Player player = event.getPlayer();
		
		//Update player's name in database asynchronously if it was changed
		new BukkitRunnable(){
			public void run(){
				boolean refactoredName = false;
				String oldName = player.getName();
				String currentName = player.getName();
				
				Connection connection = plugin.getLSDatabase().openConnection();
				Statement statement = plugin.getLSDatabase().createStatement(connection);
				ResultSet set = plugin.getLSDatabase().queryDatabase(statement, "select OwnerName from LockedBlocks where OwnerUUID = '" + player.getUniqueId().toString() + "'");
				
				try {
					if (set.next())
						oldName = set.getString("OwnerName");
				}catch (SQLException e){e.printStackTrace();}
				
				if (!(oldName.equals(currentName))){
					plugin.getLSDatabase().executeStatement(statement, "update LockedBlocks set OwnerName = '" + currentName + "' where OwnerUUID = '" + player.getUniqueId().toString() + "'");
					refactoredName = true;
				}
				if (refactoredName && plugin.getConfig().getBoolean("Aesthetics.DisplayNameChangeNotice"))
					plugin.getLogger().info("Successfully refactored SQLite Database tables for player " + currentName + " (Old name: " + oldName + ")");
				
				plugin.getLSDatabase().closeResultSet(set); plugin.getLSDatabase().closeStatement(statement); plugin.getLSDatabase().closeConnection(connection);
			}
		}.runTaskAsynchronously(plugin);
		
		// Set administrators into LockNotify mode
		if (player.hasPermission("locks.locknotify") && plugin.getConfig().getBoolean("EnableNotifyOnLogin"))
			plugin.adminNotify.add(player.getName());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		if (plugin.adminNotify.contains(event.getPlayer().getName())){plugin.adminNotify.remove(event.getPlayer().getName());}
	}
}