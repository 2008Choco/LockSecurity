package me.choco.locks.events;

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

import me.choco.locks.LockSecurity;
import me.choco.locks.api.PlayerLockBlockEvent;
import me.choco.locks.api.PlayerUnlockBlockEvent;
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
						if (!plugin.inspectLockMode.contains(player.getName())){
							if (keys.playerHasUnsmithedKey(player)){
								event.setCancelled(true);
								if (player.hasPermission("locks.lock")){
									PlayerLockBlockEvent lockEvent = new PlayerLockBlockEvent(plugin, player, block);
									Bukkit.getPluginManager().callEvent(lockEvent);
									if (!lockEvent.isCancelled()){
										plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.SuccessfullyLockedBlock").replaceAll("%lockID%", String.valueOf(lockedAccessor.getNextLockID())).replaceAll("%keyID%", String.valueOf(lockedAccessor.getNextKeyID())));
										lockedAccessor.setLocked(block, player);
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
						if (!plugin.inspectLockMode.contains(player.getName()) && !plugin.unlockMode.contains(player.getName())){
							if (lockedAccessor.playerHasCorrectKey(block, player) || plugin.ignoresLocks.contains(player.getName())
									|| (player.isSneaking() && !player.getItemInHand().getType().equals(Material.AIR))
									|| (lockedAccessor.getBlockOwnerUUID(block).equals(player.getUniqueId().toString()) && !plugin.getConfig().getBoolean("Griefing.OwnerRequiresKey"))){
								return;
							}else{
								event.setCancelled(true);
								if (plugin.getConfig().getBoolean("Aesthetics.DisplayLockedSmokeParticle"))
									ParticleEffect.SMOKE_NORMAL.display(0.1F, 0.2F, 0.1F, 0.01F, 5, block.getLocation().add(0.5, 1.2, 0.5), player);
								
								if (!player.getItemInHand().getType().equals(Material.TRIPWIRE_HOOK)){
									plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.NoKey"));
									player.playSound(player.getLocation(), Sound.DOOR_CLOSE, 1, 0);
								}else{
									plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Events.LockPickAttempt"));
									player.playSound(player.getLocation(), Sound.CLICK, 1, 2);
								}
							}
						}else if (plugin.inspectLockMode.contains(player.getName())){ /*Inspect Lock Mode*/
							event.setCancelled(true);
							displayBlockInfo(player, block);
						}else if (plugin.unlockMode.contains(player.getName())){ /*Unlock Mode*/
							event.setCancelled(true);
							if (lockedAccessor.getBlockOwnerUUID(block).equals(player.getUniqueId().toString())
									|| player.hasPermission("locks.adminunlock")){
								PlayerUnlockBlockEvent unlockEvent = new PlayerUnlockBlockEvent(plugin, player, block);
								Bukkit.getPluginManager().callEvent(unlockEvent);
								if (!unlockEvent.isCancelled()){
									plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.BlockUnlocked").replaceAll("%lockID%", String.valueOf(lockedAccessor.getBlockLockID(block))));
									lockedAccessor.setUnlocked(block);
									player.playSound(block.getLocation(), Sound.DOOR_OPEN, 1, 2);
									plugin.unlockMode.remove(player.getName());
								}
							}else{
								plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.Unlock.NotOwner"));
							}
						}
					}
				}
			}else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
				if (plugin.inspectLockMode.contains(player.getName())){ /*Inspect Lock Mode*/
					if (plugin.isLockable(block)){
						if (lockedAccessor.getLockedState(block).equals(LockState.LOCKED)){
							event.setCancelled(true);
							displayBlockInfo(player, block);	
						}else{
							event.setCancelled(true);
							plugin.sendPathMessage(player, plugin.messages.getConfig().getString("Commands.LockInspect.BlockNotLocked"));
						}
					}
				}
			}else{
				return;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void displayBlockInfo(Player player, Block block){
		player.sendMessage(ChatColor.GOLD + "- - - - - - " + ChatColor.DARK_AQUA + "Lock information " + ChatColor.GOLD + "- - - - - -");
		player.sendMessage(ChatColor.GOLD + "Lock ID: " + ChatColor.AQUA + lockedAccessor.getBlockLockID(block));
		player.sendMessage(ChatColor.GOLD + "Key ID: " + ChatColor.AQUA + lockedAccessor.getBlockKeyID(block));
		player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + lockedAccessor.getBlockOwner(block) + " (" + ChatColor.GOLD + lockedAccessor.getBlockOwnerUUID(block) + ChatColor.AQUA + ")");
		player.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.AQUA + block.getLocation().getWorld().getName() + " x:" + block.getLocation().getBlockX() + " y:" + block.getLocation().getBlockY() + " z:" + block.getLocation().getBlockZ());
	}
}