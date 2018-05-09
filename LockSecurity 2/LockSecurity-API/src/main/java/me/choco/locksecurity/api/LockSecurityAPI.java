package me.choco.locksecurity.api;

import me.choco.locksecurity.api.registration.ILockedBlockManager;
import me.choco.locksecurity.api.registration.IPlayerRegistry;

public class LockSecurityAPI {
	
	private static LockSecurity implementation;
	
	public static void setImplementation(LockSecurity implementation) {
		if (LockSecurityAPI.implementation != null) {
			throw new InstantiationError("Cannot set implementation more than once");
		}
		
		LockSecurityAPI.implementation = implementation;
	}
	
	public static LockSecurity getImplementation() {
		return implementation;
	}
	
	public static IPlayerRegistry getPlayerRegistry() {
		return implementation.getPlayerRegistry();
	}
	
	public static ILockedBlockManager getLockedBlockManager() {
		return implementation.getLockedBlockManager();
	}
	
}