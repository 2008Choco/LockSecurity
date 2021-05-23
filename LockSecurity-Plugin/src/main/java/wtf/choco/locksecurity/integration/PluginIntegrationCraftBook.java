package wtf.choco.locksecurity.integration;

import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.craftbook.mechanics.pipe.PipePutEvent;
import com.sk89q.craftbook.mechanics.pipe.PipeSuckEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import wtf.choco.commons.integration.PluginIntegration;
import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.block.LockedBlockManager;

public final class PluginIntegrationCraftBook implements PluginIntegration, Listener {

    private final CraftBookPlugin craftBookPlugin;

    public PluginIntegrationCraftBook(Plugin plugin) {
        this.craftBookPlugin = (CraftBookPlugin) plugin;
    }

    @Override
    public Plugin getIntegratedPlugin() {
        return craftBookPlugin;
    }

    @Override
    public void load() { }

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, LockSecurity.getInstance());
    }

    @Override
    public void disable() {}

    @EventHandler
    private void onPipePull(PipeSuckEvent event) {
        LockedBlockManager lockedBlockManager = LockSecurity.getInstance().getLockedBlockManager();
        if (lockedBlockManager.isLocked(event.getSuckedBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPipePut(PipePutEvent event) {
        LockedBlockManager lockedBlockManager = LockSecurity.getInstance().getLockedBlockManager();
        if (lockedBlockManager.isLocked(event.getPuttingBlock())) {
            event.setCancelled(true);
        }
    }

}
