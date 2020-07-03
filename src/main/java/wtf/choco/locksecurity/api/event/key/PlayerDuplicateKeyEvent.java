package wtf.choco.locksecurity.api.event.key;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDuplicateKeyEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancelled = false;

    private ItemStack output;
    private final ItemStack smithedKey, unsmithedKey;

    public PlayerDuplicateKeyEvent(Player who, ItemStack smithedKey, ItemStack unsmithedKey, ItemStack output) {
        super(who);

        this.smithedKey = smithedKey;
        this.unsmithedKey = unsmithedKey;
        this.output = output;
    }

    public ItemStack getSmithedKey() {
        return smithedKey.clone();
    }

    public ItemStack getUnsmithedKey() {
        return unsmithedKey.clone();
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
