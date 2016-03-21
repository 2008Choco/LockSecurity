package me.choco.locks.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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
		if (!event.isCancelled()){
			if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				if (plugin.isLockable(block)){
					if (!plugin.getLocalizedData().isLockedBlock(block)){
						if (!LSMode.getMode(player).equals(LSMode.INSPECT_LOCKS)){ //TODO
							if (keys.playerHasUnsmithedKey(player)){
								event.setCancelled(true);
								if (player.hasPermission("locks.lock")){
									if (hasLockAvailableForWorld(player)){
										PlayerLockBlockEvent lockEvent = new PlayerLockBlockEvent(player, block);
										Bukkit.getPluginManager().callEvent(lockEvent);
										if (!lockEvent.isCancelled()){
											if (Bukkit.getPluginManager().getPlugin("Vault") != null){
												double lockCost = plugin.getConfig().getDouble("Vault.CostToLock");
												if (plugin.economy.getBalance(player) >= lockCost){
													plugin.economy.withdrawPlayer(player, lockCost);
													if (plugin.getConfig().getBoolean("Vault.DisplayWithdrawMessage"))
														plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.BalanceWithdrawn").replace("%money%", String.valueOf(lockCost)));
												}else{
													plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.NotEnoughMoney"));
													return;
												}
											}
											plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.SuccessfullyLockedBlock").replaceAll("%lockID%", String.valueOf(plugin.getLocalizedData().getNextLockID())).replaceAll("%keyID%", String.valueOf(plugin.getLocalizedData().getNextKeyID())));
											for (String name : plugin.adminNotify){
												plugin.sendPathMessage(Bukkit.getPlayer(name), plugin.messages.getConfig().getString("Commands.LockNotify.LockNotification")
														.replace("%player%", player.getName()).replaceAll("%type%", block.getType().toString())
														.replace("%x%", String.valueOf(block.getLocation().getBlockX()))
														.replace("%y%", String.valueOf(block.getLocation().getBlockY()))
														.replace("%z%", String.valueOf(block.getLocation().getBlockZ()))
														.replace("%world%", block.getWorld().getName())
														.replace("%lockID%", String.valueOf(plugin.getLocalizedData().getNextLockID()))
														.replace("%keyID%", String.valueOf(plugin.getLocalizedData().getNextKeyID())));
											}
											if (event.getItem().getAmount() > 1){
												event.getItem().setAmount(event.getItem().getAmount() - 1);
												player.getInventory().addItem(keys.createLockedKey(1, plugin.getLocalizedData().getNextKeyID()));
											}else{
												keys.convertToLockedKey(event.getItem(), plugin.getLocalizedData().getNextKeyID());
											}
											
											plugin.getLocalizedData().registerLockedBlock(new LockedBlock(block, player, plugin.getLocalizedData().getNextLockID(), plugin.getLocalizedData().getNextKeyID()));
											player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 2);
										}
									}else{
										plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.ReachedLockMaximum"));
									}
								}else{
									plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.NoPermissionToLock"));
								}
							}
						}else{
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockInspect.BlockNotLocked"));
							event.setCancelled(true);
						}
					}
					else if (plugin.getLocalizedData().isLockedBlock(block)){
						LockedBlock lockedBlock = plugin.getLocalizedData().getLockedBlock(block);
						if (LSMode.getMode(player).equals(LSMode.DEFAULT) || LSMode.getMode(player).equals(LSMode.IGNORE_LOCKS)){
							if (keys.playerHasCorrectKey(block, player) || LSMode.getMode(player).equals(LSMode.IGNORE_LOCKS)
									|| (player.isSneaking() && !player.getInventory().getItemInMainHand().getType().equals(Material.AIR))
									|| (lockedBlock.getOwner().getUniqueId().equals(player.getUniqueId()) && !plugin.getConfig().getBoolean("Griefing.OwnerRequiresKey"))){
								PlayerInteractLockedBlockEvent interactLockedBlockEvent = new PlayerInteractLockedBlockEvent(player, lockedBlock, InteractResult.SUCCESS);
								Bukkit.getPluginManager().callEvent(interactLockedBlockEvent);
								if (!interactLockedBlockEvent.isCancelled()){ return; }
								else{ event.setCancelled(true); }
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
						}else if (LSMode.getMode(player).equals(LSMode.INSPECT_LOCKS)){ /*Inspect Lock Mode*/
							event.setCancelled(true);
							displayBlockInfo(player, plugin.getLocalizedData().getLockedBlock(block));
						}else if (LSMode.getMode(player).equals(LSMode.UNLOCK)){ /*Unlock Mode*/
							event.setCancelled(true);
							if (lockedBlock.getOwner().getUniqueId().equals(player.getUniqueId())
									|| player.hasPermission("locks.adminunlock")){
								PlayerUnlockBlockEvent unlockEvent = new PlayerUnlockBlockEvent(player, lockedBlock);
								Bukkit.getPluginManager().callEvent(unlockEvent);
								if (!unlockEvent.isCancelled()){
									if (Bukkit.getPluginManager().getPlugin("Vault") != null){
										double unlockReward = plugin.getConfig().getDouble("Vault.UnlockReward");
										plugin.economy.depositPlayer(Bukkit.getOfflinePlayer(lockedBlock.getOwner().getUniqueId()), unlockReward);
										if (plugin.getConfig().getBoolean("Vault.DisplayDepositMessage"))
											plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.BalanceDeposited").replace("%money%", String.valueOf(unlockReward)));
									}
									plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.BlockUnlocked").replaceAll("%lockID%", String.valueOf(lockedBlock.getLockId())));
									plugin.getLocalizedData().unregisterLockedBlock(lockedBlock);
									player.playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 2);
									LSMode.setMode(player, LSMode.DEFAULT);
								}
							}else{
								plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.NotOwner"));
							}
						}else if (LSMode.getMode(player).equals(LSMode.TRANSFER_LOCK)){ /*Transfer Lock Mode*/
							event.setCancelled(true);
							lockedBlock.setOwner(Bukkit.getOfflinePlayer(plugin.transferTo.get(player.getName())));
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.TransferLock.TransferredBlock")
								.replaceAll("%type%", block.getType().toString()).replaceAll("%player%", plugin.transferTo.get(player.getName()))
								.replaceAll("%keyID%", String.valueOf(lockedBlock.getKeyId())).replaceAll("%lockID%", String.valueOf(lockedBlock.getLockId())));
							LSMode.setMode(player, LSMode.DEFAULT);
						}
					}
				}
			}else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
				if (LSMode.getMode(player).equals(LSMode.INSPECT_LOCKS)){ /*Inspect Lock Mode*/
					if (plugin.isLockable(block)){
						event.setCancelled(true);
						if (plugin.getLocalizedData().isLockedBlock(block)){
							displayBlockInfo(player, plugin.getLocalizedData().getLockedBlock(block));	
						}else{
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockInspect.BlockNotLocked"));
						}
					}
				}
			}else{
				return;
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
}