package me.choco.locks.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;

import me.choco.locks.LockSecurity;
import me.choco.locks.utils.LockState;
import me.choco.locks.utils.LockedBlockAccessor;

public class LockedBlockGriefProtection implements Listener{
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	public LockedBlockGriefProtection(LockSecurity plugin){
		this.plugin = plugin;
		this.lockedAccessor = new LockedBlockAccessor(plugin);
	}
	
	@EventHandler
	public void onZombieBreakLockedDoor(EntityBreakDoorEvent event){
		Block block = event.getBlock();
		if (plugin.isLockable(block)){
			if (lockedAccessor.getLockedState(block).equals(LockState.LOCKED)){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event){
		Block block = event.getBlock();
		if (plugin.isLockable(block)){
			if (lockedAccessor.getLockedState(block).equals(LockState.LOCKED)){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDestroyBlockBeneathDoor(BlockBreakEvent event){
		Block block = event.getBlock().getLocation().add(0, 1, 0).getBlock();
		if (block.getType().toString().contains("DOOR") && !block.getType().equals(Material.IRON_DOOR)){
			if (lockedAccessor.getLockedState(block).equals(LockState.LOCKED)){
				event.setCancelled(true);
				plugin.sendPathMessage(event.getPlayer(), plugin.messages.getConfig().getString("Events.CannotBreak").replaceAll("%type%", block.getType().toString()).replaceAll("%player%", lockedAccessor.getBlockOwner(block)));
			}
		}
	}
	
	@EventHandler
	public void onRedstonePowerDoor(BlockRedstoneEvent event){
		Block block = event.getBlock();
		if (plugin.isLockable(block)){
			if (lockedAccessor.getLockedState(block).equals(LockState.LOCKED)){
				event.setNewCurrent(0);
			}
		}
	}
}