package wtf.choco.locksecurity.api.event.key;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerResetKeyEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancelled = false;

    private ItemStack output;
    private final ItemStack key;

    public PlayerResetKeyEvent(Player who, ItemStack key, ItemStack output) {
        super(who);

        this.key = key;
        this.output = output;
    }

    public ItemStack getKey() {
        return key.clone();
    }

    public void setOutput(ItemStack output) {
        this.output = output;
    }

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
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
