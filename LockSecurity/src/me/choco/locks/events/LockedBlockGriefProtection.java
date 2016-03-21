package me.choco.locks.events;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import me.choco.locks.LockSecurity;

public class LockedBlockGriefProtection implements Listener{
	LockSecurity plugin;
	public LockedBlockGriefProtection(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onZombieBreakLockedDoor(EntityBreakDoorEvent event){
		if (plugin.getLocalizedData().isLockedBlock(event.getBlock())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event){
		if (plugin.getLocalizedData().isLockedBlock(event.getBlock())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onDestroyBlockBeneathDoor(BlockBreakEvent event){
		Block block = event.getBlock().getLocation().add(0, 1, 0).getBlock();
		if (plugin.getLocalizedData().isLockedBlock(block)){
			event.setCancelled(true);
			plugin.sendPathMessage(event.getPlayer(), plugin.messages.getConfig().getString("Events.CannotBreak")
					.replace("%type%", block.getType().name())
					.replace("%player%", plugin.getLocalizedData().getLockedBlock(block).getOwner().getName()));
		}
	}
	
	@EventHandler
	public void onRedstonePowerDoor(BlockRedstoneEvent event){
		if (plugin.getLocalizedData().isLockedBlock(event.getBlock())) event.setNewCurrent(0);
	}
	
	@EventHandler
	public void onHopperPullItem(InventoryMoveItemEvent event){
		if (plugin.getLocalizedData().isLockedBlock(event.getSource().getLocation().getBlock())) event.setCancelled(true);
	}
}