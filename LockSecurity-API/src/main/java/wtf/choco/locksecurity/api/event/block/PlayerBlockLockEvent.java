package wtf.choco.locksecurity.api.event.block;

import com.google.common.base.Preconditions;

import org.bukkit.GameMode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import wtf.choco.locksecurity.api.block.ILockedBlock;
import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;

/**
 * Called when a player locks a block.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
public class PlayerBlockLockEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private ItemStack smithedKey;
    private boolean cancelled = false;
    private boolean consumeUnsmithedKey;

    private final ILockSecurityPlayer playerWrapper;
    private final ILockedBlock lockedBlock;
    private final ItemStack unsmithedKey;
    private final EquipmentSlot hand;

    public PlayerBlockLockEvent(@NotNull ILockSecurityPlayer player, @NotNull ILockedBlock lockedBlock, @NotNull ItemStack unsmithedKey, @NotNull ItemStack smithedKey, @NotNull EquipmentSlot hand) {
        super(player.getBukkitPlayer().get());

        Preconditions.checkArgument(lockedBlock != null, "lockedBlock must not be null");
        Preconditions.checkArgument(unsmithedKey != null, "unsmithedKey must not be null");
        Preconditions.checkArgument(smithedKey != null, "key must not be null");
        Preconditions.checkArgument(hand != null, "hand must not be null");

        this.playerWrapper = player;
        this.lockedBlock = lockedBlock;
        this.unsmithedKey = unsmithedKey.clone();
        this.smithedKey = smithedKey.clone();
        this.hand = hand;

        this.consumeUnsmithedKey = (super.player.getGameMode() != GameMode.CREATIVE);
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
     * Get the {@link ILockedBlock} involved in this event. This block has not yet been
     * registered by the time this event has been called, therefore a reference should not
     * be held if the event has been cancelled.
     *
     * @return the locked block
     */
    @NotNull
    public ILockedBlock getLockedBlock() {
        return lockedBlock;
    }

    /**
     * Get the unsmithed key used to lock the block. Changes made to this item will not be
     * reflected in the player's inventory.
     *
     * @return the unsmithed key
     */
    @NotNull
    public ItemStack getUnsmithedKey() {
        return unsmithedKey.clone();
    }

    /**
     * Set the smithed key to be returned to the player once the event has succeeded.
     *
     * @param key the key to give
     */
    public void setSmithedKey(@Nullable ItemStack key) {
        this.smithedKey = (key != null ? key.clone() : null);
    }

    /**
     * Get the smithed key to be returned to the player once the event has succeeded.
     *
     * @return the smithed key
     */
    @Nullable
    public ItemStack getSmithedKey() {
        return (smithedKey != null ? smithedKey.clone() : null);
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
     * Set whether or not to consumed the unsmithed key.
     *
     * @param consumeUnsmithedKey whether or not to consume the unsmithed key
     */
    public void setConsumeUnsmithedKey(boolean consumeUnsmithedKey) {
        this.consumeUnsmithedKey = consumeUnsmithedKey;
    }

    /**
     * Check whether or not the unsmithed key should be consumed. For creative players, this
     * value will already be set to false.
     *
     * @return whether or not to consume the unsmithed key
     */
    public boolean shouldConsumeUnsmithedKey() {
        return consumeUnsmithedKey;
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
