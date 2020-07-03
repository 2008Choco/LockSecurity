package wtf.choco.locksecurity.api.event.block;

import com.google.common.base.Preconditions;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.player.LockSecurityPlayer;

public class PlayerInteractLockedBlockEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;

    private final LockSecurityPlayer player;
    private final LockedBlock lockedBlock;
    private final ItemStack item;
    private final EquipmentSlot hand;
    private final Action action;

    public PlayerInteractLockedBlockEvent(LockSecurityPlayer player, LockedBlock lockedBlock, ItemStack item, EquipmentSlot hand, Action action) {
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

    public LockSecurityPlayer getPlayerWrapper() {
        return player;
    }

    public LockedBlock getLockedBlock() {
        return lockedBlock;
    }

    public ItemStack getItem() {
        return (item != null ? item.clone() : null);
    }

    public EquipmentSlot getHand() {
        return hand;
    }

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
    public HandlerList getHandlers() {
        return HANDLERS;
    }

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
