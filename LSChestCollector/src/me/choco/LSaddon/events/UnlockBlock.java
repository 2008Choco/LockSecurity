package me.choco.LSaddon.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.choco.LSaddon.ChestCollector;
import me.choco.LSaddon.utils.CollectorBlock;
import me.choco.LSaddon.utils.CollectorHandler;
import me.choco.locksecurity.api.event.PlayerUnlockBlockEvent;

public class UnlockBlock implements Listener {
	
	private final CollectorHandler collectorHandler;
	
	public UnlockBlock(ChestCollector plugin) {
		this.collectorHandler = plugin.getCollectorHandler();
	}
	
	@EventHandler
	public void onBlockUnlock(PlayerUnlockBlockEvent event) {
		if (!collectorHandler.isCollector(event.getBlock())) return;
		
		CollectorBlock collector = collectorHandler.getCollector(event.getBlock());
		this.collectorHandler.unregisterCollector(collector);
	}
}