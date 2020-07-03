package wtf.choco.locksecurity.api.event.block;

import com.google.common.base.Preconditions;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.player.LockSecurityPlayer;

public class PlayerBlockUnlockEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancelled = false;

    private final LockSecurityPlayer playerWrapper;
    private final LockedBlock lockedBlock;
    private final ItemStack key;
    private final EquipmentSlot hand;
    private final boolean request;

    public PlayerBlockUnlockEvent(LockSecurityPlayer player, LockedBlock lockedBlock, ItemStack key, EquipmentSlot hand, boolean request) {
        super(player.getBukkitPlayer().get());

        Preconditions.checkArgument(lockedBlock != null, "lockedBlock must not be null");
        Preconditions.checkArgument(key != null, "key must not be null");
        Preconditions.checkArgument(hand != null, "hand must not be null");

        this.playerWrapper = player;
        this.lockedBlock = lockedBlock;
        this.key = key;
        this.hand = hand;
        this.request = request;
    }

    public LockSecurityPlayer getPlayerWrapper() {
        return playerWrapper;
    }

    public LockedBlock getLockedBlock() {
        return lockedBlock;
    }

    public ItemStack getKey() {
        return key.clone();
    }

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
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
