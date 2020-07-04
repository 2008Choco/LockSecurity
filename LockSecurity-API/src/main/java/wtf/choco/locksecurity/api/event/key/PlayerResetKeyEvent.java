package wtf.choco.locksecurity.api.event.key;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player resets a key in a crafting inventory.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
public class PlayerResetKeyEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancelled = false;

    private ItemStack output;
    private final ItemStack key;

    public PlayerResetKeyEvent(@NotNull Player who, @NotNull ItemStack key, @Nullable ItemStack output) {
        super(who);

        this.key = key;
        this.output = output;
    }

    /**
     * Get the smithed key to be reset.
     *
     * @return the smithed key
     */
    @NotNull
    public ItemStack getKey() {
        return key.clone();
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
