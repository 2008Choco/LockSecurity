package wtf.choco.locksecurity.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;

import org.bukkit.OfflinePlayer;

public final class LockSecurityPlayerManager {

    private final Map<UUID, LockSecurityPlayer> players = new HashMap<>();

    public LockSecurityPlayer get(OfflinePlayer player) {
        Preconditions.checkArgument(player != null, "Cannot get LockSecurityPlayer wrapper for null player");
        return players.computeIfAbsent(player.getUniqueId(), LockSecurityPlayer::new);
    }

    public void clear() {
        this.players.clear();
    }

}
