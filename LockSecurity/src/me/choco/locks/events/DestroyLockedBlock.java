package me.choco.locks.events;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.PlayerUnlockBlockEvent;
import me.choco.locks.api.utils.LSMode;
import me.choco.locks.utils.LockState;
import me.choco.locks.utils.LockedBlockAccessor;

public class DestroyLockedBlock implements Listener{
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	public DestroyLockedBlock(LockSecurity plugin){
		this.plugin = plugin;
		this.lockedAccessor = new LockedBlockAccessor(plugin);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDestroyLockedBlock(BlockBreakEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (plugin.isLockable(block)){
			if (lockedAccessor.getLockedState(block).equals(LockState.LOCKED)){
				String id = String.valueOf(lockedAccessor.getBlockLockID(block));
				if (plugin.locked.getConfig().get(id + ".OwnerUUID").equals(player.getUniqueId().toString()) 
						|| (LSMode.getMode(player).equals(LSMode.IGNORE_LOCKS) && plugin.getConfig().getBoolean("Griefing.IgnorelocksCanBreakLocks"))){
					PlayerUnlockBlockEvent unlockEvent = new PlayerUnlockBlockEvent(plugin, player, block);
					Bukkit.getPluginManager().callEvent(unlockEvent);
					if (!unlockEvent.isCancelled()){
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.BlockUnregistered").replaceAll("%type%", block.getType().toString()).replaceAll("%id%", id));
						lockedAccessor.setUnlocked(block);
					}else{
						event.setCancelled(true);
					}
				}else{
					event.setCancelled(true);
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.CannotBreak").replaceAll("%type%", block.getType().toString()).replaceAll("%player%", lockedAccessor.getBlockOwner(block)));
				}
			}
		}
	}
}