package me.choco.locksecurity.events.protection;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import me.choco.locksecurity.LockSecurityPlugin;
import me.choco.locksecurity.api.registration.ILockedBlockManager;

public class GriefProtectionListener implements Listener {
	
	private final LockSecurityPlugin plugin;
	private final ILockedBlockManager lockedBlockManager;
	
	public GriefProtectionListener(LockSecurityPlugin plugin) {
		this.plugin = plugin;
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onZombieBreakLockedDoor(EntityBreakDoorEvent event) {
		if (lockedBlockManager.isRegistered(event.getBlock())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if (lockedBlockManager.isRegistered(event.getBlock())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onDestroyBlockBeneathDoor(BlockBreakEvent event) {
		Block block = event.getBlock().getRelative(BlockFace.UP);
		if (lockedBlockManager.isRegistered(block)) {
			event.setCancelled(true);
			this.plugin.sendMessage(event.getPlayer(), plugin.getLocale().getMessage("event.lock.cannotbreak")
					.replace("%type%", block.getType().name())
					.replace("%player%", this.lockedBlockManager.getLockedBlock(block).getOwner().getPlayer().getName()));
		}
	}
	
	@EventHandler
	public void onRedstonePowerDoor(BlockRedstoneEvent event) {
		if (lockedBlockManager.isRegistered(event.getBlock())) event.setNewCurrent(0);
	}
	
	@EventHandler
	public void onHopperPullItem(InventoryMoveItemEvent event) {
		if (lockedBlockManager.isRegistered(event.getSource().getLocation().getBlock())) event.setCancelled(true);
	}
	
}