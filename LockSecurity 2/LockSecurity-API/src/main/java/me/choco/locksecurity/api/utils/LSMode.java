package me.choco.locksecurity.api.utils;

import me.choco.locksecurity.api.data.ILockSecurityPlayer;

/** 
 * Different modes which may be applied to a {@link ILockSecurityPlayer}
 * 
 * @author Parker Hawke - 2008Choco
 */
public enum LSMode {
	
	/** 
	 * The mode to ignore the key requirements on a block 
	 */
	IGNORE_LOCKS("Ignore Locks"),
	
	/** 
	 * The mode to retrieve lock information by right clicking on a block 
	 */
	LOCK_INSPECT("Lock Inspect"),
	
	/** 
	 * The mode to unlock a block by right clicking on it
	 */
	UNLOCK("Unlock"),
	
	/** 
	 * The mode to transfer a locked block from one player to another
	 */
	TRANSFER_LOCK("Transfer Lock"),
	
	/** 
	 * The mode to receive notifications for blocks locked throughout the world
	 */
	ADMIN_NOTIFY("Admin Notify");
	
	private final String name;
	
	private LSMode(String name) {
		this.name = name;
	}
	
	/** 
	 * Get the name of the mode (Utilized in data handling and message processing)
	 * 
	 * @return the name of the mode
	 */
	public String getName() {
		return name;
	}
	
	/** 
	 * Get an LSMode by its name
	 * 
	 * @param name the name of the mode
	 * @return The mode with the specified name. Null if not found
	 */
	public static LSMode getByName(String name) {
		for (LSMode mode : values())
			if (mode.getName().equals(name)) return mode;
		return null;
	}
}