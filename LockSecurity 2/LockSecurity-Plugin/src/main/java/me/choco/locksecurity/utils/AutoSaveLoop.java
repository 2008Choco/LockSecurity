package me.choco.locksecurity.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

import org.bukkit.scheduler.BukkitRunnable;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.ILockSecurityPlayer;
import me.choco.locksecurity.api.ILockedBlockManager;
import me.choco.locksecurity.api.IPlayerRegistry;

public final class AutoSaveLoop extends BukkitRunnable {
	
	private static AutoSaveLoop instance;
	
	private final LockSecurity plugin;
	private final IPlayerRegistry playerRegistry;
	private final ILockedBlockManager lockedBlockManager;
	
	private AutoSaveLoop(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@Override
	public void run() {
		if (playerRegistry != null) {
			for (ILockSecurityPlayer player : playerRegistry.getPlayers()) {
				File dataFile = player.getJSONDataFile();
				
				try {
					dataFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				JSONUtils.writeJSON(player.getJSONDataFile(), player.write(new JsonObject()));
			}
		}
		
		if (lockedBlockManager != null) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(plugin.infoFile))) {
				new PrintWriter(plugin.infoFile).close();
				
				String toWrite = "nextLockID=" + lockedBlockManager.getNextLockID() + "\n"
								+ "nextKeyID=" + lockedBlockManager.getNextKeyID();
				writer.write(toWrite);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Start the auto save loop as an asynchronous task
	 * 
	 * @param plugin LockSecurity's plugin instance
	 * @param delayTicks the time interval between saves
	 * 
	 * @return the singleton instance of AutoSaveLoop
	 */
	public static AutoSaveLoop startLoop(LockSecurity plugin, int delayTicks) {
		if (instance != null) return instance;
		
		instance = new AutoSaveLoop(plugin);
		instance.runTaskTimerAsynchronously(plugin, delayTicks, delayTicks);
		return instance;
	}
	
}