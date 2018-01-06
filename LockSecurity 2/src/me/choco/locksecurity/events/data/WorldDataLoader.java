package me.choco.locksecurity.events.data;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.ILockedBlockManager;
import me.choco.locksecurity.api.IPlayerRegistry;

public class WorldDataLoader implements Listener {
	
	private final ILockedBlockManager manager;
	private final IPlayerRegistry playerRegistry;
	
	public WorldDataLoader(LockSecurity plugin) {
		this.manager = plugin.getLockedBlockManager();
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@EventHandler
	public void onLoadWorld(PlayerChangedWorldEvent event) {
		World world = event.getPlayer().getWorld();
		
		// The world is freshly loaded. Time to load data
		if (world.getPlayers().size() == 1) {
			manager.loadDataForWorld(world);
		}
	}
	
	@EventHandler
	public void onJoinAndLoadWorld(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!playerRegistry.hasJSONDataFile(player))
			this.playerRegistry.getPlayer(player);
		
		World world = player.getWorld();
		
		// The world is freshly loaded. Time to load data
		if (world.getPlayers().size() == 0) {
			manager.loadDataForWorld(world);
		}
	}
}