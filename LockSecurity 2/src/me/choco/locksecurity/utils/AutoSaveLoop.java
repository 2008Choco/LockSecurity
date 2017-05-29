package me.choco.locksecurity.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

import org.bukkit.scheduler.BukkitRunnable;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.registration.LockedBlockManager;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.json.JSONUtils;

public class AutoSaveLoop extends BukkitRunnable {
	
	private final LockSecurity plugin;
	private final PlayerRegistry playerRegistry;
	private final LockedBlockManager lockedBlockManager;
	
	public AutoSaveLoop(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@Override
	public void run() {
		if (playerRegistry != null) {
			for (LSPlayer player : playerRegistry.getPlayers().values())
				JSONUtils.writeJSON(player.getJSONDataFile(), player.write(new JsonObject()));
		}
		
		if (lockedBlockManager != null) {
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(plugin.infoFile))) {
				new PrintWriter(plugin.infoFile).close();
				
				String toWrite = "nextLockID=" + lockedBlockManager.getNextLockID() + "\n"
								+ "nextKeyID=" + lockedBlockManager.getNextKeyID();
				writer.write(toWrite);
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
}