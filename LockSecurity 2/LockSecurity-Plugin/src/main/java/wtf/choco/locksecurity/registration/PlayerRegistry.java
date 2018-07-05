package wtf.choco.locksecurity.registration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;

import wtf.choco.locksecurity.LockSecurityPlugin;
import wtf.choco.locksecurity.api.data.ILockSecurityPlayer;
import wtf.choco.locksecurity.api.utils.LSMode;
import wtf.choco.locksecurity.data.LockSecurityPlayer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class PlayerRegistry {
	
	private final Map<UUID, ILockSecurityPlayer> players = new HashMap<>();
	private final LockSecurityPlugin plugin;
	
	/**
	 * Construct a new PlayerRegistry. There should be need for one 1 manager
	 * 
	 * @param plugin the LockSecurity plugin
	 */
	public PlayerRegistry(LockSecurityPlugin plugin) {
		this.plugin = plugin;
	}
	
	public ILockSecurityPlayer getPlayer(OfflinePlayer player) {
		return getPlayer(player.getUniqueId());
	}
	
	public ILockSecurityPlayer getPlayer(UUID player) {
		return players.computeIfAbsent(player, LockSecurityPlayer::new);
	}
	
	public void registerPlayer(LockSecurityPlayer player) {
		Preconditions.checkArgument(player != null, "Registered players must not be null");
		this.players.put(player.getUniqueId(), player);
	}
	
	public void unregisterPlayer(LockSecurityPlayer player) {
		this.unregisterPlayer(player.getUniqueId());
	}
	
	public void unregisterPlayer(OfflinePlayer player) {
		this.unregisterPlayer(player.getUniqueId());
	}
	
	public void unregisterPlayer(UUID uuid) {
		this.players.remove(uuid);
	}
	
	public List<ILockSecurityPlayer> getPlayers() {
		return ImmutableList.copyOf(players.values());
	}
	
	public List<ILockSecurityPlayer> getPlayers(LSMode mode) {
		return this.players.values().stream()
				.filter(p -> p.isModeEnabled(mode))
				.collect(Collectors.toList());
	}
	
	public boolean hasJSONDataFile(OfflinePlayer player) {
		return hasJSONDataFile(player.getUniqueId());
	}
	
	public boolean hasJSONDataFile(UUID player) {
		Preconditions.checkArgument(player != null, "JSON data files do not exist for null players");
		return new File(plugin.playerdataDir, player + ".json").exists();
	}
	
	public void clearRegistry() {
		this.players.clear();
	}
	
}