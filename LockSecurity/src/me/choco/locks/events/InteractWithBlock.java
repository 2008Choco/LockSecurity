package me.choco.locks.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.choco.locks.LockSecurity;
import me.choco.locks.api.PlayerInteractLockedBlockEvent;
import me.choco.locks.api.PlayerLockBlockEvent;
import me.choco.locks.api.PlayerUnlockBlockEvent;
import me.choco.locks.api.utils.InteractResult;
import me.choco.locks.api.utils.LSMode;
import me.choco.locks.utils.Keys;
import me.choco.locks.utils.LockState;
import me.choco.locks.utils.LockedBlockAccessor;
import me.choco.locks.utils.particles.ParticleEffect;

public class InteractWithBlock implements Listener{
	LockSecurity plugin;
	LockedBlockAccessor lockedAccessor;
	Keys keys;
	public InteractWithBlock(LockSecurity plugin){
		this.plugin = plugin;
		this.lockedAccessor = new LockedBlockAccessor(plugin);
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
					if (lockedAccessor.getLockedState(block).equals(LockState.UNLOCKED)){
						if (!LSMode.getMode(player).equals(LSMode.INSPECT_LOCKS)){
							if (keys.playerHasUnsmithedKey(player)){
								event.setCancelled(true);
								if (player.hasPermission("locks.lock")){
									if (hasLockAvailableForWorld(player)){
										PlayerLockBlockEvent lockEvent = new PlayerLockBlockEvent(plugin, player, block);
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
											plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.SuccessfullyLockedBlock").replaceAll("%lockID%", String.valueOf(lockedAccessor.getNextLockID())).replaceAll("%keyID%", String.valueOf(lockedAccessor.getNextKeyID())));
											for (String name : plugin.adminNotify){
												plugin.sendPathMessage(Bukkit.getPlayer(name), plugin.messages.getConfig().getString("Commands.LockNotify.LockNotification")
														.replaceAll("%player%", player.getName()).replaceAll("%type%", block.getType().toString())
														.replaceAll("%x%", String.valueOf(block.getLocation().getBlockX()))
														.replaceAll("%y%", String.valueOf(block.getLocation().getBlockY()))
														.replaceAll("%z%", String.valueOf(block.getLocation().getBlockZ()))
														.replaceAll("%world%", block.getWorld().getName())
														.replaceAll("%lockID%", String.valueOf(lockedAccessor.getNextLockID())).replaceAll("%keyID%", String.valueOf(lockedAccessor.getNextKeyID())));
											}
											lockedAccessor.setLocked(block, player);
											removeCurrentItem(player);
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
					else if (lockedAccessor.getLockedState(block).equals(LockState.LOCKED)){
						if (LSMode.getMode(player).equals(LSMode.DEFAULT) || LSMode.getMode(player).equals(LSMode.IGNORE_LOCKS)){
							if (lockedAccessor.playerHasCorrectKey(block, player) || LSMode.getMode(player).equals(LSMode.IGNORE_LOCKS)
									|| (player.isSneaking() && !player.getInventory().getItemInMainHand().getType().equals(Material.AIR))
									|| (lockedAccessor.getBlockOwnerUUID(block).equals(player.getUniqueId().toString()) && !plugin.getConfig().getBoolean("Griefing.OwnerRequiresKey"))){
								PlayerInteractLockedBlockEvent interactLockedBlockEvent = new PlayerInteractLockedBlockEvent(plugin, player, block, InteractResult.SUCCESS);
								Bukkit.getPluginManager().callEvent(interactLockedBlockEvent);
								if (!interactLockedBlockEvent.isCancelled()){
									return;
								}else event.setCancelled(true);
							}else{
								event.setCancelled(true);
								if (!player.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK)){
									PlayerInteractLockedBlockEvent interactLockedBlockEvent = new PlayerInteractLockedBlockEvent(plugin, player, block, InteractResult.NO_KEY);
									Bukkit.getPluginManager().callEvent(interactLockedBlockEvent);
									if (!interactLockedBlockEvent.isCancelled()){
										if (plugin.getConfig().getBoolean("Aesthetics.DisplayLockedSmokeParticle"))
											ParticleEffect.SMOKE_NORMAL.display(0.1F, 0.2F, 0.1F, 0.01F, 5, block.getLocation().add(0.5, 1.2, 0.5), player);
										plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.NoKey"));
										player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 0);
									}
								}else{
									PlayerInteractLockedBlockEvent interactLockedBlockEvent = new PlayerInteractLockedBlockEvent(plugin, player, block, InteractResult.NOT_RIGHT_KEY);
									Bukkit.getPluginManager().callEvent(interactLockedBlockEvent);
									if (!interactLockedBlockEvent.isCancelled()){
										if (plugin.getConfig().getBoolean("Aesthetics.DisplayLockedSmokeParticle"))
											ParticleEffect.SMOKE_NORMAL.display(0.1F, 0.2F, 0.1F, 0.01F, 5, block.getLocation().add(0.5, 1.2, 0.5), player);
										plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.LockPickAttempt"));
										player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_OFF, 1, 2);
									}
								}
							}
						}else if (LSMode.getMode(player).equals(LSMode.INSPECT_LOCKS)){ /*Inspect Lock Mode*/
							event.setCancelled(true);
							displayBlockInfo(player, block);
						}else if (LSMode.getMode(player).equals(LSMode.UNLOCK)){ /*Unlock Mode*/
							event.setCancelled(true);
							if (lockedAccessor.getBlockOwnerUUID(block).equals(player.getUniqueId().toString())
									|| player.hasPermission("locks.adminunlock")){
								PlayerUnlockBlockEvent unlockEvent = new PlayerUnlockBlockEvent(plugin, player, block);
								Bukkit.getPluginManager().callEvent(unlockEvent);
								if (!unlockEvent.isCancelled()){
									if (Bukkit.getPluginManager().getPlugin("Vault") != null){
										double unlockReward = plugin.getConfig().getDouble("Vault.UnlockReward");
										plugin.economy.depositPlayer(Bukkit.getPlayer(UUID.fromString(lockedAccessor.getBlockOwnerUUID(block))), unlockReward);
										if (plugin.getConfig().getBoolean("Vault.DisplayDepositMessage"))
											plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.BalanceDeposited").replace("%money%", String.valueOf(unlockReward)));
									}
									plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.BlockUnlocked").replaceAll("%lockID%", String.valueOf(lockedAccessor.getBlockLockID(block))));
									lockedAccessor.setUnlocked(block);
									player.playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 2);
									LSMode.getMode(player).equals(LSMode.DEFAULT);
								}
							}else{
								plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.NotOwner"));
							}
						}else if (LSMode.getMode(player).equals(LSMode.TRANSFER_LOCK)){ /*Transfer Lock Mode*/
							event.setCancelled(true);
							lockedAccessor.transferLock(block, Bukkit.getOfflinePlayer(plugin.transferTo.get(player.getName())));
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.TransferLock.TransferredBlock")
								.replaceAll("%type%", block.getType().toString()).replaceAll("%player%", plugin.transferTo.get(player.getName()))
								.replaceAll("%keyID%", String.valueOf(lockedAccessor.getBlockKeyID(block))).replaceAll("%lockID%", String.valueOf(lockedAccessor.getBlockLockID(block))));
							LSMode.setMode(player, LSMode.DEFAULT);
						}
					}
				}
			}else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
				if (LSMode.getMode(player).equals(LSMode.INSPECT_LOCKS)){ /*Inspect Lock Mode*/
					if (plugin.isLockable(block)){
						event.setCancelled(true);
						if (lockedAccessor.getLockedState(block).equals(LockState.LOCKED)){
							displayBlockInfo(player, block);	
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
	
	private void displayBlockInfo(Player player, Block block){
		player.sendMessage(ChatColor.GOLD + "- - - - - - " + ChatColor.DARK_AQUA + "Lock information " + ChatColor.GOLD + "- - - - - -");
		player.sendMessage(ChatColor.GOLD + "Lock ID: " + ChatColor.AQUA + lockedAccessor.getBlockLockID(block));
		player.sendMessage(ChatColor.GOLD + "Key ID: " + ChatColor.AQUA + lockedAccessor.getBlockKeyID(block));
		player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + lockedAccessor.getBlockOwner(block) + " (" + ChatColor.GOLD + lockedAccessor.getBlockOwnerUUID(block) + ChatColor.AQUA + ")");
		player.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.AQUA + block.getLocation().getWorld().getName() + " x:" + block.getLocation().getBlockX() + " y:" + block.getLocation().getBlockY() + " z:" + block.getLocation().getBlockZ());
	}
	
	private boolean hasLockAvailableForWorld(Player player){
		return (lockedAccessor.getLockCount(player) < plugin.getConfig().getInt("MaximumLocks." + player.getWorld().getName())
				|| plugin.getConfig().getInt("MaximumLocks." + player.getWorld().getName()) == -1
				|| plugin.getConfig().get("MaximumLocks." + player.getWorld().getName()) == null
				|| player.isOp());
	}
	
	private void removeCurrentItem(Player player){
		if (player.getInventory().getItemInMainHand().getAmount() > 1){
			player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
		}
		else{
			player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		}
	}
}