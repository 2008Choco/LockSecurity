package me.choco.locks.events;

import java.util.Iterator;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.choco.locks.LockSecurity;
import me.choco.locks.utils.LockState;
import me.choco.locks.utils.LockedBlockAccessor;

public class ExplodeLockedBlock implements Listener{
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	public ExplodeLockedBlock(LockSecurity plugin){
		this.plugin = plugin;
		this.lockedAccessor = new LockedBlockAccessor(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onExplodeLockedBlock(EntityExplodeEvent event){
		List<Block> explodedBlocks = event.blockList();
        Iterator<Block> it = explodedBlocks.iterator();
        while (it.hasNext()) {
            Block block = it.next();
			if (plugin.isLockable(block)){
				if (lockedAccessor.getLockedState(block).equals(LockState.LOCKED)){
					if (plugin.getConfig().getBoolean("Griefing.PreventLockedExplosions") == true){
						it.remove();
					}
				}
			}
        }
	}
}