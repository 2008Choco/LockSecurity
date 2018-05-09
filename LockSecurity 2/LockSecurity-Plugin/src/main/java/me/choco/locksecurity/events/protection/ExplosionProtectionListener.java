package me.choco.locksecurity.events.protection;

import java.util.Iterator;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.choco.locksecurity.LockSecurityPlugin;
import me.choco.locksecurity.api.registration.ILockedBlockManager;

public class ExplosionProtectionListener implements Listener {
	
	private final LockSecurityPlugin plugin;
	private final ILockedBlockManager lockedBlockManager;
	
	public ExplosionProtectionListener(LockSecurityPlugin plugin) {
		this.plugin = plugin;
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onExplodeLockedBlock(EntityExplodeEvent event) {
		if (!plugin.getConfig().getBoolean("Griefing.PreventLockedExplosions")) return;
		
		Iterator<Block> it = event.blockList().iterator();
		while (it.hasNext())
			if (lockedBlockManager.isRegistered(it.next())) it.remove();
	}
	
}