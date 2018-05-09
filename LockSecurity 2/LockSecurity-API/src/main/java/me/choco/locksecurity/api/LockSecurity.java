package me.choco.locksecurity.api;

import me.choco.locksecurity.api.registration.ILockedBlockManager;
import me.choco.locksecurity.api.registration.IPlayerRegistry;

public interface LockSecurity {
	
	/** 
	 * Get the main instance of the {@link IPlayerRegistry} class
	 * 
	 * @return the PlayerRegistry class
	 */
	public IPlayerRegistry getPlayerRegistry();
	
	/** 
	 * Get the main instance of the {@link ILockedBlockManager} class
	 * 
	 * @return the LockedBlockManager class
	 */
	public ILockedBlockManager getLockedBlockManager();
	
}