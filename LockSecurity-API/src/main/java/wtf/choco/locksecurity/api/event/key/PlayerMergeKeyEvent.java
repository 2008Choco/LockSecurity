package wtf.choco.locksecurity.api.event.key;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerMergeKeyEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancelled = false;

    private ItemStack output;
    private final ItemStack firstKey, secondKey;

    public PlayerMergeKeyEvent(@NotNull Player who, @NotNull ItemStack firstKey, @NotNull ItemStack secondKey, @Nullable ItemStack output) {
        super(who);

        this.firstKey = firstKey;
        this.secondKey = secondKey;
        this.output = output;
    }

    @NotNull
    public ItemStack getFirstKey() {
        return firstKey.clone();
    }

    @NotNull
    public ItemStack getSecondKey() {
        return secondKey.clone();
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
