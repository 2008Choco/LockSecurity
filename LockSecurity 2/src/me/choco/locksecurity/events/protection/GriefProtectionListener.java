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

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.registration.LockedBlockManager;

public class GriefProtectionListener implements Listener {
	
	private LockedBlockManager lockedBlockManager;
	public GriefProtectionListener(LockSecurity plugin) {
		this.lockedBlockManager = plugin.getLockedBlockManager();
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onZombieBreakLockedDoor(EntityBreakDoorEvent event){
		if (lockedBlockManager.isRegistered(event.getBlock())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event){
		if (lockedBlockManager.isRegistered(event.getBlock())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onDestroyBlockBeneathDoor(BlockBreakEvent event){
		Block block = event.getBlock().getRelative(BlockFace.UP);
		if (lockedBlockManager.isRegistered(block)){
			event.setCancelled(true);
//			plugin.sendPathMessage(event.getPlayer(), plugin.messages.getConfig().getString("Events.CannotBreak")
//					.replace("%type%", block.getType().name())
//					.replace("%player%", plugin.getLocalizedData().getLockedBlock(block).getOwner().getName()));
		}
	}
	
	@EventHandler
	public void onRedstonePowerDoor(BlockRedstoneEvent event){
		if (lockedBlockManager.isRegistered(event.getBlock())) event.setNewCurrent(0);
	}
	
	@EventHandler
	public void onHopperPullItem(InventoryMoveItemEvent event){
		if (lockedBlockManager.isRegistered(event.getSource().getLocation().getBlock())) event.setCancelled(true);
	}
}