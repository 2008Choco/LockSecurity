package wtf.choco.locksecurity.api.event.block;

import com.google.common.base.Preconditions;

import org.bukkit.GameMode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.player.LockSecurityPlayer;

public class PlayerBlockLockEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private ItemStack smithedKey;
    private boolean cancelled = false;
    private boolean consumeUnsmithedKey;

    private final LockSecurityPlayer playerWrapper;
    private final LockedBlock lockedBlock;
    private final ItemStack unsmithedKey;
    private final EquipmentSlot hand;

    public PlayerBlockLockEvent(LockSecurityPlayer player, LockedBlock lockedBlock, ItemStack unsmithedKey, ItemStack smithedKey, EquipmentSlot hand) {
        super(player.getBukkitPlayer().get());

        Preconditions.checkArgument(lockedBlock != null, "lockedBlock must not be null");
        Preconditions.checkArgument(unsmithedKey != null, "unsmithedKey must not be null");
        Preconditions.checkArgument(smithedKey != null, "key must not be null");
        Preconditions.checkArgument(hand != null, "hand must not be null");

        this.playerWrapper = player;
        this.lockedBlock = lockedBlock;
        this.unsmithedKey = unsmithedKey;
        this.smithedKey = smithedKey;
        this.hand = hand;

        this.consumeUnsmithedKey = (super.player.getGameMode() != GameMode.CREATIVE);
    }

    public LockSecurityPlayer getPlayerWrapper() {
        return playerWrapper;
    }

    public LockedBlock getLockedBlock() {
        return lockedBlock;
    }

    public ItemStack getUnsmithedKey() {
        return unsmithedKey.clone();
    }

    public void setSmithedKey(ItemStack key) {
        this.smithedKey = key;
    }

    public ItemStack getSmithedKey() {
        return smithedKey.clone();
    }

    public EquipmentSlot getHand() {
        return hand;
    }

    public void setConsumeUnsmithedKey(boolean consumeUnsmithedKey) {
        this.consumeUnsmithedKey = consumeUnsmithedKey;
    }

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
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
