package me.choco.locksecurity.api;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import me.choco.locksecurity.api.data.ILockSecurityPlayer;
import me.choco.locksecurity.api.registration.ILockedBlockManager;

public interface LockSecurity {
	
	/** 
	 * Get the main instance of the {@link ILockedBlockManager} class
	 * 
	 * @return the LockedBlockManager class
	 */
	public ILockedBlockManager getLockedBlockManager();
	
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
	 * @param player the player UUID to get an instance from
	 * @return the LSPlayer instance
	 */
	public ILockSecurityPlayer getPlayer(UUID player);
	
}