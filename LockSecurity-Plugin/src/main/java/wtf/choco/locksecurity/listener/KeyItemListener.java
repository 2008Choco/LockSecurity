package wtf.choco.locksecurity.listener;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.api.event.key.PlayerDuplicateKeyEvent;
import wtf.choco.locksecurity.api.event.key.PlayerMergeKeyEvent;
import wtf.choco.locksecurity.api.event.key.PlayerResetKeyEvent;
import wtf.choco.locksecurity.api.key.KeyFlag;
import wtf.choco.locksecurity.key.KeyFactory;
import wtf.choco.locksecurity.util.ItemBuilder;
import wtf.choco.locksecurity.util.LSConstants;
import wtf.choco.locksecurity.util.LSEventFactory;

public final class KeyItemListener implements Listener {

    public static final ItemStack IMPOSSIBLE_RECIPE_RESULT = ItemBuilder.of(Material.BARRIER)
            .name(ChatColor.GRAY + "Impossible")
            .lore(ChatColor.WHITE + "If you're seeing this item in a recipe,",
                    ChatColor.WHITE + "something went wrong...")
            .build();

    private static final NamespacedKey CHEST_RECIPE_KEY = Material.CHEST.getKey();

    private final LockSecurity plugin;

    public KeyItemListener(LockSecurity plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAttemptToPlaceKey(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (KeyFactory.UNSMITHED.isKey(item) || KeyFactory.SMITHED.isKey(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDiscoverChestRecipe(PlayerRecipeDiscoverEvent event) {
        Player player = event.getPlayer();
        if (event.getRecipe().equals(CHEST_RECIPE_KEY) && player.hasPermission("locksecurity.crafting.unsmithed")) {
            player.discoverRecipes(KeyFactory.UNSMITHED_KEY_RECIPES);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("locksecurity.crafting.unsmithed")) {
            player.discoverRecipes(KeyFactory.UNSMITHED_KEY_RECIPES);
        } else {
            player.undiscoverRecipes(KeyFactory.UNSMITHED_KEY_RECIPES);
        }
    }

    @EventHandler
    public void onKeyRecipe(PrepareItemCraftEvent event) {
        HumanEntity viewer = event.getView().getPlayer();
        Recipe recipe = event.getRecipe();
        CraftingInventory inventory = event.getInventory();
        FileConfiguration config = plugin.getConfig();

        if (recipe instanceof ShapedRecipe && KeyFactory.UNSMITHED_KEY_RECIPES.contains(((ShapedRecipe) recipe).getKey()) && !viewer.hasPermission("locksecurity.crafting.unsmithed")) {
            inventory.setResult(null);
            return;
        }

        if (!(recipe instanceof ShapelessRecipe) || !(viewer instanceof Player)) {
            return;
        }

        Player player = (Player) viewer;
        NamespacedKey recipeKey = ((ShapelessRecipe) recipe).getKey();

        // Merge two smithed keys
        if (recipeKey.equals(KeyFactory.RECIPE_KEY_MERGE)) {
            // Try to find the keys in the crafting matrix
            ItemStack firstKey = null, secondKey = null;
            for (ItemStack matrixItem : inventory.getMatrix()) {
                if (matrixItem == null) {
                    continue;
                }

                if (firstKey == null) {
                    firstKey = matrixItem;
                }
                else {
                    secondKey = matrixItem;
                    break;
                }
            }

            // Here, two unsmithed keys should just keep the result as null... There's no benefit to setting the result to itself
            inventory.setResult(null);
            if (firstKey == null || secondKey == null) {
                return;
            }

            Set<KeyFlag> firstKeyFlags = KeyFactory.SMITHED.getFlags(firstKey);
            Set<KeyFlag> secondKeyFlags = KeyFactory.SMITHED.getFlags(secondKey);

            if (KeyFactory.SMITHED.isKey(firstKey)) {
                // Two smithed keys - merging the two keys (if not already the same)
                if (KeyFactory.SMITHED.isKey(secondKey) && !firstKey.isSimilar(secondKey) && config.getBoolean(LSConstants.KEYS_ALLOW_KEY_MERGING, true) && player.hasPermission("locksecurity.crafting.merge")) {
                    // Check key flags. If either prevent merging, don't merge
                    if (firstKeyFlags.contains(KeyFlag.PREVENT_MERGING) || secondKeyFlags.contains(KeyFlag.PREVENT_MERGING)) {
                        return;
                    }

                    ItemStack mergedKey = KeyFactory.SMITHED.merge(firstKey, secondKey);

                    // If either of the keys are identical to the result, the merge does not need to happen. No change
                    if (!firstKey.isSimilar(mergedKey) && !secondKey.isSimilar(mergedKey)) {
                        PlayerMergeKeyEvent mergeKeyEvent = LSEventFactory.callPlayerMergeKeyEvent(player, firstKey, secondKey, mergedKey);
                        if (!mergeKeyEvent.isCancelled()) {
                            inventory.setResult(mergeKeyEvent.getOutput());
                        }
                    }
                }

                // A smithed key and an unsmithed key - Duplicate the smithed key
                else if (KeyFactory.UNSMITHED.isKey(secondKey) && config.getBoolean(LSConstants.KEYS_ALLOW_KEY_DUPLICATION, true) && player.hasPermission("locksecurity.crafting.duplicate") && !firstKeyFlags.contains(KeyFlag.PREVENT_DUPLICATION)) {
                    PlayerDuplicateKeyEvent duplicateKeyEvent = LSEventFactory.callPlayerDuplicateKeyEvent(player, firstKey, secondKey, ItemBuilder.modify(firstKey).amount(2).build());
                    if (!duplicateKeyEvent.isCancelled()) {
                        inventory.setResult(duplicateKeyEvent.getOutput());
                    }
                }
            }

            // A smithed key and an unsmithed key - Duplicate the smithed key
            else if (KeyFactory.UNSMITHED.isKey(firstKey) && KeyFactory.SMITHED.isKey(secondKey) && config.getBoolean(LSConstants.KEYS_ALLOW_KEY_DUPLICATION, true) && player.hasPermission("locksecurity.crafting.duplicate") && !firstKeyFlags.contains(KeyFlag.PREVENT_DUPLICATION)) {
                PlayerDuplicateKeyEvent duplicateKeyEvent = LSEventFactory.callPlayerDuplicateKeyEvent(player, secondKey, firstKey, ItemBuilder.modify(secondKey).amount(2).build());
                if (!duplicateKeyEvent.isCancelled()) {
                    inventory.setResult(duplicateKeyEvent.getOutput());
                }
            }
        }

        // Convert a smithed key back into a smithed key
        else if (recipeKey.equals(KeyFactory.RECIPE_KEY_RESET)) {
            inventory.setResult(null);

            if (!config.getBoolean(LSConstants.KEYS_ALLOW_KEY_RESETTING, true)) {
                return;
            }

            // Try to find the key in the crafting matrix
            ItemStack keyItem = null;
            for (ItemStack matrixItem : inventory.getMatrix()) {
                if ((keyItem = matrixItem) != null) {
                    break;
                }
            }

            boolean preventReset = KeyFactory.SMITHED.hasFlag(keyItem, KeyFlag.PREVENT_RESETTING);
            if (KeyFactory.SMITHED.isKey(keyItem) && player.hasPermission("locksecurity.crafting.reset") && !preventReset) {
                PlayerResetKeyEvent resetKeyEvent = LSEventFactory.callPlayerResetKeyEvent(player, keyItem, KeyFactory.createUnsmithedKey());
                if (!resetKeyEvent.isCancelled()) {
                    inventory.setResult(resetKeyEvent.getOutput());
                }
            }
        }
    }

    @EventHandler
    public void onAttemptCraftImpossibleResult(CraftItemEvent event) {
        if (IMPOSSIBLE_RECIPE_RESULT.isSimilar(event.getCurrentItem())) {
            event.setCancelled(true);
            event.getViewers().forEach(e -> {
                if (e instanceof Player) {
                    ((Player) e).playSound(event.getInventory().getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1.6F);
                }
            });
        }
    }

}
