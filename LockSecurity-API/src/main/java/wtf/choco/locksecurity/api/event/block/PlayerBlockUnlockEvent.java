package wtf.choco.locksecurity.api.event.block;

import com.google.common.base.Preconditions;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import wtf.choco.locksecurity.api.block.ILockedBlock;
import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;

public class PlayerBlockUnlockEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancelled = false;

    private final ILockSecurityPlayer playerWrapper;
    private final ILockedBlock lockedBlock;
    private final ItemStack key;
    private final EquipmentSlot hand;
    private final boolean request;

    public PlayerBlockUnlockEvent(@NotNull ILockSecurityPlayer player, @NotNull ILockedBlock lockedBlock, @NotNull ItemStack key, @NotNull EquipmentSlot hand, boolean request) {
        super(player.getBukkitPlayer().get());

        Preconditions.checkArgument(lockedBlock != null, "lockedBlock must not be null");
        Preconditions.checkArgument(key != null, "key must not be null");
        Preconditions.checkArgument(hand != null, "hand must not be null");

        this.playerWrapper = player;
        this.lockedBlock = lockedBlock;
        this.key = key.clone();
        this.hand = hand;
        this.request = request;
    }

    @NotNull
    public ILockSecurityPlayer getPlayerWrapper() {
        return playerWrapper;
    }

    @NotNull
    public ILockedBlock getLockedBlock() {
        return lockedBlock;
    }

    @NotNull
    public ItemStack getKey() {
        return key.clone();
    }

    @NotNull
    public EquipmentSlot getHand() {
        return hand;
    }

    public boolean isRequest() {
        return request;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
