package wtf.choco.locksecurity.api;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import wtf.choco.locksecurity.api.data.ILockSecurityPlayer;
import wtf.choco.locksecurity.api.registration.ILockedBlockManager;

public final class LockSecurityAPI {
	
	private static LockSecurity implementation;

	private LockSecurityAPI() { }
	
	public static void setImplementation(LockSecurity implementation) {
		if (LockSecurityAPI.implementation != null) {
			throw new InstantiationError("Cannot set implementation more than once");
		}
		
		LockSecurityAPI.implementation = implementation;
	}
	
	public static LockSecurity getImplementation() {
		return implementation;
	}
	
	public static ILockedBlockManager getLockedBlockManager() {
		return implementation.getLockedBlockManager();
	}
	
	public static ILockSecurityPlayer getPlayer(OfflinePlayer player) {
		return implementation.getPlayer(player);
	}
	
	public static ILockSecurityPlayer getPlayer(UUID player) {
		return implementation.getPlayer(player);
	}
	
}