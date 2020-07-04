package wtf.choco.locksecurity.api.event.block;

import com.google.common.base.Preconditions;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import wtf.choco.locksecurity.api.block.ILockedBlock;
import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;

public class PlayerInteractLockedBlockEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;

    private final ILockSecurityPlayer player;
    private final ILockedBlock lockedBlock;
    private final ItemStack item;
    private final EquipmentSlot hand;
    private final Action action;

    public PlayerInteractLockedBlockEvent(@NotNull ILockSecurityPlayer player, @NotNull ILockedBlock lockedBlock, @Nullable ItemStack item, @NotNull EquipmentSlot hand, @NotNull Action action) {
        super(player.getBukkitPlayer().get());

        Preconditions.checkArgument(lockedBlock != null, "lockedBlock must not be null");
        Preconditions.checkArgument(hand != null, "hand must not be null");
        Preconditions.checkArgument(action != null, "action must not be null");

        this.player = player;
        this.lockedBlock = lockedBlock;
        this.item = (item != null) ? item.clone() : null;
        this.hand = hand;
        this.action = action;
    }

    @NotNull
    public ILockSecurityPlayer getPlayerWrapper() {
        return player;
    }

    @NotNull
    public ILockedBlock getLockedBlock() {
        return lockedBlock;
    }

    @Nullable
    public ItemStack getItem() {
        return (item != null ? item.clone() : null);
    }

    @NotNull
    public EquipmentSlot getHand() {
        return hand;
    }

    @NotNull
    public Action getAction() {
        return action;
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
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public static enum Action {

        OPEN_BLOCK,
        MISSING_KEY,
        INCORRECT_KEY,
        INSPECT_BLOCK,
        CLONE_KEY;

    }

}
