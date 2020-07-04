package wtf.choco.locksecurity.key;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import com.google.common.base.Preconditions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.api.impl.key.KeyBuilderUnsmithedWrapper;
import wtf.choco.locksecurity.api.key.IKeyBuilderUnsmithed;
import wtf.choco.locksecurity.api.key.KeyFlag;
import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.key.KeyFactoryUnsmithed.KeyBuilderUnsmithed;
import wtf.choco.locksecurity.util.ItemBuilder;
import wtf.choco.locksecurity.util.LSConstants;

public final class KeyFactoryUnsmithed implements KeyFactoryType<KeyBuilderUnsmithed> {

    KeyFactoryUnsmithed() { }

    @Override
    public boolean isKey(ItemStack item) {
        return builder().build().isSimilar(item);
    }

    @Override
    public KeyBuilderUnsmithed builder() {
        return new KeyBuilderUnsmithed();
    }

    @Override
    public KeyBuilderUnsmithed modify(ItemStack item) {
        return builder();
    }

    @Override
    public ItemStack merge(ItemStack firstKey, ItemStack secondKey, int amount) {
        return firstKey;
    }

    @Override
    public LockedBlock[] getUnlocks(ItemStack item) {
        return new LockedBlock[0];
    }

    @Override
    public boolean hasFlag(ItemStack item, KeyFlag flag) {
        return false;
    }

    @Override
    public Set<KeyFlag> getFlags(ItemStack item) {
        return Collections.emptySet();
    }

    @Override
    public ItemStack refresh(ItemStack key) {
        Preconditions.checkArgument(key != null, "Cannot refresh null key");
        return key.clone();
    }

    public final class KeyBuilderUnsmithed implements KeyBuilder<IKeyBuilderUnsmithed> {

        private final IKeyBuilderUnsmithed apiWrapper = new KeyBuilderUnsmithedWrapper(this);

        private KeyBuilderUnsmithed() { }

        @Override
        public ItemStack build(int amount) {
            ItemBuilder keyItem = ItemBuilder.of(Material.TRIPWIRE_HOOK)
                    .name(ChatColor.WHITE + "Unsmithed Key")
                    .lore(Arrays.asList(ChatColor.GRAY + "Unlocks: " + ChatColor.WHITE + "None"));

            // Assign model data
            FileConfiguration config = LockSecurity.getInstance().getConfig();
            int modelData = config.getInt(LSConstants.KEYS_UNSMITHED_MODEL_DATA, 0);
            if (modelData != 0) {
                keyItem.modelData(modelData);
            }

            return keyItem.amount(amount).build();
        }

        @Override
        public IKeyBuilderUnsmithed getAPIWrapper() {
            return apiWrapper;
        }

    }


}
