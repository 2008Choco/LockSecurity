package me.choco.locksecurity.events.data;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.registration.LockedBlockManager;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.LSPlayer;

public class WorldDataLoader implements Listener {
	
	private LockedBlockManager manager;
	private PlayerRegistry playerRegistry;
	public WorldDataLoader(LockSecurity plugin) {
		this.manager = plugin.getLockedBlockManager();
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@EventHandler
	public void onLoadWorld(PlayerChangedWorldEvent event){
		World world = event.getPlayer().getWorld();
		
		// The world is freshly loaded. Time to load data
		if (world.getPlayers().size() == 1){
			manager.loadDataForWorld(world);
		}
	}
	
	@EventHandler
	public void onJoinAndLoadWorld(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if (!playerRegistry.hasJSONDataFile(player))
			playerRegistry.registerPlayer(new LSPlayer(player));
		
		World world = player.getWorld();
		
		// The world is freshly loaded. Time to load data
		if (world.getPlayers().size() == 0){
			manager.loadDataForWorld(world);
		}
		
		// TODO: TEST THIS. JOIN EVENTS MAY CONSIDER IT AS 0 PLAYERS RATHER THAN 1
	}
}