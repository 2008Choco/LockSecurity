package me.choco.locksecurity.events.data;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.registration.LockedBlockManager;

public class WorldDataUnloader implements Listener {
	
	private LockedBlockManager manager;
	public WorldDataUnloader(LockSecurity plugin) {
		this.manager = plugin.getLockedBlockManager();
	}
	
	@EventHandler
	public void onUnloadWorld(PlayerChangedWorldEvent event){
		World world = event.getFrom();
		
		// Empty world, time to unload data
		if (world.getPlayers().size() == 0){
			manager.unloadDataForWorld(world);
		}
	}
	
	@EventHandler
	public void onLeaveAndUnloadWorld(PlayerQuitEvent event){
		World world = event.getPlayer().getWorld();
		
		// Empty world, time to unload data
		if (world.getPlayers().size() == 1){
			manager.unloadDataForWorld(world);
		}
		
		// TODO: TEST THIS. LEAVE EVENTS MAY CONSIDER IT AS 1 PLAYER RATHER THAN 0
	}
}