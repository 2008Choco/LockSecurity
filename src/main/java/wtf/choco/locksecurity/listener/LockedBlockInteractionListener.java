package wtf.choco.locksecurity.listener;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.api.event.block.PlayerBlockLockEvent;
import wtf.choco.locksecurity.api.event.block.PlayerInteractLockedBlockEvent;
import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.block.LockedBlockManager;
import wtf.choco.locksecurity.block.LockedMultiBlock;
import wtf.choco.locksecurity.key.KeyFactory;
import wtf.choco.locksecurity.key.KeyFlag;
import wtf.choco.locksecurity.player.LockSecurityPlayer;
import wtf.choco.locksecurity.player.LockSecurityPlayerManager;
import wtf.choco.locksecurity.util.ItemBuilder;
import wtf.choco.locksecurity.util.LSEventFactory;

public final class LockedBlockInteractionListener implements Listener {

    private static final Set<LockedBlock> AWAITING_CONFIRMATION = new HashSet<>();

    private final LockSecurity plugin;

    public LockedBlockInteractionListener(LockSecurity plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onLockBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        LockedBlockManager manager = plugin.getLockedBlockManager();
        Block block = event.getClickedBlock();
        ItemStack keyItem = event.getItem();
        Player player = event.getPlayer();
        World world = block.getWorld();
        EquipmentSlot hand = event.getHand();

        if (manager.isLocked(block)) { // If block is locked...
            LockSecurityPlayer playerWrapper = plugin.getPlayerManager().get(player);
            LockedBlock lockedBlock = manager.getLockedBlock(block);

            // ... if the key is null and the player is sneaking, do some lock inspection!
            if (keyItem == null && player.isSneaking() && player.hasPermission("locksecurity.block.inspect")) {
                if (!LSEventFactory.handlePlayerInteractLockedBlockEvent(playerWrapper, lockedBlock, keyItem, hand, PlayerInteractLockedBlockEvent.Action.INSPECT_BLOCK)) {
                    return;
                }

                if (lockedBlock.hasNickname()) {
                    player.sendMessage(ChatColor.DARK_GRAY + "- - - - - - " + ChatColor.GRAY + lockedBlock.getNickname() + ChatColor.DARK_GRAY + " - - - - - -");
                } else {
                    player.sendMessage(ChatColor.DARK_GRAY + "- - - - - - " + ChatColor.GRAY + "(" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ") : " + block.getWorld().getName() + ChatColor.DARK_GRAY + " - - - - - -");
                }

                player.spigot().sendMessage(messagePlayerComponent(lockedBlock.getOwner().getOfflineBukkitPlayer()));
                player.sendMessage(ChatColor.GRAY + "Locked at: " + ChatColor.WHITE + lockedBlock.getLockTime().format(DateTimeFormatter.RFC_1123_DATE_TIME));
                event.setCancelled(true);
                return;
            }

            // ... if the key is unsmithed and owner is clicking, create a new key!
            if (KeyFactory.UNSMITHED.isKey(keyItem) && lockedBlock.isOwner(player)) {
                // ... check for key cloning permissions
                if (!player.hasPermission("locksecurity.block.clonekey")) {
                    player.sendMessage("You do not have permission to clone a key");
                    return;
                }

                // ... call interact event
                if (!LSEventFactory.handlePlayerInteractLockedBlockEvent(playerWrapper, lockedBlock, keyItem, hand, PlayerInteractLockedBlockEvent.Action.CLONE_KEY)) {
                    return;
                }

                ItemStack smithedKeyItem = KeyFactory.SMITHED.builder().unlocks(lockedBlock).build();
                this.giveSmithedKey(player, hand, keyItem, smithedKeyItem, player.getGameMode() != GameMode.CREATIVE);
                world.playSound(block.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 2.5F);
                event.setCancelled(true);
                return;
            }

            // ... validate the key in the player's hand (if any)
            if (keyItem == null || !lockedBlock.isValidKey(keyItem)) {
                // ... if the player is sneaking, let them at least place blocks
                // or, if ignoring locks, let them open it!
                if (!playerWrapper.isIgnoringLocks() && !player.isSneaking()) {
                    if (!LSEventFactory.handlePlayerInteractLockedBlockEvent(playerWrapper, lockedBlock, keyItem, hand, keyItem != null ? PlayerInteractLockedBlockEvent.Action.INCORRECT_KEY : PlayerInteractLockedBlockEvent.Action.MISSING_KEY)) {
                        return;
                    }

                    player.spawnParticle(Particle.SMOKE_NORMAL, block.getLocation().add(0.5, 1.2, 0.5), 5, 0.1F, 0.2F, 0.1F, 0.01F);
                    world.playSound(block.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_OFF, 1, 2);
                    event.setCancelled(true);
                    return;
                }

                return;
            }

            // ... attempt to unlock with the valid key
            if (player.isSneaking()) {
                event.setCancelled(true);

                // ... check for unlocking permissions
                if (!player.hasPermission("locksecurity.block.unlock")) {
                    player.sendMessage("You do not have permission to unlock a block");
                    return;
                }

                if (!lockedBlock.isOwner(player)) {
                    player.sendMessage("You do not own this block and cannot unlock it");
                    return;
                }

                // Unlock confirmation
                if (AWAITING_CONFIRMATION.add(lockedBlock)) {
                    // ... call a BlockUnlockEvent as a request
                    if (!LSEventFactory.handlePlayerBlockUnlockEvent(playerWrapper, lockedBlock, keyItem, hand, true)) {
                        AWAITING_CONFIRMATION.remove(lockedBlock);
                        return;
                    }

                    player.sendMessage("Are you sure you want to unlock this block? Repeat this action to confirm...");
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (AWAITING_CONFIRMATION.remove(lockedBlock)) {
                            player.sendMessage("Unlock request cancelled for block at (" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ") in world " + world.getName());
                        }
                    }, 100); // 5 seconds
                    return;
                }

                // ... call a BlockUnlockEvent, don't run unlocking process if cancelled
                if (!LSEventFactory.handlePlayerBlockUnlockEvent(playerWrapper, lockedBlock, keyItem, hand, false)) {
                    return;
                }

                // ... unlock the block, unregister and untrack it
                playerWrapper.untrackOwned(lockedBlock);
                manager.unregisterLockedBlock(lockedBlock);

                // ... give the player back their key
                player.getInventory().setItem(hand, KeyFactory.SMITHED.refresh(keyItem));

                world.playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1.5F);
                AWAITING_CONFIRMATION.remove(lockedBlock);
            }

            /* AT THIS POINT, THE KEY IS VALID AND SHOULD OPEN THE BLOCK SUCCESSFUL */

            if (!LSEventFactory.handlePlayerInteractLockedBlockEvent(playerWrapper, lockedBlock, keyItem, hand, PlayerInteractLockedBlockEvent.Action.OPEN_BLOCK)) {
                return;
            }

            // ... check for "break on use" on the key and... well, do it
            if (KeyFactory.SMITHED.hasFlag(keyItem, KeyFlag.BREAK_ON_USE)) {
                player.playEffect(hand == EquipmentSlot.HAND ? EntityEffect.BREAK_EQUIPMENT_MAIN_HAND : EntityEffect.BREAK_EQUIPMENT_OFF_HAND);
                this.reduceItemInHand(player, hand, keyItem);
            }
        }

        // If block is not locked...
        else if (KeyFactory.UNSMITHED.isKey(keyItem) && plugin.isLockableBlock(block)) {
            event.setCancelled(true);

            // ... check for locking permissions
            if (!player.hasPermission("locksecurity.block.lock")) {
                player.sendMessage("You do not have permission to lock a block");
                return;
            }

            // ... create necessary data for and call an event
            LockSecurityPlayerManager playerManager = plugin.getPlayerManager();
            LockSecurityPlayer playerWrapper = playerManager.get(player);
            LockedBlock lockedBlock = getLockedBlock(block, playerWrapper);
            ItemStack smithedKeyItem = KeyFactory.SMITHED.builder().unlocks(lockedBlock).build();

            // ... call the event
            PlayerBlockLockEvent blockLockEvent = LSEventFactory.callPlayerBlockLockEvent(playerWrapper, lockedBlock, keyItem, smithedKeyItem, hand);
            if (blockLockEvent.isCancelled()) {
                return;
            }

            smithedKeyItem = blockLockEvent.getSmithedKey();

            // ... register the locked block and track it
            manager.registerLockedBlock(lockedBlock);
            playerWrapper.trackOwned(lockedBlock);

            this.giveSmithedKey(player, hand, keyItem, smithedKeyItem, blockLockEvent.shouldConsumeUnsmithedKey());
            world.playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1, 2);

            // ... handle notifications for those with them enabled
            BaseComponent[] notification = null;
            for (Player admin : Bukkit.getOnlinePlayers()) {
                if (admin != player && playerManager.get(admin).hasLockNotifications()) {
                    if (notification == null) {
                        notification = lockNotificationComponent(admin, lockedBlock, player);
                    }

                    admin.spigot().sendMessage(notification);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onNicknameLockedBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        LockedBlockManager manager = plugin.getLockedBlockManager();
        Block block = event.getClickedBlock();
        if (!manager.isLocked(block) || event.getMaterial() != Material.NAME_TAG) {
            return;
        }

        ItemStack nametagItem = event.getItem();
        if (!nametagItem.hasItemMeta() || !nametagItem.getItemMeta().hasDisplayName()) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        if (!player.hasPermission("locksecurity.block.nickname")) {
            player.sendMessage("You do not have permission to nickname this " + block.getType().getKey().getKey().replace("_", " "));
            return;
        }

        LockedBlock lockedBlock = manager.getLockedBlock(block);
        if (!lockedBlock.isOwner(player)) {
            player.sendMessage("You do not own this block and may not give it a nickname");
            return;
        }

        String nickname = nametagItem.getItemMeta().getDisplayName();
        lockedBlock.setNickname(nickname);
        if (player.getGameMode() != GameMode.CREATIVE) {
            this.reduceItemInHand(player, event.getHand(), nametagItem);
        }

        player.sendMessage("This block's nickname has been changed to \"" + nickname + ChatColor.RESET + "\"");
    }

    private void giveSmithedKey(Player player, EquipmentSlot hand, ItemStack unsmithedKey, ItemStack smithedKey, boolean consume) {
        PlayerInventory inventory = player.getInventory();

        // Reduce item count for unsmithed keys
        if (consume) {
            this.reduceItemInHand(player, hand, unsmithedKey);
        }

        // Generate a new smithed key and add it to the player's inventory (drop if inventory full)
        inventory.addItem(smithedKey).forEach((index, item) -> player.getWorld().dropItemNaturally(player.getLocation(), item));
    }

    private void reduceItemInHand(Player player, EquipmentSlot hand, ItemStack item) {
        ItemStack modifiedItem = ItemBuilder.modify(item).amount(Math.max(item.getAmount() - 1, 0)).build();
        player.getInventory().setItem(hand, modifiedItem);
    }

    private TextComponent messagePlayerComponent(OfflinePlayer target) {
        TextComponent clickMessageComponent = new TextComponent(ChatColor.WHITE + target.getName());
        if (target.isOnline()) {
            clickMessageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {
                    new TextComponent(target.getName() + "\n" + target.getUniqueId().toString() + "\n\nClick to message!"),
            }));
            clickMessageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + target.getName() + " "));
        } else {
            clickMessageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {
                    new TextComponent(target.getName() + "\n" + target.getUniqueId().toString() + "\n\nPlayer is offline. Cannot message."),
            }));
        }

        TextComponent component = new TextComponent(ChatColor.GRAY + "Owned by: ");
        component.addExtra(clickMessageComponent);
        return component;
    }

    // [owner] has locked a [type] at (x, y, z) in world [world]
    private BaseComponent[] lockNotificationComponent(Player admin, LockedBlock block, Player owner) {
        ComponentBuilder componentBuilder = new ComponentBuilder(owner.getName());
        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {
                new TextComponent(owner.getName() + "\n" + owner.getUniqueId().toString() + "\n\nClick to message!"),
        }));
        componentBuilder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + owner.getName() + " "));

        componentBuilder.append(" has locked a " + block.getType().getKey().getKey().replace("_", " ") + " at ", FormatRetention.NONE);
        componentBuilder.append("(" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ") in world " + block.getWorld().getName());
        if (admin.hasPermission("minecraft.command.teleport")) {
            componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("Click to teleport!") }));
            componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minecraft:teleport " + admin.getName() + " " + block.getX() + " " + block.getY() + " " + block.getZ()));
        }

        return componentBuilder.create();
    }

    private LockedBlock getLockedBlock(Block block, LockSecurityPlayer owner) {
        BlockData blockData = block.getBlockData();
        BlockState state = block.getState();

        ZonedDateTime time = ZonedDateTime.now();
        LockedBlock lockedBlock = null;

        if (state instanceof Chest) {
            InventoryHolder holder = ((Chest) state).getInventory().getHolder();
            if (holder instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) holder;
                Block leftChest = ((Chest) doubleChest.getLeftSide()).getBlock();
                Block rightChest = ((Chest) doubleChest.getRightSide()).getBlock();
                lockedBlock = new LockedMultiBlock(leftChest, rightChest, owner, time);
            }
        }
        else if (blockData instanceof Bisected) {
            Bisected.Half half = ((Bisected) blockData).getHalf();
            Block topBlock = (half == Bisected.Half.TOP) ? block : block.getRelative(BlockFace.UP);
            Block bottomBlock = (half == Bisected.Half.BOTTOM) ? block : block.getRelative(BlockFace.DOWN);
            lockedBlock = new LockedMultiBlock(topBlock, bottomBlock, owner, time);
        }

        return (lockedBlock != null) ? lockedBlock : new LockedBlock(block, owner, time);
    }

}
