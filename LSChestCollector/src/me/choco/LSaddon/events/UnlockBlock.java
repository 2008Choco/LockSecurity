package me.choco.LSaddon.events;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.choco.LSaddon.ChestCollector;
import me.choco.LSaddon.utils.CollectorHandler;
import me.choco.locks.api.PlayerUnlockBlockEvent;

public class UnlockBlock implements Listener{
	ChestCollector plugin;
	CollectorHandler collectorHandler;
	public UnlockBlock(ChestCollector plugin){
		this.plugin = plugin;
		this.collectorHandler = new CollectorHandler(plugin);
	}
	
	@EventHandler
	public void onBlockUnlock(PlayerUnlockBlockEvent event){
		Block block = event.getBlock();
		if (collectorHandler.isCollector(block)){
			collectorHandler.removeCollector(block);
		}
	}
}