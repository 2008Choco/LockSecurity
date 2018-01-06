package me.choco.locksecurity.events.data;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.ILockedBlockManager;

public class WorldDataUnloader implements Listener {
	
	private final ILockedBlockManager manager;
	
	public WorldDataUnloader(LockSecurity plugin) {
		this.manager = plugin.getLockedBlockManager();
	}
	
	@EventHandler
	public void onUnloadWorld(PlayerChangedWorldEvent event) {
		World world = event.getFrom();
		
		// Empty world, time to unload data
		if (world.getPlayers().size() == 0) {
			this.manager.unloadDataForWorld(world);
		}
	}
	
	@EventHandler
	public void onLeaveAndUnloadWorld(PlayerQuitEvent event) {
		World world = event.getPlayer().getWorld();
		
		// Empty world, time to unload data
		if (world.getPlayers().size() == 1) {
			this.manager.unloadDataForWorld(world);
		}
	}
	
}