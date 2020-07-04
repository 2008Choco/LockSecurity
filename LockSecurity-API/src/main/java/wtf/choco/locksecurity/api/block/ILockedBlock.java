package wtf.choco.locksecurity.api.block;

import java.time.ZonedDateTime;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;

public interface ILockedBlock {

    @NotNull
    public Block getBlock();

    @NotNull
    public Material getType();

    @NotNull
    public Location getLocation();

    @NotNull
    public World getWorld();

    public int getX();

    public int getY();

    public int getZ();

    public boolean isOwner(@Nullable OfflinePlayer player);

    public boolean isOwner(@Nullable ILockSecurityPlayer player);

    @NotNull
    public ZonedDateTime getLockTime();

    public void setNickname(@Nullable String nickname);

    @Nullable
    public String getNickname();

    public boolean hasNickname();

    public boolean isValidKey(@Nullable ItemStack key);

}
