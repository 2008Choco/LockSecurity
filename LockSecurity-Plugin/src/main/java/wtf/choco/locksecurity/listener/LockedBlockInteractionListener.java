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
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Nameable;
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
import wtf.choco.locksecurity.api.key.KeyFlag;
import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.block.LockedBlockManager;
import wtf.choco.locksecurity.block.LockedMultiBlock;
import wtf.choco.locksecurity.key.KeyFactory;
import wtf.choco.locksecurity.player.LockSecurityPlayer;
import wtf.choco.locksecurity.util.ItemBuilder;
import wtf.choco.locksecurity.util.LSConstants;
import wtf.choco.locksecurity.util.LSEventFactory;

public final class LockedBlockInteractionListener implements Listener {

    private static final Set<LockedBlock> AWAITING_CONFIRMATION = new HashSet<>();

    private final LockSecurity plugin;

    public LockedBlockInteractionListener(LockSecurity plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInteractWithLockedBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // If the block isn't locked, we handle this at a higher priority down below
        LockedBlockManager manager = plugin.getLockedBlockManager();
        Block block = event.getClickedBlock();
        if (!manager.isLocked(block)) {
            return;
        }

        String blockType = block.getType().getKey().getKey().toLowerCase().replace("_", " ");
        ItemStack keyItem = event.getItem();
        Player player = event.getPlayer();
        World world = block.getWorld();
        EquipmentSlot hand = event.getHand();

        LockSecurityPlayer playerWrapper = plugin.getPlayer(player);
        LockedBlock lockedBlock = manager.getLockedBlock(block);

        // If the key is null and the player is sneaking, do some lock inspection
        if (keyItem == null && player.isSneaking() && player.hasPermission(LSConstants.LOCKSECURITY_BLOCK_INSPECT)) {
            // Call an interaction event (inspect block)
            if (!LSEventFactory.handlePlayerInteractLockedBlockEvent(playerWrapper, lockedBlock, keyItem, hand, PlayerInteractLockedBlockEvent.Action.INSPECT_BLOCK)) {
                return;
            }

            // Send the inspection message
            if (lockedBlock.hasNickname()) {
                player.sendMessage(ChatColor.DARK_GRAY + "- - - - - - " + ChatColor.GRAY + lockedBlock.getNickname() + ChatColor.DARK_GRAY + " - - - - - -");
            } else {
                player.sendMessage(ChatColor.DARK_GRAY + "- - - - - - " + ChatColor.GRAY + "(" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ") : " + block.getWorld().getName() + ChatColor.DARK_GRAY + " - - - - - -");
            }

            player.spigot().sendMessage(messagePlayerComponent(lockedBlock.getOwner().getBukkitPlayerOffline()));
            player.sendMessage(ChatColor.GRAY + "Locked at: " + ChatColor.WHITE + lockedBlock.getLockTime().format(DateTimeFormatter.RFC_1123_DATE_TIME));
            event.setCancelled(true);
            return;
        }

        // If the key is unsmithed and owner is clicking, create a new key
        if (KeyFactory.UNSMITHED.isKey(keyItem) && (lockedBlock.isOwner(player) || playerWrapper.isIgnoringLocks())) {
            // Check for key cloning permissions
            if (!player.hasPermission(LSConstants.LOCKSECURITY_BLOCK_CLONEKEY)) {
                player.sendMessage(LSConstants.WARNING_PREFIX + "You do not have permission to " + ChatColor.GREEN + "clone " + ChatColor.GRAY + "this key.");
                return;
            }

            // Call an interaction event (clone key)
            if (!LSEventFactory.handlePlayerInteractLockedBlockEvent(playerWrapper, lockedBlock, keyItem, hand, PlayerInteractLockedBlockEvent.Action.CLONE_KEY)) {
                return;
            }

            // Clone the key and give it to the player
            ItemStack smithedKeyItem = KeyFactory.SMITHED.builder().unlocks(lockedBlock).build();
            this.giveSmithedKey(player, hand, keyItem, smithedKeyItem, player.getGameMode() != GameMode.CREATIVE);
            world.playSound(block.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 2.5F);
            event.setCancelled(true);
            return;
        }

        // Validate the key in the player's hand (if any)
        if (keyItem == null || !lockedBlock.isValidKey(keyItem)) {
            // If the player is sneaking, let them at least place blocks. Or if ignoring locks, let them open the block
            if (playerWrapper.isIgnoringLocks() || player.isSneaking()) {
                return;
            }

            // Call an interaction event (incorrect key or missing key)
            if (!LSEventFactory.handlePlayerInteractLockedBlockEvent(playerWrapper, lockedBlock, keyItem, hand, keyItem != null ? PlayerInteractLockedBlockEvent.Action.INCORRECT_KEY : PlayerInteractLockedBlockEvent.Action.MISSING_KEY)) {
                return;
            }

            // Stop the player from opening the chest with the wrong key
            player.spawnParticle(Particle.SMOKE_NORMAL, block.getLocation().add(0.5, 1.2, 0.5), 5, 0.1F, 0.2F, 0.1F, 0.01F);
            world.playSound(block.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_OFF, 1, 2);
            event.setCancelled(true);
            return;
        }

        // Attempt to unlock with the valid key
        if (player.isSneaking()) {
            event.setCancelled(true);

            // Check for WorldGuard flags
            if (plugin.getWorldGuardIntegration().testIfPresent(i -> !i.queryFlagBlockUnlocking(block, player))) {
                player.sendMessage(LSConstants.WARNING_PREFIX + "You do not have permission to unlock a block here.");
                return;
            }

            // Check for unlocking permissions
            if (!player.hasPermission(LSConstants.LOCKSECURITY_BLOCK_UNLOCK)) {
                player.sendMessage(LSConstants.WARNING_PREFIX + "You do not have permission to unlock a " + ChatColor.YELLOW + blockType + ChatColor.GRAY + ".");
                return;
            }

            if (!lockedBlock.isOwner(player) && !playerWrapper.isIgnoringLocks()) {
                player.sendMessage(LSConstants.WARNING_PREFIX + "You do not own this " + ChatColor.YELLOW + blockType + ChatColor.GRAY + " and cannot unlock it.");
                return;
            }

            // Unlock confirmation
            if (AWAITING_CONFIRMATION.add(lockedBlock)) {
                // Call a BlockUnlockEvent as a request
                if (!LSEventFactory.handlePlayerBlockUnlockEvent(playerWrapper, lockedBlock, keyItem, hand, true)) {
                    AWAITING_CONFIRMATION.remove(lockedBlock);
                    return;
                }

                player.sendMessage(LSConstants.QUESTION_PREFIX + "Are you sure you want to unlock this " + ChatColor.YELLOW + blockType + ChatColor.GRAY + "?");
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Repeat this action to confirm...");
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (AWAITING_CONFIRMATION.remove(lockedBlock)) {
                        player.sendMessage(ChatColor.GRAY + "Unlock request " + ChatColor.RED + "cancelled " + ChatColor.GRAY + "for " + ChatColor.YELLOW + blockType + ChatColor.GRAY + " at " + ChatColor.AQUA + "(" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ") " + ChatColor.GRAY + "in world " + ChatColor.GREEN + world.getName() + ChatColor.GRAY + ".");
                    }
                }, 100); // 5 seconds
                return;
            }

            // Call a BlockUnlockEvent, don't run unlocking process if cancelled
            if (!LSEventFactory.handlePlayerBlockUnlockEvent(playerWrapper, lockedBlock, keyItem, hand, false)) {
                return;
            }

            // Unname the block if it had a nickname
            if (lockedBlock.hasNickname()) {
                BlockState state = block.getState();
                if (state instanceof Nameable) {
                    ((Nameable) state).setCustomName(null);
                }
            }

            // Unlock the block and unregister it
            manager.unregisterLockedBlock(lockedBlock);
            player.getInventory().setItem(hand, KeyFactory.SMITHED.refresh(keyItem));
            world.playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1.5F);
            AWAITING_CONFIRMATION.remove(lockedBlock);
            return;
        }

        /*
         * At this point, the key is valid and should open the block successfully
         */

        // Call an interaction event
        if (!LSEventFactory.handlePlayerInteractLockedBlockEvent(playerWrapper, lockedBlock, keyItem, hand, PlayerInteractLockedBlockEvent.Action.OPEN_BLOCK)) {
            return;
        }

        // Check for "break on use" on the key and break it if necessary
        if (KeyFactory.SMITHED.hasFlag(keyItem, KeyFlag.BREAK_ON_USE)) {
            player.playEffect(hand == EquipmentSlot.HAND ? EntityEffect.BREAK_EQUIPMENT_MAIN_HAND : EntityEffect.BREAK_EQUIPMENT_OFF_HAND);
            this.reduceItemInHand(player, hand, keyItem);
        }
    }

    // High priority helps ensure that world protection plugins are called first. Don't want to lock WorldGuarded blocks, for instance
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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

        // If the block is locked, we handle this at a lower priority above. If it's not an unsmithed key or lockable block, we don't really care anyways
        if (!plugin.isLockableBlock(block) || manager.isLocked(block) || !KeyFactory.UNSMITHED.isKey(keyItem)) {
            return;
        }

        event.setCancelled(true);

        // Check for WorldGuard flags
        if (plugin.getWorldGuardIntegration().testIfPresent(i -> !i.queryFlagBlockLocking(block, player))) {
            player.sendMessage(LSConstants.WARNING_PREFIX + "You do not have permission to lock a block here.");
            return;
        }

        // Check for block locking permissions
        if (!player.hasPermission(LSConstants.LOCKSECURITY_BLOCK_LOCK)) {
            player.sendMessage(LSConstants.WARNING_PREFIX + "You do not have permission to lock a block");
            return;
        }

        // Check for the maximum lock count for this world
        int maxLocks = plugin.getConfig().getInt(String.format(LSConstants.MAX_LOCKS_WORLD, world.getName()), -1);
        if (!player.hasPermission(LSConstants.LOCKSECURITY_BLOCK_LOCK_IGNORELIMIT) && maxLocks >= 0 && manager.getLockedBlocks(player).size() >= maxLocks) {
            if (maxLocks == 0) {
                player.sendMessage(LSConstants.WARNING_PREFIX + "Locks are not permitted in this world");
            } else {
                player.sendMessage(LSConstants.WARNING_PREFIX + "You have reached the maximum amount of locks in this world (" + ChatColor.YELLOW + maxLocks + ChatColor.GRAY + ")");
            }

            return;
        }

        // Create necessary data and call an event
        LockSecurityPlayer playerWrapper = plugin.getPlayer(player);
        LockedBlock lockedBlock = getLockedBlock(block, playerWrapper);
        ItemStack smithedKeyItem = KeyFactory.SMITHED.builder().unlocks(lockedBlock).build();

        PlayerBlockLockEvent blockLockEvent = LSEventFactory.callPlayerBlockLockEvent(playerWrapper, lockedBlock, keyItem, smithedKeyItem, hand);
        if (blockLockEvent.isCancelled()) {
            return;
        }

        smithedKeyItem = blockLockEvent.getSmithedKey(); // Update the key from the one set in the event

        // Register the locked block
        manager.registerLockedBlock(lockedBlock);
        this.giveSmithedKey(player, hand, keyItem, smithedKeyItem, blockLockEvent.shouldConsumeUnsmithedKey());
        world.playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1, 2);

        // Handle notifications for those that have them enabled
        BaseComponent[] notification = null;
        for (Player admin : Bukkit.getOnlinePlayers()) {
            if (admin != player && plugin.getPlayer(admin).hasLockNotifications()) {
                if (notification == null) {
                    notification = lockNotificationComponent(admin, lockedBlock, player);
                }

                admin.spigot().sendMessage(notification);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onNicknameLockedBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getMaterial() != Material.NAME_TAG) {
            return;
        }

        LockedBlockManager manager = plugin.getLockedBlockManager();
        Block block = event.getClickedBlock();
        if (!manager.isLocked(block)) {
            return;
        }

        ItemStack nametagItem = event.getItem();
        if (!nametagItem.hasItemMeta() || !nametagItem.getItemMeta().hasDisplayName()) {
            return;
        }

        event.setCancelled(true);
        String blockType = block.getType().getKey().getKey().toLowerCase().replace("_", " ");
        Player player = event.getPlayer();

        if (!player.hasPermission(LSConstants.LOCKSECURITY_BLOCK_NICKNAME)) {
            player.sendMessage(LSConstants.WARNING_PREFIX + "You do not have permission to nickname this " + ChatColor.YELLOW + blockType + ChatColor.GRAY + ".");
            return;
        }

        LockedBlock lockedBlock = manager.getLockedBlock(block);
        if (!lockedBlock.isOwner(player)) {
            player.sendMessage(LSConstants.WARNING_PREFIX + "You do not own this " + ChatColor.YELLOW + blockType + ChatColor.GRAY + " and may not give it a nickname.");
            return;
        }

        String nickname = nametagItem.getItemMeta().getDisplayName();
        lockedBlock.setNickname(nickname);

        // Set the name of Nameable tile entities as well
        BlockState state = block.getState();
        if (state instanceof Nameable) {
            ((Nameable) state).setCustomName(nickname);
            state.update(false, false);
        }

        if (player.getGameMode() != GameMode.CREATIVE) {
            this.reduceItemInHand(player, event.getHand(), nametagItem);
        }

        player.sendMessage(ChatColor.GRAY + "This block's nickname has been changed to \"" + nickname + ChatColor.GRAY + "\".");
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

    // Owned by: [owner]
    private TextComponent messagePlayerComponent(OfflinePlayer target) {
        String targetName = (target.getName() != null ? target.getName() : "Unknown");
        TextComponent clickMessageComponent = new TextComponent(targetName);
        clickMessageComponent.setColor(net.md_5.bungee.api.ChatColor.WHITE);
        clickMessageComponent.setItalic(target.getName() == null);

        if (target.isOnline()) {
            clickMessageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(target.getName() + "\n" + target.getUniqueId().toString() + "\n\nClick to message!")));
            clickMessageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + target.getName() + " "));
        } else {
            clickMessageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(targetName + "\n" + target.getUniqueId().toString() + "\n\nPlayer is offline. Cannot message.")));
        }

        TextComponent component = new TextComponent("Owned by: ");
        component.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        component.addExtra(clickMessageComponent);
        return component;
    }

    // [owner] has locked a [type] at (x, y, z) in world [world]
    private BaseComponent[] lockNotificationComponent(Player admin, LockedBlock block, Player owner) {
        ComponentBuilder componentBuilder = new ComponentBuilder(owner.getName()).color(net.md_5.bungee.api.ChatColor.GOLD);
        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(owner.getName() + "\n" + owner.getUniqueId().toString() + "\n\nClick to message!")));
        componentBuilder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + owner.getName() + " "));

        componentBuilder.append(" has locked a ", FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GRAY);
        componentBuilder.append(block.getType().getKey().getKey().replace("_", " "), FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.YELLOW);
        componentBuilder.append(" at ", FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GRAY);
        componentBuilder.append("(" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ")", FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.AQUA);

        if (admin.hasPermission(LSConstants.MINECRAFT_COMMAND_TELEPORT)) {
            componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport!")));
            componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minecraft:teleport " + admin.getName() + " " + block.getX() + " " + block.getY() + " " + block.getZ()));
        }

        componentBuilder.append(" in world ", FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GRAY);
        componentBuilder.append(block.getWorld().getName(), FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN);

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
