package me.choco.locks.events;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.PlayerUnlockBlockEvent;
import me.choco.locks.api.block.LockedBlock;
import me.choco.locks.api.utils.LSMode;

public class DestroyLockedBlock implements Listener{
	LockSecurity plugin;
	public DestroyLockedBlock(LockSecurity plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onDestroyLockedBlock(BlockBreakEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (plugin.getLocalizedData().isLockedBlock(block)){
			LockedBlock lockedBlock = plugin.getLocalizedData().getLockedBlock(block);
			if (lockedBlock.getOwner().getUniqueId().equals(player.getUniqueId())
					|| (plugin.getConfig().getBoolean("Griefing.IgnorelocksCanBreakLocks") && LSMode.getMode(player).equals(LSMode.IGNORE_LOCKS))){
				PlayerUnlockBlockEvent unlockEvent = new PlayerUnlockBlockEvent(plugin, player, block);
				Bukkit.getPluginManager().callEvent(unlockEvent);
				if (!unlockEvent.isCancelled()){
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.BlockUnregistered").replaceAll("%type%", block.getType().toString()).replace("%id%", String.valueOf(lockedBlock.getLockId())));
					plugin.getLocalizedData().unregisterLockedBlock(lockedBlock);
				}else{ event.setCancelled(true); }
			}else{
				event.setCancelled(true);
				plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.CannotBreak").replace("%type%", block.getType().name()).replace("%player%", lockedBlock.getOwner().getName()));
			}
		}
	}
}