package me.choco.LSaddon.events;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.choco.LSaddon.ChestCollector;
import me.choco.LSaddon.utils.CollectorHandler;
import me.choco.locksecurity.api.event.PlayerUnlockBlockEvent;

public class UnlockBlock implements Listener{
	
	private CollectorHandler collectorHandler;
	public UnlockBlock(ChestCollector plugin){
		this.collectorHandler = plugin.getCollectorHandler();
	}
	
	@EventHandler
	public void onBlockUnlock(PlayerUnlockBlockEvent event){
		Block block = event.getBlock().getBlock();
		if (!collectorHandler.isCollector(block)) return;
		
		collectorHandler.removeCollector(block);
	}
}