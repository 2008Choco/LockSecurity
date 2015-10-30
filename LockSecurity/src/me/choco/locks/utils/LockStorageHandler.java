package me.choco.locks.utils;

import org.bukkit.Location;

import me.choco.locks.LockSecurity;

public class LockStorageHandler {
	LockSecurity plugin;
	public LockStorageHandler(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	public void removeLock(Location location){
		plugin.lockedStringIDs.remove(location);
		plugin.lockedKeyIDs.remove(location);
	}
	
	public void addLockInformation(Location location, int id, int keyID){
		plugin.lockedStringIDs.put(location, id);
		plugin.lockedKeyIDs.put(location, keyID);
	}
	
	public int getLockID(Location location){
		return plugin.lockedStringIDs.get(location);
	}
	
	public int getKeyID(Location location){
		return plugin.lockedKeyIDs.get(location);
	}
	
	public Location getLocationFromKeyID(int keyID){
		for (Location location : plugin.lockedKeyIDs.keySet()){
			if (plugin.lockedKeyIDs.get(location) == keyID){
				return location;
			}
		}
		return null;
	}
	
	public Location getLocationFromLockID(int lockID){
		for (Location location : plugin.lockedKeyIDs.keySet()){
			if (plugin.lockedStringIDs.get(location) == lockID){
				return location;
			}
		}
		return null;
	}
	
	public boolean isStored(Location location){
		if (plugin.lockedStringIDs.containsKey(location))
			return true;
		return false;
	}
	
	public void clearLocks(){
		plugin.lockedStringIDs.clear();
		plugin.lockedKeyIDs.clear();
	}
}