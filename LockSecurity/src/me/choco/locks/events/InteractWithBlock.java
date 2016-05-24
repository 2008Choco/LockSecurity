package me.choco.locks.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.LockedBlock;
import me.choco.locks.api.event.PlayerInteractLockedBlockEvent;
import me.choco.locks.api.event.PlayerLockBlockEvent;
import me.choco.locks.api.event.PlayerUnlockBlockEvent;
import me.choco.locks.api.utils.InteractResult;
import me.choco.locks.api.utils.LSMode;
import me.choco.locks.utils.Keys;

public class InteractWithBlock implements Listener{
	LockSecurity plugin;
	Keys keys;
	public InteractWithBlock(LockSecurity plugin){
		this.plugin = plugin;
		this.keys = new Keys(plugin);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteractWithBlock(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (event.isCancelled()) return;
		
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if (!plugin.isLockable(block)) return;
			
			if (!plugin.getLocalizedData().isLockedBlock(block)){
				if (plugin.isInMode(player, LSMode.IGNORE_LOCKS)){
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockInspect.BlockNotLocked"));
					event.setCancelled(true); return;
				}
				
				if (keys.isUnsmithedKey(event.getItem())){
					event.setCancelled(true);
					if (!player.hasPermission("locks.lock")){
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.NoPermissionToLock"));
						return;
					}
					
					if (!hasLockAvailableForWorld(player)){
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.ReachedLockMaximum"));
						return;
					}
					
					PlayerLockBlockEvent lockEvent = new PlayerLockBlockEvent(player, block);
					Bukkit.getPluginManager().callEvent(lockEvent);
					if (lockEvent.isCancelled()) return;
					
					// Optional vault suppor
					if (Bukkit.getPluginManager().getPlugin("Vault") != null && plugin.economy != null){
						double lockCost = plugin.getConfig().getDouble("Vault.CostToLock");
						if (plugin.economy.getBalance(player) < lockCost){
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.NotEnoughMoney"));
							return;
						}
						
						plugin.economy.withdrawPlayer(player, lockCost);
						if (plugin.getConfig().getBoolean("Vault.DisplayWithdrawMessage"))
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.BalanceWithdrawn").replace("%money%", String.valueOf(lockCost)));
					}
					
					// Notify administrators
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.SuccessfullyLockedBlock").replaceAll("%lockID%", String.valueOf(plugin.getLocalizedData().getNextLockID())).replaceAll("%keyID%", String.valueOf(plugin.getLocalizedData().getNextKeyID())));
					for (Player p : plugin.getPlayersInMode(LSMode.ADMIN_NOTIFY)){
						plugin.sendPathMessage(p, plugin.messages.getConfig().getString("Commands.LockNotify.LockNotification")
								.replace("%player%", player.getName()).replaceAll("%type%", block.getType().toString())
								.replace("%x%", String.valueOf(block.getLocation().getBlockX()))
								.replace("%y%", String.valueOf(block.getLocation().getBlockY()))
								.replace("%z%", String.valueOf(block.getLocation().getBlockZ()))
								.replace("%world%", block.getWorld().getName())
								.replace("%lockID%", String.valueOf(plugin.getLocalizedData().getNextLockID()))
								.replace("%keyID%", String.valueOf(plugin.getLocalizedData().getNextKeyID())));
					}
					
					// Convert the key
					if (event.getItem().getAmount() > 1){
						event.getItem().setAmount(event.getItem().getAmount() - 1);
						player.getInventory().addItem(keys.createLockedKey(1, plugin.getLocalizedData().getNextKeyID()));
					}else{
						keys.convertToLockedKey(event.getItem(), plugin.getLocalizedData().getNextKeyID());
					}
					
					// Register the block
					int keyId = plugin.getLocalizedData().getNextKeyID();
					plugin.getLocalizedData().registerLockedBlock(new LockedBlock(block, player, plugin.getLocalizedData().getNextLockID(), keyId));
					dualComponentBlockHandler(block, player, keyId);
					player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 2);
				}
			}
			
			else{
				LockedBlock lockedBlock = plugin.getLocalizedData().getLockedBlock(block);
				if (plugin.isInMode(player, LSMode.INSPECT_LOCKS)){ /*Inspect Lock Mode*/
					event.setCancelled(true);
					displayBlockInfo(player, plugin.getLocalizedData().getLockedBlock(block));
				}
				
				else if (plugin.isInMode(player, LSMode.UNLOCK)){
					event.setCancelled(true);
					if (!(lockedBlock.getOwner().getUniqueId().equals(player.getUniqueId())
							&& player.hasPermission("locks.adminunlock"))){
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.NotOwner"));
						return;
					}
					
					PlayerUnlockBlockEvent unlockEvent = new PlayerUnlockBlockEvent(player, lockedBlock);
					Bukkit.getPluginManager().callEvent(unlockEvent);
					if (unlockEvent.isCancelled()) return;
					
					if (Bukkit.getPluginManager().getPlugin("Vault") != null && plugin.economy != null){
						double unlockReward = plugin.getConfig().getDouble("Vault.UnlockReward");
						plugin.economy.depositPlayer(Bukkit.getOfflinePlayer(lockedBlock.getOwner().getUniqueId()), unlockReward);
						if (plugin.getConfig().getBoolean("Vault.DisplayDepositMessage"))
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.BalanceDeposited").replace("%money%", String.valueOf(unlockReward)));
					}
					
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.BlockUnlocked").replaceAll("%lockID%", String.valueOf(lockedBlock.getLockId())));
					plugin.getLocalizedData().unregisterLockedBlock(lockedBlock);
					player.playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 2);
					plugin.removeMode(player, LSMode.UNLOCK);
				}
				
				else if (plugin.isInMode(player, LSMode.TRANSFER_LOCK)){ /*Transfer Lock Mode*/
					event.setCancelled(true);
					lockedBlock.setOwner(Bukkit.getOfflinePlayer(plugin.transferTo.get(player.getName())));
					plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.TransferLock.TransferredBlock")
						.replaceAll("%type%", block.getType().toString()).replaceAll("%player%", plugin.transferTo.get(player.getName()))
						.replaceAll("%keyID%", String.valueOf(lockedBlock.getKeyId())).replaceAll("%lockID%", String.valueOf(lockedBlock.getLockId())));
					plugin.removeMode(player, LSMode.TRANSFER_LOCK);
				}
				
				else if (plugin.getModes(player).isEmpty() || plugin.isInMode(player, LSMode.IGNORE_LOCKS)){
					if (keys.playerHasCorrectKey(block, player) || plugin.isInMode(player, LSMode.IGNORE_LOCKS)
							|| (player.isSneaking() && !player.getInventory().getItemInMainHand().getType().equals(Material.AIR))
							|| (lockedBlock.getOwner().getUniqueId().equals(player.getUniqueId()) && !plugin.getConfig().getBoolean("Griefing.OwnerRequiresKey"))){
						PlayerInteractLockedBlockEvent interactLockedBlockEvent = new PlayerInteractLockedBlockEvent(player, lockedBlock, InteractResult.SUCCESS);
						Bukkit.getPluginManager().callEvent(interactLockedBlockEvent);
						if (interactLockedBlockEvent.isCancelled()){ event.setCancelled(true); }
					}else{
						event.setCancelled(true);
						if (!player.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK)){
							PlayerInteractLockedBlockEvent interactLockedBlockEvent = new PlayerInteractLockedBlockEvent(player, lockedBlock, InteractResult.NO_KEY);
							Bukkit.getPluginManager().callEvent(interactLockedBlockEvent);
							if (!interactLockedBlockEvent.isCancelled()){
								if (plugin.getConfig().getBoolean("Aesthetics.DisplayLockedSmokeParticle"))
									player.spawnParticle(Particle.SMOKE_NORMAL, block.getLocation().add(0.5, 1, 0.5), 5, 0.1F, 0.2F, 0.1F, 0.01F);
								plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.NoKey"));
								player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 0);
							}
						}else{
							PlayerInteractLockedBlockEvent interactLockedBlockEvent = new PlayerInteractLockedBlockEvent(player, lockedBlock, InteractResult.NOT_RIGHT_KEY);
							Bukkit.getPluginManager().callEvent(interactLockedBlockEvent);
							if (!interactLockedBlockEvent.isCancelled()){
								if (plugin.getConfig().getBoolean("Aesthetics.DisplayLockedSmokeParticle"))
									player.spawnParticle(Particle.SMOKE_NORMAL, block.getLocation().add(0.5, 1, 0.5), 5, 0.1F, 0.2F, 0.1F, 0.01F);
								plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.LockPickAttempt"));
								player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_OFF, 1, 2);
							}
						}
					}
				}
			}
		}
		
		else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
			if (plugin.isInMode(player, LSMode.INSPECT_LOCKS)){ /*Inspect Lock Mode*/
				if (plugin.isLockable(block)){
					event.setCancelled(true);
					if (plugin.getLocalizedData().isLockedBlock(block)){
						displayBlockInfo(player, plugin.getLocalizedData().getLockedBlock(block));	
					}else{
						plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockInspect.BlockNotLocked"));
					}
				}
			}
		}
	}
	
	private void displayBlockInfo(Player player, LockedBlock block){
		player.sendMessage(ChatColor.GOLD + "- - - - - - " + ChatColor.DARK_AQUA + "Lock information " + ChatColor.GOLD + "- - - - - -");
		player.sendMessage(ChatColor.GOLD + "Lock ID: " + ChatColor.AQUA + block.getLockId());
		player.sendMessage(ChatColor.GOLD + "Key ID: " + ChatColor.AQUA + block.getKeyId());
		player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + block.getOwner().getName() + " (" + ChatColor.GOLD + block.getOwner().getUniqueId() + ChatColor.AQUA + ")");
		player.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.AQUA + block.getBlock().getWorld().getName() + " x:" + block.getBlock().getLocation().getBlockX() + " y:" + block.getBlock().getLocation().getBlockY() + " z:" + block.getBlock().getLocation().getBlockZ());
	}
	
	private boolean hasLockAvailableForWorld(Player player){
		return (plugin.getLocalizedData().getAllLocks(player).size() < plugin.getConfig().getInt("MaximumLocks." + player.getWorld().getName())
				|| plugin.getConfig().getInt("MaximumLocks." + player.getWorld().getName()) == -1
				|| plugin.getConfig().get("MaximumLocks." + player.getWorld().getName()) == null
				|| player.isOp());
	}
	
	private void dualComponentBlockHandler(Block block, Player owner, int keyId){
		Material type = block.getType();
		
		if (type.toString().contains("DOOR")){
			if (block.getRelative(BlockFace.UP).getType().equals(type)){
				plugin.getLocalizedData().registerLockedBlock(new LockedBlock(block.getLocation().add(0, 1, 0).getBlock(), owner, plugin.getLocalizedData().getNextLockID(), keyId));
			}else if (block.getRelative(BlockFace.DOWN).getType().equals(type)){
				plugin.getLocalizedData().registerLockedBlock(new LockedBlock(block.getLocation().add(0, 1, 0).getBlock(), owner, plugin.getLocalizedData().getNextLockID(), keyId));
			}
		}
		if (type.equals(Material.CHEST) || type.equals(Material.TRAPPED_CHEST)){
			if (block.getRelative(BlockFace.NORTH).getType().equals(type)){
				plugin.getLocalizedData().registerLockedBlock(new LockedBlock(block.getRelative(BlockFace.NORTH), owner, plugin.getLocalizedData().getNextLockID(), keyId));
			}else if (block.getRelative(BlockFace.SOUTH).getType().equals(type)){
				plugin.getLocalizedData().registerLockedBlock(new LockedBlock(block.getRelative(BlockFace.SOUTH), owner, plugin.getLocalizedData().getNextLockID(), keyId));
			}else if (block.getRelative(BlockFace.EAST).getType().equals(type)){
				plugin.getLocalizedData().registerLockedBlock(new LockedBlock(block.getRelative(BlockFace.EAST), owner, plugin.getLocalizedData().getNextLockID(), keyId));
			}else if (block.getRelative(BlockFace.WEST).getType().equals(type)){
				plugin.getLocalizedData().registerLockedBlock(new LockedBlock(block.getRelative(BlockFace.WEST), owner, plugin.getLocalizedData().getNextLockID(), keyId));
			}
		}
	}
}