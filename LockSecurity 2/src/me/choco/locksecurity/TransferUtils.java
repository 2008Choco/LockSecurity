package me.choco.locksecurity;

public final class TransferUtils {
	
	protected static final void fromDatabase(LockSecurity plugin) {
		plugin.getLogger().info("Commencing transfer process for Data Support of LockSecurity 1.7.0 - 1.8.2");
		// TODO: Transfer data from "lockinfo.db" (SQLite)
	}
	
	protected static final void fromFile(LockSecurity plugin) {
		plugin.getLogger().info("Commencing transfer process for Data Support of LockSecurity 1.0.0 - 1.6.3");
		// TODO: Transfer data from "locked.yml" (ConfigurationFile)
	}
}