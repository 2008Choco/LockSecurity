package me.choco.locks.utils.general.loops;

import org.bukkit.scheduler.BukkitRunnable;

import me.choco.locks.LockSecurity;

public class DatabaseSaveLoop extends BukkitRunnable{
	
	LockSecurity plugin;
	public DatabaseSaveLoop(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	@Override
	public void run(){ plugin.getLocalizedData().saveLocalizedDataToDatabase(false); }
}