package me.choco.LSaddon.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import me.choco.LSaddon.ChestCollector;
import me.choco.locksecurity.api.LockedBlock;

public class CollectorHandler {
	
	private int nextCollectorId;
	private final List<CollectorBlock> collectors = new ArrayList<>();
	
	public CollectorHandler(ChestCollector plugin) {
		this.nextCollectorId = plugin.collectorsFile.getConfig().getInt("NextCollectorID", 1);
	}
	
	public void registerCollector(CollectorBlock collector) {
		this.collectors.add(collector);
	}
	
	public void unregisterCollector(CollectorBlock collector) {
		this.collectors.remove(collector);
	}
	
	public boolean isCollector(LockedBlock block) {
		return this.collectors.stream().anyMatch(c -> c.getBlock().equals(block));
	}
	
	public boolean isCollector(Location location) {
		return this.collectors.stream().anyMatch(c -> c.getBlock().getLocation().equals(location));
	}
	
	public boolean isCollector(Block block) {
		return isCollector(block.getLocation());
	}
	
	public CollectorBlock getCollector(int id) {
		return this.collectors.stream()
			.filter(c -> c.getId() == id)
			.findFirst().orElse(null);
	}
	
	public CollectorBlock getCollector(LockedBlock block) {
		return this.collectors.stream()
			.filter(c -> c.getBlock().equals(block))
			.findFirst().orElse(null);
	}
	
	public CollectorBlock getCollector(Location location) {
		return this.collectors.stream()
			.filter(c -> c.getBlock().getLocation().equals(location))
			.findFirst().orElse(null);
	}
	
	public CollectorBlock getCollector(Block block) {
		return this.getCollector(block.getLocation());
	}
	
	public int getNextCollectorID(boolean increment) {
		return increment ? nextCollectorId++ : nextCollectorId;
	}
	
	public int getNextCollectorID() {
		return getNextCollectorID(false);
	}
	
	public List<CollectorBlock> getCollectors(OfflinePlayer player) {
		return collectors.stream()
				.filter(c -> c.getBlock().getOwner().getPlayer().getUniqueId().equals(player.getUniqueId()))
				.collect(Collectors.toList());
	}
	
	public List<CollectorBlock> getCollectors() {
		return ImmutableList.copyOf(collectors);
	}
	
	public void clearCollectors() {
		this.collectors.clear();
	}
	
}