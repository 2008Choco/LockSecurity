package wtf.choco.locksecurity.key;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.key.KeyFactorySmithed.KeyBuilderSmithed;
import wtf.choco.locksecurity.persistence.LSPersistentDataTypes;
import wtf.choco.locksecurity.util.ItemBuilder;

public final class KeyFactorySmithed implements KeyFactoryType<KeyBuilderSmithed> {

    KeyFactorySmithed() { }

    @Override
    public boolean isKey(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(LSPersistentDataTypes.KEY_UNLOCKS, LSPersistentDataTypes.LOCKED_BLOCK_ARRAY);
    }

    @Override
    public KeyBuilderSmithed builder() {
        return new KeyBuilderSmithed();
    }

    @Override
    public KeyBuilderSmithed modify(ItemStack item) {
        return new KeyBuilderSmithed(item);
    }

    @Override
    public ItemStack merge(ItemStack firstKey, ItemStack secondKey, int amount) {
        Preconditions.checkArgument(firstKey != null, "Cannot merge null key (firstKey)");
        Preconditions.checkArgument(firstKey != null, "Cannot merge null key (secondKey)");
        Preconditions.checkArgument(amount >= 0, "Amount must be >= 0");

        LockedBlock[] firstKeyUnlocks = getUnlocks(firstKey), secondKeyUnlocks = getUnlocks(secondKey);
        Set<LockedBlock> mergedUnlocks = new HashSet<>(firstKeyUnlocks.length + secondKeyUnlocks.length); // Set to prevent duplication

        this.addAll(mergedUnlocks, firstKeyUnlocks);
        this.addAll(mergedUnlocks, secondKeyUnlocks);

        int firstKeyBitmask = getBitmask(firstKey), secondKeyBitmask = getBitmask(secondKey);

        LockedBlock[] mergedUnlocksArray = mergedUnlocks.toArray(new LockedBlock[mergedUnlocks.size()]);
        return builder().unlocks(mergedUnlocksArray).applyBitmask(firstKeyBitmask & secondKeyBitmask).build(amount);
    }

    @Override
    public LockedBlock[] getUnlocks(ItemStack item) {
        Preconditions.checkArgument(item != null, "Cannot get unlockable blocks from null item");

        if (!item.hasItemMeta()) {
            return new LockedBlock[0];
        }

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.has(LSPersistentDataTypes.KEY_UNLOCKS, LSPersistentDataTypes.LOCKED_BLOCK_ARRAY) ? container.get(LSPersistentDataTypes.KEY_UNLOCKS, LSPersistentDataTypes.LOCKED_BLOCK_ARRAY) : new LockedBlock[0];
    }

    @Override
    public boolean hasFlag(ItemStack item, KeyFlag flag) {
        Preconditions.checkArgument(item != null, "Cannot get key flags from null item");

        if (!item.hasItemMeta()) {
            return false;
        }

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.has(LSPersistentDataTypes.KEY_KEY_FLAGS, PersistentDataType.INTEGER) && (container.get(LSPersistentDataTypes.KEY_KEY_FLAGS, PersistentDataType.INTEGER) & flag.getBit()) != 0;
    }

    @Override
    public Set<KeyFlag> getFlags(ItemStack item) {
        Preconditions.checkArgument(item != null, "Cannot get key flags from null item");

        if (!item.hasItemMeta()) {
            return Collections.emptySet();
        }

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (!container.has(LSPersistentDataTypes.KEY_KEY_FLAGS, PersistentDataType.INTEGER)) {
            return Collections.emptySet();
        }

        int bitmask = container.get(LSPersistentDataTypes.KEY_KEY_FLAGS, PersistentDataType.INTEGER);
        if ((bitmask = (bitmask & KeyFlag.BITMASK)) == 0) {
            return Collections.emptySet();
        }

        Set<KeyFlag> flags = EnumSet.noneOf(KeyFlag.class);
        for (KeyFlag flag : KeyFlag.values()) {
            if ((bitmask & flag.getBit()) != 0) {
                flags.add(flag);
            }
        }

        return flags;
    }

    @Override
    public ItemStack refresh(ItemStack key) {
        return modify(key).build(key.getAmount());
    }

    private <T> void addAll(Collection<T> collection, T[] values) {
        for (int i = 0; i < values.length; i++) {
            collection.add(values[i]);
        }
    }

    private int getBitmask(ItemStack item) {
        if (!item.hasItemMeta()) {
            return 0x00;
        }

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.has(LSPersistentDataTypes.KEY_KEY_FLAGS, PersistentDataType.INTEGER) ? container.get(LSPersistentDataTypes.KEY_KEY_FLAGS, PersistentDataType.INTEGER) : 0;
    }

    public final class KeyBuilderSmithed implements KeyBuilder {

        private List<LockedBlock> unlocks = Collections.emptyList();
        private int flagBitmask = 0x00;
        private boolean showFlagLore = true;

        private KeyBuilderSmithed(ItemStack key) {
            Preconditions.checkArgument(key != null, "Cannot refresh null key");
            Preconditions.checkArgument(isKey(key), "item is not a key (isKey(ItemStack) == false)");

            ItemMeta meta = key.getItemMeta();

            this.unlocks(key);
            this.flagBitmask = getBitmask(key);
            this.showFlagLore = false;

            // Hacky, but the only way to do this without having it stored as a bit in the bitmask
            List<String> lore = meta.getLore();
            if (lore != null) {
                for (KeyFlag flag : KeyFlag.values()) {
                    String loreEntry = flag.getLoreEntry();
                    if (loreEntry != null && lore.contains(loreEntry)) {
                        this.showFlagLore = true;
                        break;
                    }
                }
            }
        }

        private KeyBuilderSmithed() { }

        public KeyBuilderSmithed unlocks(ItemStack key) {
            return unlocks(KeyFactory.SMITHED.getUnlocks(key));
        }

        public KeyBuilderSmithed unlocks(Iterable<? extends LockedBlock> blocks) {
            Preconditions.checkArgument(blocks != null, "Cannot unlock null block");

            this.validateUnlocks();

            for (LockedBlock block : blocks) {
                if (block == null) {
                    continue;
                }

                Preconditions.checkArgument(!unlocks.contains(block), "Cannot add unlockable block more than once (" + block + ")");
                this.unlocks.add(block);
            }

            return this;
        }

        public KeyBuilderSmithed unlocks(LockedBlock... blocks) {
            Preconditions.checkArgument(blocks != null, "Cannot unlock null block");

            this.validateUnlocks();

            for (LockedBlock block : blocks) {
                if (block == null) {
                    continue;
                }

                Preconditions.checkArgument(!unlocks.contains(block), "Cannot add unlockable block more than once (" + block + ")");
                this.unlocks.add(block);
            }

            return this;
        }

        public KeyBuilderSmithed unlocks(LockedBlock block) {
            Preconditions.checkArgument(block != null, "Cannot unlock null block");

            this.validateUnlocks();
            this.unlocks.add(block);
            return this;
        }

        public KeyBuilderSmithed withFlags(KeyFlag... flags) {
            for (KeyFlag flag : flags) {
                this.withFlag(flag);
            }

            return this;
        }

        public KeyBuilderSmithed withFlag(KeyFlag flag, boolean value) {
            if (value) {
                withFlag(flag);
            } else {
                this.flagBitmask &= ~flag.getBit();
            }

            return this;
        }

        public KeyBuilderSmithed withFlag(KeyFlag flag) {
            this.flagBitmask |= flag.getBit();
            return this;
        }

        private KeyBuilderSmithed applyBitmask(int bitmask) {
            this.flagBitmask = bitmask & KeyFlag.BITMASK;
            return this;
        }

        public KeyBuilderSmithed preventDuplication() {
            return withFlag(KeyFlag.PREVENT_DUPLICATION);
        }

        public KeyBuilderSmithed preventMerging() {
            return withFlag(KeyFlag.PREVENT_MERGING);
        }

        public KeyBuilderSmithed preventResetting() {
            return withFlag(KeyFlag.PREVENT_RESETTING);
        }

        public KeyBuilderSmithed breakOnUse() {
            return withFlag(KeyFlag.BREAK_ON_USE);
        }

        public KeyBuilderSmithed hideBlockCoordinates() {
            return withFlag(KeyFlag.HIDE_BLOCK_COORDINATES);
        }

        public KeyBuilderSmithed hideFlagLore(boolean hide) {
            this.showFlagLore = !hide;
            return this;
        }

        public KeyBuilderSmithed hideFlagLore() {
            return hideFlagLore(true);
        }

        @Override
        public ItemStack build(int amount) {
            ItemBuilder builder = ItemBuilder.of(Material.TRIPWIRE_HOOK, amount);
            builder.name(ChatColor.WHITE + "Smithed Key");

            if (unlocks.isEmpty()) {
                return KeyFactory.createUnsmithedKey();
            }
            else {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Unlocks:");

                boolean showBlockCoordinates = (flagBitmask & KeyFlag.HIDE_BLOCK_COORDINATES.getBit()) == 0;

                LockedBlock[] blocks = unlocks.toArray(new LockedBlock[unlocks.size()]);
                for (LockedBlock block : blocks) {
                    StringBuilder blockEntry = new StringBuilder(ChatColor.GRAY + " - " + ChatColor.WHITE);
                    if (block.hasNickname()) {
                        blockEntry.append(block.getNickname()).append(ChatColor.RESET).append(' ');
                    }

                    if (!block.hasNickname() || showBlockCoordinates) {
                        blockEntry.append(ChatColor.WHITE).append('(').append(block.getX()).append(", ").append(block.getY()).append(", ").append(block.getZ()).append(") in ").append(block.getWorld().getName());
                    }

                    lore.add(blockEntry.toString());
                }

                if (showFlagLore && (flagBitmask & KeyFlag.BITMASK) != 0) {
                    lore.add("");

                    for (KeyFlag flag : KeyFlag.values()) {
                        String loreEntry = flag.getLoreEntry();
                        if ((flagBitmask & flag.getBit()) != 0 && loreEntry != null && !loreEntry.isEmpty()) {
                            lore.add(loreEntry);
                        }
                    }
                }

                builder.lore(lore);
                builder.applyPersistentData(container -> {
                    container.set(LSPersistentDataTypes.KEY_UNLOCKS, LSPersistentDataTypes.LOCKED_BLOCK_ARRAY, blocks);
                    container.set(LSPersistentDataTypes.KEY_KEY_FLAGS, PersistentDataType.INTEGER, flagBitmask & KeyFlag.BITMASK);
                });
            }

            // Assign model data
            FileConfiguration config = LockSecurity.getInstance().getConfig();
            int modelData = config.getInt("ModelData.SmithedKey", 0);
            if (modelData != 0) {
                builder.modelData(modelData);
            }

            return builder.build();
        }

        private void validateUnlocks() {
            if (unlocks.isEmpty()) {
                this.unlocks = new ArrayList<>();
            }
        }

    }

}
