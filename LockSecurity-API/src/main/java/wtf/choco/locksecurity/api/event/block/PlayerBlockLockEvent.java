package wtf.choco.locksecurity.api.event.block;

import com.google.common.base.Preconditions;

import org.bukkit.GameMode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import wtf.choco.locksecurity.api.block.ILockedBlock;
import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;

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

    @NotNull
    public ILockSecurityPlayer getPlayerWrapper() {
        return playerWrapper;
    }

    @NotNull
    public ILockedBlock getLockedBlock() {
        return lockedBlock;
    }

    @NotNull
    public ItemStack getUnsmithedKey() {
        return unsmithedKey.clone();
    }

    @NotNull
    public void setSmithedKey(ItemStack key) {
        this.smithedKey = key.clone();
    }

    @NotNull
    public ItemStack getSmithedKey() {
        return smithedKey.clone();
    }

    @NotNull
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
    @NotNull
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
