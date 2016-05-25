package me.choco.locks.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.LockedBlock;
import me.choco.locks.utils.LocalizedDataHandler;

public class LockedBlockGriefProtection implements Listener{
	
	private LockSecurity plugin;
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

	private final BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST)){
			LocalizedDataHandler data = plugin.getLocalizedData();
			for (BlockFace face : faces){
				Block relative = block.getRelative(face);
				if (!(relative.getType().equals(Material.CHEST) || relative.getType().equals(Material.TRAPPED_CHEST)) ||
						!data.isLockedBlock(block.getRelative(face))) continue;
				
				LockedBlock refLock = data.getLockedBlock(relative);
				if (!refLock.getOwner().getUniqueId().equals(player.getUniqueId())){
					event.setCancelled(true);
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.DisallowedAction")
							.replace("%type%", block.getType().name())
							.replace("%owner%", refLock.getOwner().getName()));
				}
				
				data.registerLockedBlock(new LockedBlock(block, player, data.getNextLockID(), refLock.getKeyId()));
			}
		}
	}
}