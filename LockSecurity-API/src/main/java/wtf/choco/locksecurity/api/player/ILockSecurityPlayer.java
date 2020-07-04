package wtf.choco.locksecurity.api.player;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILockSecurityPlayer {

    @NotNull
    public Optional<Player> getBukkitPlayer();

    @NotNull
    public OfflinePlayer getBukkitPlayerOffline();

    @NotNull
    public UUID getUniqueId();

    public boolean is(@Nullable OfflinePlayer player);

    public void setIgnoringLocks(boolean ignoringLocks);

    public boolean isIgnoringLocks();

    public void setLockNotifications(boolean notifications);

    public boolean hasLockNotifications();

}
