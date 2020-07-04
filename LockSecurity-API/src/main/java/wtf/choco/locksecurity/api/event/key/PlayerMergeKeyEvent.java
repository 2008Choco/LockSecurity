package wtf.choco.locksecurity.api.event.key;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player merges two smithed keys in a crafting table.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
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

    /**
     * Get the first smithed key in the crafting inventory.
     *
     * @return the first smithed key
     */
    @NotNull
    public ItemStack getFirstKey() {
        return firstKey.clone();
    }

    /**
     * Get the second smithed key in the crafting inventory.
     *
     * @return the second smithed key
     */
    @NotNull
    public ItemStack getSecondKey() {
        return secondKey.clone();
    }

    /**
     * Set the output of this duplication.
     *
     * @param output the output
     */
    public void setOutput(@Nullable ItemStack output) {
        this.output = output;
    }

    /**
     * Get the output of this duplication.
     *
     * @return the output
     */
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
