package me.choco.locksecurity.registration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import org.bukkit.OfflinePlayer;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.ILockSecurityPlayer;
import me.choco.locksecurity.api.IPlayerRegistry;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.utils.LockSecurityPlayer;

public class PlayerRegistry implements IPlayerRegistry {
	
	private final Map<UUID, ILockSecurityPlayer> players = new HashMap<>();
	private final LockSecurity plugin;
	
	/**
	 * Construct a new PlayerRegistry. There should be need for one 1 manager
	 * 
	 * @param plugin the LockSecurity plugin
	 */
	public PlayerRegistry(LockSecurity plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public ILockSecurityPlayer getPlayer(OfflinePlayer player) {
		return getPlayer(player.getUniqueId());
	}
	
	@Override
	public ILockSecurityPlayer getPlayer(UUID player) {
		return players.computeIfAbsent(player, LockSecurityPlayer::new);
	}
	
	@Override
	public void registerPlayer(ILockSecurityPlayer player) {
		this.players.put(player.getUniqueId(), player);
	}
	
	@Override
	public void unregisterPlayer(ILockSecurityPlayer player) {
		this.unregisterPlayer(player.getUniqueId());
	}
	
	@Override
	public void unregisterPlayer(OfflinePlayer player) {
		this.unregisterPlayer(player.getUniqueId());
	}
	
	@Override
	public void unregisterPlayer(UUID uuid) {
		this.players.remove(uuid);
	}
	
	@Override
	public List<ILockSecurityPlayer> getPlayers() {
		return ImmutableList.copyOf(players.values());
	}
	
	@Override
	public List<ILockSecurityPlayer> getPlayers(LSMode mode) {
		return this.players.values().stream()
				.filter(p -> p.isModeEnabled(mode))
				.collect(Collectors.toList());
	}
	
	@Override
	public boolean hasJSONDataFile(OfflinePlayer player) {
		return hasJSONDataFile(player.getUniqueId());
	}
	
	@Override
	public boolean hasJSONDataFile(UUID player) {
		return new File(plugin.playerdataDir, player + ".json").exists();
	}
	
	@Override
	public void clearRegistry() {
		this.players.clear();
	}
	
}