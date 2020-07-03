package wtf.choco.locksecurity.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.api.event.block.PlayerBlockLockEvent;
import wtf.choco.locksecurity.api.event.block.PlayerBlockUnlockEvent;
import wtf.choco.locksecurity.api.event.block.PlayerInteractLockedBlockEvent;
import wtf.choco.locksecurity.api.event.key.PlayerDuplicateKeyEvent;
import wtf.choco.locksecurity.api.event.key.PlayerMergeKeyEvent;
import wtf.choco.locksecurity.api.event.key.PlayerResetKeyEvent;
import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.player.LockSecurityPlayer;

public final class LSEventFactory {

    private LSEventFactory() { }

    public static PlayerBlockLockEvent callPlayerBlockLockEvent(LockSecurityPlayer player, LockedBlock lockedBlock, ItemStack unsmithedKey, ItemStack key, EquipmentSlot hand) {
        PlayerBlockLockEvent event = new PlayerBlockLockEvent(player, lockedBlock, unsmithedKey, key, hand);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static boolean handlePlayerBlockUnlockEvent(LockSecurityPlayer player, LockedBlock lockedBlock, ItemStack key, EquipmentSlot hand, boolean request) {
        PlayerBlockUnlockEvent event = new PlayerBlockUnlockEvent(player, lockedBlock, key, hand, request);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }

    public static boolean handlePlayerInteractLockedBlockEvent(LockSecurityPlayer player, LockedBlock lockedBlock, ItemStack item, EquipmentSlot hand, PlayerInteractLockedBlockEvent.Action action) {
        PlayerInteractLockedBlockEvent event = new PlayerInteractLockedBlockEvent(player, lockedBlock, item, hand, action);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }

    public static PlayerDuplicateKeyEvent callPlayerDuplicateKeyEvent(Player player, ItemStack smithedKey, ItemStack unsmithedKey, ItemStack output) {
        PlayerDuplicateKeyEvent event = new PlayerDuplicateKeyEvent(player, smithedKey, unsmithedKey, output);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static PlayerMergeKeyEvent callPlayerMergeKeyEvent(Player player, ItemStack firstKey, ItemStack secondKey, ItemStack output) {
        PlayerMergeKeyEvent event = new PlayerMergeKeyEvent(player, firstKey, secondKey, output);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static PlayerResetKeyEvent callPlayerResetKeyEvent(Player player, ItemStack key, ItemStack output) {
        PlayerResetKeyEvent event = new PlayerResetKeyEvent(player, key, output);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

}
