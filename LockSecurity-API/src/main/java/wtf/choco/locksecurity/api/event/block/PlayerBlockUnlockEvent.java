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

/**
 * Called when a player unlocks a block.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
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

    /**
     * Get the {@link ILockSecurityPlayer} wrapper for the player involved in this event.
     *
     * @return the player wrapper
     */
    @NotNull
    public ILockSecurityPlayer getPlayerWrapper() {
        return playerWrapper;
    }

    /**
     * Get the {@link ILockedBlock} involved in this event.
     *
     * @return the locked block
     */
    @NotNull
    public ILockedBlock getLockedBlock() {
        return lockedBlock;
    }

    /**
     * Get the unsmithed key used to unlock the block. Changes made to this item will not be
     * reflected in the player's inventory.
     *
     * @return the unsmithed key
     */
    @NotNull
    public ItemStack getKey() {
        return key.clone();
    }

    /**
     * Get the hand used in this event.
     *
     * @return the hand
     */
    @NotNull
    public EquipmentSlot getHand() {
        return hand;
    }

    /**
     * Check whether or not the player is only requesting to unlock the block. If a request
     * is being made, the block will not be unlocked after this event has succeeded... another
     * event will be called at a later time confirming the unlock.
     *
     * @return true if a request, false otherwise
     */
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
