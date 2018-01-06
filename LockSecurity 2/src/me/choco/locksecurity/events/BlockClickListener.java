package me.choco.locksecurity.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import me.choco.locksecurity.api.ILockSecurityPlayer;
import me.choco.locksecurity.api.ILockedBlock;
import me.choco.locksecurity.api.ILockedBlockManager;
import me.choco.locksecurity.api.IPlayerRegistry;
import me.choco.locksecurity.api.LSMode;
import me.choco.locksecurity.api.event.PlayerInteractLockedBlockEvent;
import me.choco.locksecurity.api.event.PlayerInteractLockedBlockEvent.InteractResult;
import me.choco.locksecurity.api.utils.KeyFactory;
import me.choco.locksecurity.api.utils.KeyFactory.KeyType;
import me.choco.locksecurity.data.LockedBlock;
import me.choco.locksecurity.api.event.PlayerLockBlockEvent;

public class BlockClickListener implements Listener {
	
	private final LockSecurity plugin;
	private final ILockedBlockManager lockedBlockManager;
	private final IPlayerRegistry playerRegistry;
	
	public BlockClickListener(LockSecurity plugin) {
		this.plugin = plugin;
		this.lockedBlockManager = plugin.getLockedBlockManager();
		this.playerRegistry = plugin.getPlayerRegistry();
	}
	
	@EventHandler
	public void onClickWithKey(PlayerInteractEvent event) {
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !lockedBlockManager.isLockable(event.getClickedBlock())) return;
		
		Player player = event.getPlayer();
		ILockSecurityPlayer lsPlayer = playerRegistry.getPlayer(player);
		Block block = event.getClickedBlock();
		
		ItemStack key = player.getInventory().getItemInMainHand();
		
		/* Block is locked */
		if (lockedBlockManager.isRegistered(block)) {
			if (lsPlayer.isModeEnabled(LSMode.IGNORE_LOCKS)) return;
			
			ILockedBlock lBlock = lockedBlockManager.getLockedBlock(block);
			
			// Inspect locks mode
			if (lsPlayer.isModeEnabled(LSMode.LOCK_INSPECT)) {
				event.setCancelled(true);
				
				this.displayInformation(player, lBlock);
				return;
			}
			
			// Transfer locks mode
			if (lsPlayer.isModeEnabled(LSMode.TRANSFER_LOCK)) {
				event.setCancelled(true);
				
				if (lsPlayer.getTransferTarget() != null) {
					lBlock.setOwner(lsPlayer.getTransferTarget());
					lsPlayer.setTransferTarget(null);
				}
				
				lsPlayer.disableMode(LSMode.TRANSFER_LOCK);
			}
			
			// Unlock mode
			if (lsPlayer.isModeEnabled(LSMode.UNLOCK)) {
				event.setCancelled(true);
				
				if (!player.hasPermission("locks.unlock.self")) {
					this.plugin.sendMessage(player, plugin.getLocale().getMessage("event.unlock.nopermission.self")
							.replace("%type%", block.getType().name()));
					return;
				}
				if (!player.hasPermission("locks.unlock.admin")) {
					this.plugin.sendMessage(player, plugin.getLocale().getMessage("event.unlock.nopermission.other")
							.replace("%type%", block.getType().name())
							.replace("%player%", lBlock.getOwner().getPlayer().getName()));
					return;
				}
				
				this.lockedBlockManager.unregisterBlock(lBlock);
				lsPlayer.removeBlockFromOwnership(lBlock);
				lsPlayer.disableMode(LSMode.UNLOCK);
				return;
			}
			
			// No key in hand
			if ((!lBlock.isOwner(lsPlayer) && !plugin.getConfig().getBoolean("Griefing.OwnerRequiresKey"))
					&& key == null || !key.getType().equals(Material.TRIPWIRE_HOOK)) {
				event.setCancelled(true);
				
				// PlayerInteractLockedBlockEvent - No key
				PlayerInteractLockedBlockEvent pilbe = new PlayerInteractLockedBlockEvent(lsPlayer, lBlock, InteractResult.NO_KEY);
				Bukkit.getPluginManager().callEvent(pilbe);
				
				this.plugin.sendMessage(player, plugin.getLocale().getMessage("event.key.none"));
				player.spawnParticle(Particle.SMOKE_NORMAL, block.getLocation().add(0.5, 1, 0.5), 5, 0.1F, 0.2F, 0.1F, 0.01F);
				player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 0);
				return;
			}
			
			// Key validation
			if (!lBlock.isValidKey(key)) {
				event.setCancelled(true);
				
				// PlayerInteractLockedBlockEvent - Not right key
				PlayerInteractLockedBlockEvent pilbe = new PlayerInteractLockedBlockEvent(lsPlayer, lBlock, InteractResult.NOT_RIGHT_KEY);
				Bukkit.getPluginManager().callEvent(pilbe);
				
				this.plugin.sendMessage(player, plugin.getLocale().getMessage("event.key.attemptpick"));
				player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_OFF, 1, 2);
				if (plugin.getConfig().getBoolean("Aesthetics.DisplayLockedSmokeParticle")) {
					player.spawnParticle(Particle.SMOKE_NORMAL, block.getLocation().add(0.5, 1, 0.5), 5, 0.1F, 0.2F, 0.1F, 0.01F);
				}
				return;
			}
			
			// PlayerInteractLockedBlockEvent - No key
			PlayerInteractLockedBlockEvent pilbe = new PlayerInteractLockedBlockEvent(lsPlayer, lBlock, InteractResult.SUCCESS);
			Bukkit.getPluginManager().callEvent(pilbe);
		}
		
		/* Block is unlocked */
		else{
			if (lsPlayer.isModeEnabled(LSMode.IGNORE_LOCKS)) {
				event.setCancelled(true);
				
				this.plugin.sendMessage(player, plugin.getLocale().getMessage("command.lockinspect.notlocked"));
				return;
			}
			
			if (!KeyFactory.isUnsmithedKey(key)) return;
			
			if (!player.hasPermission("locks.lock")) {
				this.plugin.sendMessage(player, plugin.getLocale().getMessage("event.lock.nopermission")
						.replace("%type%", block.getType().name()));
				event.setCancelled(true);
				return;
			}
			
			// PlayerLockBlockEvent
			PlayerLockBlockEvent plbe = new PlayerLockBlockEvent(lsPlayer, block, lockedBlockManager.getNextLockID(), lockedBlockManager.getNextKeyID());
			Bukkit.getPluginManager().callEvent(plbe);
			if (plbe.isCancelled()) return;
			
			int lockID = lockedBlockManager.getNextLockID(true), keyID = lockedBlockManager.getNextKeyID(true);
			LockedBlock lBlock = new LockedBlock(lsPlayer, block, lockID, keyID);
			this.lockedBlockManager.registerBlock(lBlock);
			
			BlockState state = block.getState();
			if (state instanceof Chest) {
				Block toLock = null;
				if ((toLock = this.getAdjacentChest(block)) != null) {
					// PlayerLockBlockEvent - Secondary block
					PlayerLockBlockEvent plbeSecondary = new PlayerLockBlockEvent(lsPlayer, toLock, lockedBlockManager.getNextLockID(), keyID);
					Bukkit.getPluginManager().callEvent(plbeSecondary);
					if (plbeSecondary.isCancelled()) return;
					
					LockedBlock lBlockSecondary = new LockedBlock(lsPlayer, toLock, lockedBlockManager.getNextLockID(true), keyID, lBlock);
					this.lockedBlockManager.registerBlock(lBlockSecondary);
				}
			}
			
			else if (state.getData() instanceof Door) {
				Door dBlock = (Door) state.getData();
				Block toLock = (dBlock.isTopHalf() ? block.getRelative(BlockFace.DOWN) : block.getRelative(BlockFace.UP));
				
				// PlayerLockBlockEvent - Secondary block
				PlayerLockBlockEvent plbeSecondary = new PlayerLockBlockEvent(lsPlayer, toLock, lockedBlockManager.getNextLockID(), lockID);
				Bukkit.getPluginManager().callEvent(plbeSecondary);
				if (plbeSecondary.isCancelled()) return;
				
				LockedBlock lBlockSecondary = new LockedBlock(lsPlayer, toLock, lockedBlockManager.getNextLockID(true), keyID, lBlock);
				this.lockedBlockManager.registerBlock(lBlockSecondary);
			}
			
			event.setCancelled(true);
			this.plugin.sendMessage(player, plugin.getLocale().getMessage("event.lock.registered")
					.replace("%keyID%", String.valueOf(keyID))
					.replace("%lockID%", String.valueOf(lockID)));
			this.giveRespectiveLockedKey(player, key, keyID);
			player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 2);
			
			// Display notification to all administrators in admin notify mode
			for (ILockSecurityPlayer admin : playerRegistry.getPlayers(LSMode.ADMIN_NOTIFY)) {
				if (!admin.getPlayer().isOnline()) return;
				
				Location location = block.getLocation();
				this.plugin.sendMessage(admin.getPlayer().getPlayer(), plugin.getLocale().getMessage("command.locknotify.notification")
						.replace("%player%", player.getName())
						.replace("%type%", block.getType().toString())
						.replace("%x%", String.valueOf(location.getBlockX()))
						.replace("%y%", String.valueOf(location.getBlockY()))
						.replace("%z%", String.valueOf(location.getBlockZ()))
						.replace("%world%", location.getWorld().getName())
						.replace("%lockID%", String.valueOf(lockID))
						.replace("%keyID%", String.valueOf(keyID)));
			}
		}
	}
	
	private void giveRespectiveLockedKey(Player player, ItemStack key, int keyID) {
		int keyAmount = key.getAmount();
		
		if (keyAmount > 1) key.setAmount(keyAmount - 1);
		else player.getInventory().setItemInMainHand(null);
		
		player.getInventory().addItem(KeyFactory.buildKey(KeyType.SMITHED).withIDs(keyID).build());
	}
	
	private static final BlockFace[] FACES = new BlockFace[]{ BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
	
	private Block getAdjacentChest(Block block) {
		for (BlockFace face : FACES) {
			Block relative = block.getRelative(face);
			if (relative.getType().equals(block.getType())) return relative;
		}
		return null;
	}
	
	private void displayInformation(Player player, ILockedBlock block) {
		OfflinePlayer owner = block.getOwner().getPlayer();
		Location location = block.getLocation();
		
		player.sendMessage(ChatColor.GOLD + "- - - - - - " + ChatColor.DARK_AQUA + "Lock information " + ChatColor.GOLD + "- - - - - -");
		player.sendMessage(ChatColor.GOLD + "Lock ID: " + ChatColor.AQUA + block.getLockID());
		player.sendMessage(ChatColor.GOLD + "Key ID: " + ChatColor.AQUA + block.getKeyID());
		player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + owner.getName() + " (" + ChatColor.GOLD + owner.getUniqueId() + ChatColor.AQUA + ")");
		player.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.AQUA + location.getWorld().getName() + " x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ());
	}
	
}