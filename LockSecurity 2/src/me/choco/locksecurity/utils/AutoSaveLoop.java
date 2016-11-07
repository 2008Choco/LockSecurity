package me.choco.locksecurity.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.registration.LockedBlockManager;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.json.JSONUtils;

public class AutoSaveLoop extends BukkitRunnable {
	
	boolean initialStart = true;
	
	private LockSecurity plugin;
	private PlayerRegistry playerRegistry;
	private LockedBlockManager lockedBlockManager;
	public AutoSaveLoop(LockSecurity plugin) {
		this.plugin = plugin;
		this.playerRegistry = plugin.getPlayerRegistry();
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@Override
	public void run() {
		if (initialStart){
			initialStart = false;
			return;
		}
		
		if (playerRegistry != null){
			for (LSPlayer player : playerRegistry.getPlayers().values())
				JSONUtils.writeJSON(player.getJSONDataFile(), player.write(new JSONObject()));
		}
		
		if (lockedBlockManager != null){
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(plugin.infoFile))){
				new PrintWriter(plugin.infoFile).close();
				
				String toWrite = "nextLockID=" + lockedBlockManager.getNextLockID() + "\n"
								+ "nextKeyID=" + lockedBlockManager.getNextKeyID();
				writer.write(toWrite);
			}catch(IOException e){ e.printStackTrace(); }
		}
	}
}