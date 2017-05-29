package me.choco.locksecurity.registration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.utils.LSPlayer;

/** 
 * A registry to keep track of players and their information. Any player that
 * has every joined the server should be registered in this registry upon joining.
 * 
 * @author Parker Hawke - 2008Choco
 */
public class PlayerRegistry {
	
	private final Map<OfflinePlayer, LSPlayer> players = new HashMap<>();
	
	private LockSecurity plugin;
	
	public PlayerRegistry(LockSecurity plugin) {
		this.plugin = plugin;
	}
	
	/** 
	 * Register a player to the player registry. If the player has never been registered 
	 * before, a new player file will be created for them
	 * 
	 * @param player the player to register
	 */
	public void registerPlayer(OfflinePlayer player){
		if (players.containsKey(player)) return;
		
		LSPlayer lsPlayer = new LSPlayer(player);
		players.put(player, lsPlayer);
	}
	
	/** 
	 * Register an LSPlayer to the player registry
	 * 
	 * @param player the player to register
	 */
	public void registerPlayer(LSPlayer player){
		this.players.put(player.getPlayer(), player);
	}
	
	/** 
	 * Unregister a player from the player registry (if existant)
	 * 
	 * @param player the player to unregister
	 */
	public void unregisterPlayer(OfflinePlayer player){
		if (!players.containsKey(player)) return;
		players.remove(player);
	}
	
	/** 
	 * Unregister an LSPlayer from the player registry (if existant)
	 * 
	 * @param player the player to unregister
	 */
	public void unregisterPlayer(LSPlayer player){
		unregisterPlayer(player.getPlayer());
	}
	
	/** 
	 * Check if a player is currently registered in the registry or not
	 * 
	 * @param player the player to check
	 * @return true if the player is registered
	 */
	public boolean isRegistered(OfflinePlayer player){
		return players.containsKey(player);
	}
	
	/** 
	 * Get an LSPlayer instance from the specified player object
	 * 
	 * @param player the player to get an instance from
	 * @return the LSPlayer instance. null if not registered
	 */
	public LSPlayer getPlayer(OfflinePlayer player){
		return players.get(player);
	}
	
	/**
	 * Get an LSPlayer instance from the specified player UUID
	 * 
	 * @param uuid the UUID to get an instace from
	 * @return the LSPlayer instance. null if not registered
	 */
	public LSPlayer getPlayer(UUID uuid) {
		return this.getPlayer(Bukkit.getOfflinePlayer(uuid));
	}
	
	/** 
	 * Get all players in the specified mode
	 * 
	 * @param mode the mode to reference
	 * @return a set of all players in the specified mode
	 */
	public Set<LSPlayer> getPlayersInMode(LSMode mode){
		Set<LSPlayer> players = new HashSet<>();
		for (LSPlayer player : this.players.values())
			if (player.getActiveModes().contains(mode)) players.add(player);
		return players;
	}
	
	/** 
	 * Check if a player has a JSON data file or not. All players that have ever
	 * been registered should have a JSON data file, otherwise, they are new players
	 * and have not yet been registered. Though this is rare
	 * 
	 * @param player the player to check
	 * @return true if the player has a JSON data file
	 */
	public boolean hasJSONDataFile(OfflinePlayer player){
		if (isRegistered(player)) return true;
		return new File(plugin.playerdataDir + File.separator + player.getUniqueId().toString() + ".json").exists();
	}
	
	/** 
	 * Get the registry Map for this class containing every single registered player. 
	 * 
	 * @return a map of all registered players
	 */
	public Map<OfflinePlayer, LSPlayer> getPlayers() {
		return players;
	}
}