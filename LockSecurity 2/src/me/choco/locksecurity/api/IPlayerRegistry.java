package me.choco.locksecurity.api;

import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.choco.locksecurity.api.utils.LSMode;

/** 
 * A registry to map Bukkit {@link Player}s to their {@link ILockSecurityPlayer} data objects
 * 
 * @author Parker Hawke - 2008Choco
 */
public interface IPlayerRegistry {
	
	/**
	 * Get an LSPlayer instance from the specified offline player. If no player has been
	 * mapped to the one provided, a new instance will be created, mapped and returned
	 * 
	 * @param player the player to get an instance from
	 * @return the LSPlayer instance
	 */
	public ILockSecurityPlayer getPlayer(OfflinePlayer player);
	
	/**
	 * Get an LSPlayer instance from the specified player UUID. If no player has been
	 * mapped to the provided UUID, a new instance will be created, mapped and returned
	 * 
	 * @param uuid the player UUID to get an instance from
	 * @return the LSPlayer instance
	 */
	public ILockSecurityPlayer getPlayer(UUID player);
	
	/**
	 * Register an existing ILockSecurityPlayer instance to the registry
	 * 
	 * @param player the player to register
	 */
	public void registerPlayer(ILockSecurityPlayer player);
	
	/** 
	 * Unregister a player from the player registry (if registered). Note that this
	 * will clear any existing data from the player without saving it
	 * 
	 * @param uuid the uuid to unregister
	 */
	public void unregisterPlayer(ILockSecurityPlayer player);
	
	/** 
	 * Unregister a player from the player registry (if registered). Note that this
	 * will clear any existing data from the player without saving it
	 * 
	 * @param uuid the uuid to unregister
	 */
	public void unregisterPlayer(OfflinePlayer player);
	
	/** 
	 * Unregister a player UUID from the player registry (if registered). Note that this
	 * will clear any existing data from the player without saving it
	 * 
	 * @param uuid the uuid to unregister
	 */
	public void unregisterPlayer(UUID player);
	
	/**
	 * Get an immutable list of all players in this registry
	 * 
	 * @return all registered players
	 */
	public List<ILockSecurityPlayer> getPlayers();
	
	/**
	 * Get an immutable list of all players in this registry with the provided mode enabled
	 * 
	 * @param mode the mode to check for
	 * @return a list of all registered players with the mode enabled
	 */
	public List<ILockSecurityPlayer> getPlayers(LSMode mode);
	
	/** 
	 * Check if a player has a JSON data file or not. All players that have ever
	 * been registered should have a JSON data file, otherwise, they are new players
	 * and have not yet been registered. Though this is rare
	 * 
	 * @param player the player to check
	 * @return true if the player has a JSON data file
	 */
	public boolean hasJSONDataFile(OfflinePlayer player);
	
	/** 
	 * Check if a player has a JSON data file or not. All players that have ever
	 * been registered should have a JSON data file, otherwise, they are new players
	 * and have not yet been registered. Though this is rare
	 * 
	 * @param player the player to check
	 * @return true if the player has a JSON data file
	 */
	public boolean hasJSONDataFile(UUID player);
	
	/**
	 * Clear all players from the registry
	 */
	public void clearRegistry();
	
}