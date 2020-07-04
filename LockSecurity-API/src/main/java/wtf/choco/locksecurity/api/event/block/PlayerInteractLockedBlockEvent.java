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

/**
 * Called when a player interacts with a locked block.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
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

    /**
     * Get the {@link ILockSecurityPlayer} wrapper for the player involved in this event.
     *
     * @return the player wrapper
     */
    @NotNull
    public ILockSecurityPlayer getPlayerWrapper() {
        return player;
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
     * Get the item used to interact with this block, if any.
     *
     * @return the item used. null if none
     */
    @Nullable
    public ItemStack getItem() {
        return (item != null ? item.clone() : null);
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
     * Get this event's action.
     *
     * @return the action
     */
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


    /**
     * The action that occurred in this event.
     *
     * @since 3.0.0
     * @author Parker Hawke - Choco
     */
    public static enum Action {

        /**
         * The player has successfully opened the block with a valid key.
         */
        OPEN_BLOCK,

        /**
         * The player has failed to open the block as they do not have a key in hand.
         */
        MISSING_KEY,

        /**
         * The player has failed to open the block as they do not have the correct key.
         */
        INCORRECT_KEY,

        /**
         * The player has inspected the block for more information.
         */
        INSPECT_BLOCK,

        /**
         * The player has cloned an unsmithed key into a smithed key for the clicked block.
         */
        CLONE_KEY;

    }

}
