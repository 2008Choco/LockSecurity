package me.choco.locksecurity.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.api.KeyFactory;
import me.choco.locksecurity.api.KeyFactory.KeyType;
import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.api.event.PlayerInteractLockedBlockEvent;
import me.choco.locksecurity.api.event.PlayerInteractLockedBlockEvent.InteractResult;
import me.choco.locksecurity.api.event.PlayerLockBlockEvent;
import me.choco.locksecurity.api.utils.LSMode;
import me.choco.locksecurity.registration.LockedBlockManager;
import me.choco.locksecurity.registration.PlayerRegistry;
import me.choco.locksecurity.utils.LSPlayer;

public class BlockClickListener implements Listener {
	
	private LockSecurity plugin;
	private LockedBlockManager lockedBlockManager;
	private PlayerRegistry playerRegistry;
	public BlockClickListener(LockSecurity plugin) {
		this.plugin = plugin;
		this.lockedBlockManager = plugin.getLockedBlockManager();
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@EventHandler
	public void onClickWithKey(PlayerInteractEvent event){
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !lockedBlockManager.isLockable(event.getClickedBlock())) return;
		
		Player player = event.getPlayer();
		LSPlayer lsPlayer = playerRegistry.getPlayer(player);
		Block block = event.getClickedBlock();
		
		ItemStack key = player.getInventory().getItemInMainHand();
		
		/* Block is locked */
		if (lockedBlockManager.isRegistered(block)){
			if (lsPlayer.isModeActive(LSMode.IGNORE_LOCKS)) return;
			
			LockedBlock lBlock = lockedBlockManager.getLockedBlock(block);
			
			// Inspect locks mode
			if (lsPlayer.isModeActive(LSMode.LOCK_INSPECT)){
				event.setCancelled(true);
				
				lBlock.displayInformation(player);
				return;
			}
			
			// Transfer locks mode
			if (lsPlayer.isModeActive(LSMode.TRANSFER_LOCK)){
				event.setCancelled(true);
				
				if (lsPlayer.getToTransferTo() != null) 
					lBlock.setOwner(lsPlayer.getToTransferTo());
				lsPlayer.setToTransferTo(null);
				lsPlayer.disableMode(LSMode.TRANSFER_LOCK);
			}
			
			// Unlock mode
			if (lsPlayer.isModeActive(LSMode.UNLOCK)){
				event.setCancelled(true);
				
				lockedBlockManager.unregisterBlock(lBlock);
				lsPlayer.removeBlockFromOwnership(lBlock);
				lsPlayer.disableMode(LSMode.UNLOCK);
				return;
			}
			
			// No key in hand
			if (key == null || !key.getType().equals(Material.TRIPWIRE_HOOK)){
				event.setCancelled(true);
				
				// PlayerInteractLockedBlockEvent - No key
				PlayerInteractLockedBlockEvent pilbe = new PlayerInteractLockedBlockEvent(lsPlayer, lBlock, InteractResult.NO_KEY);
				Bukkit.getPluginManager().callEvent(pilbe);

				// TODO: "This block requires a key" message w/ (Delay buffer)
				player.spawnParticle(Particle.SMOKE_NORMAL, block.getLocation().add(0.5, 1, 0.5), 5, 0.1F, 0.2F, 0.1F, 0.01F);
				player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 0);
				return;
			}
			
			// Key validation
			if (!lBlock.isValidKey(key)){
				event.setCancelled(true);
				
				// PlayerInteractLockedBlockEvent - Not right key
				PlayerInteractLockedBlockEvent pilbe = new PlayerInteractLockedBlockEvent(lsPlayer, lBlock, InteractResult.NOT_RIGHT_KEY);
				Bukkit.getPluginManager().callEvent(pilbe);

				// TODO: "This is not the proper key for this block" message w/ (Delay buffer)
				player.spawnParticle(Particle.SMOKE_NORMAL, block.getLocation().add(0.5, 1, 0.5), 5, 0.1F, 0.2F, 0.1F, 0.01F);
				player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_OFF, 1, 2);
				return;
			}
			
			// PlayerInteractLockedBlockEvent - No key
			PlayerInteractLockedBlockEvent pilbe = new PlayerInteractLockedBlockEvent(lsPlayer, lBlock, InteractResult.SUCCESS);
			Bukkit.getPluginManager().callEvent(pilbe);
		}
		
		/* Block is unlocked */
		else{
			if (lsPlayer.isModeActive(LSMode.IGNORE_LOCKS)){
				event.setCancelled(true);
				
				// TODO "This block is not locked" message w/ (Delay buffer)
				return;
			}
			
			if (!KeyFactory.isUnsmithedKey(key)) return;
			
			// PlayerLockBlockEvent
			PlayerLockBlockEvent plbe = new PlayerLockBlockEvent(lsPlayer, block, lockedBlockManager.getNextLockID(), lockedBlockManager.getNextKeyID());
			Bukkit.getPluginManager().callEvent(plbe);
			if (plbe.isCancelled()) return;
			
			int lockID = lockedBlockManager.getNextLockID(true), keyID = lockedBlockManager.getNextKeyID(true);
			LockedBlock lBlock = new LockedBlock(lsPlayer, block, lockID, keyID);
			lockedBlockManager.registerBlock(lBlock);
			
			BlockState state = block.getState();
			if (state instanceof Chest){
				Block toLock = null;
				if ((toLock = this.getAdjacentChest(block)) != null){
					// PlayerLockBlockEvent - Secondary block
					PlayerLockBlockEvent plbeSecondary = new PlayerLockBlockEvent(lsPlayer, toLock, lockedBlockManager.getNextLockID(), keyID);
					Bukkit.getPluginManager().callEvent(plbeSecondary);
					if (plbeSecondary.isCancelled()) return;
					
					LockedBlock lBlockSecondary = new LockedBlock(lsPlayer, toLock, lockedBlockManager.getNextLockID(true), keyID, lBlock);
					lockedBlockManager.registerBlock(lBlockSecondary);
				}
			}
			
			else if (state.getData() instanceof Door){
				Door dBlock = (Door) state.getData();
				Block toLock = (dBlock.isTopHalf() ? block.getRelative(BlockFace.DOWN) : block.getRelative(BlockFace.UP));
				
				// PlayerLockBlockEvent - Secondary block
				PlayerLockBlockEvent plbeSecondary = new PlayerLockBlockEvent(lsPlayer, toLock, lockedBlockManager.getNextLockID(), lockID);
				Bukkit.getPluginManager().callEvent(plbeSecondary);
				if (plbeSecondary.isCancelled()) return;
				
				LockedBlock lBlockSecondary = new LockedBlock(lsPlayer, toLock, lockedBlockManager.getNextLockID(true), keyID, lBlock);
				lockedBlockManager.registerBlock(lBlockSecondary);
			}
			
			// TODO "You have locked the block" message
			event.setCancelled(true);
			this.giveRespectiveLockedKey(player, key, keyID);
			player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 2);
			
			// Display notification to all administrators in admin notify mode
			for (LSPlayer admin : playerRegistry.getPlayersInMode(LSMode.ADMIN_NOTIFY)){
				if (!admin.getPlayer().isOnline()) return;
				
				// TODO: Send a message
				admin.getPlayer().getPlayer().sendMessage("");
			}
		}
	}
	
	private void giveRespectiveLockedKey(Player player, ItemStack key, int keyID) {
		int keyAmount = key.getAmount();
		
		if (keyAmount > 1) key.setAmount(keyAmount - 1);
		else player.getInventory().setItemInMainHand(null);
		
		player.getInventory().addItem(KeyFactory.buildKey(KeyType.SMITHED).withIDs(keyID).build());
	}
	
	private static final BlockFace[] faces = new BlockFace[]{ BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
	private Block getAdjacentChest(Block block){
		for (BlockFace face : faces){
			Block relative = block.getRelative(face);
			if (relative.getType().equals(block.getType())) return relative;
		}
		return null;
	}
}