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

import me.choco.locks.LockSecurity;
import me.choco.locks.utils.LockedBlockAccessor;

public class JoinAndQuit implements Listener{
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	public JoinAndQuit(LockSecurity plugin){
		this.plugin = plugin;
		this.lockedAccessor = new LockedBlockAccessor(plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		boolean refactoredName = false;
		Player player = event.getPlayer();
		String oldName = player.getName();
		String currentName = player.getName();
		
		Connection connection = plugin.openConnection();
		Statement statement = plugin.createStatement(connection);
		ResultSet set = plugin.queryDatabase(statement, "select OwnerName from LockedBlocks where OwnerUUID = '" + player.getUniqueId().toString() + "'");
		
		try {
			if (set.next())
				oldName = set.getString("OwnerName");
		}catch (SQLException e){e.printStackTrace();}
		
		if (!(oldName.equals(currentName))){
			plugin.executeStatement(statement, "update LockedBlocks set OwnerName = '" + currentName + "' where OwnerUUID = '" + player.getUniqueId().toString() + "'");
			refactoredName = true;
		}
		if (refactoredName && plugin.getConfig().getBoolean("Aesthetics.DisplayNameChangeNotice"))
			plugin.getLogger().info("Successfully refactored SQLite Database tables for player " + currentName + " (Old name: " + oldName + ")");
		
		plugin.closeResultSet(set); plugin.closeStatement(statement); plugin.closeConnection(connection);
		
		// Set administrators into LockNotify mode
		if (player.hasPermission("locks.locknotify") && plugin.getConfig().getBoolean("EnableNotifyOnLogin"))
			plugin.adminNotify.add(player.getName());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		if (plugin.adminNotify.contains(event.getPlayer().getName())){plugin.adminNotify.remove(event.getPlayer().getName());}
	}
}