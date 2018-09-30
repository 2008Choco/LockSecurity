package wtf.choco.locksecurity.events.protection;

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

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.registration.LockedBlockManager;

public class GriefProtectionListener implements Listener {
	
	private final LockSecurity plugin;
	private final LockedBlockManager lockedBlockManager;
	
	public GriefProtectionListener(LockSecurity plugin) {
		this.plugin = plugin;
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onZombieBreakLockedDoor(EntityBreakDoorEvent event) {
		if (lockedBlockManager.isLockedBlock(event.getBlock())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if (lockedBlockManager.isLockedBlock(event.getBlock())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onDestroyBlockBeneathDoor(BlockBreakEvent event) {
		Block block = event.getBlock().getRelative(BlockFace.UP);
		if (lockedBlockManager.isLockedBlock(block)) {
			event.setCancelled(true);
			this.plugin.getLocale().getMessage(event.getPlayer(), "event.lock.cannotbreak")
				.param("%type%", block.getType())
				.param("%player%", lockedBlockManager.getLockedBlock(block).getOwner().getPlayer().getName()).send();
		}
	}
	
	@EventHandler
	public void onRedstonePowerDoor(BlockRedstoneEvent event) {
		if (lockedBlockManager.isLockedBlock(event.getBlock())) event.setNewCurrent(0);
	}
	
	@EventHandler
	public void onHopperPullItem(InventoryMoveItemEvent event) {
		if (lockedBlockManager.isLockedBlock(event.getSource().getLocation().getBlock())) event.setCancelled(true);
	}
	
}