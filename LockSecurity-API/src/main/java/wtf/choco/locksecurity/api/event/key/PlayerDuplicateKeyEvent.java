package wtf.choco.locksecurity.api.event.key;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDuplicateKeyEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancelled = false;

    private ItemStack output;
    private final ItemStack smithedKey, unsmithedKey;

    public PlayerDuplicateKeyEvent(@NotNull Player who, @NotNull ItemStack smithedKey, @NotNull ItemStack unsmithedKey, @Nullable ItemStack output) {
        super(who);

        this.smithedKey = smithedKey;
        this.unsmithedKey = unsmithedKey;
        this.output = output;
    }

    @NotNull
    public ItemStack getSmithedKey() {
        return smithedKey.clone();
    }

    @NotNull
    public ItemStack getUnsmithedKey() {
        return unsmithedKey.clone();
    }

    public void setOutput(@Nullable ItemStack output) {
        this.output = output;
    }

    @Nullable
    public ItemStack getOutput() {
        return output;
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
