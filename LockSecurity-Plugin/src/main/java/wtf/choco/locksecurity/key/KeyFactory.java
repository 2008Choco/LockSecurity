package wtf.choco.locksecurity.key;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.key.KeyFactorySmithed.KeyBuilderSmithed;
import wtf.choco.locksecurity.key.KeyFactoryUnsmithed.KeyBuilderUnsmithed;

public final class KeyFactory {

    public static final KeyFactoryType<KeyBuilderSmithed> SMITHED = new KeyFactorySmithed();
    public static final KeyFactoryType<KeyBuilderUnsmithed> UNSMITHED = new KeyFactoryUnsmithed();

    public static final NamespacedKey RECIPE_UNSMITHED_KEY_UP_LEFT = LockSecurity.key("unsmithed_key_up_left");
    public static final NamespacedKey RECIPE_UNSMITHED_KEY_UP = LockSecurity.key("unsmithed_key_up");
    public static final NamespacedKey RECIPE_UNSMITHED_KEY_UP_RIGHT = LockSecurity.key("unsmithed_key_up_right");
    public static final NamespacedKey RECIPE_UNSMITHED_KEY_LEFT = LockSecurity.key("unsmithed_key_left");
    public static final NamespacedKey RECIPE_UNSMITHED_KEY_RIGHT = LockSecurity.key("unsmithed_key_right");
    public static final NamespacedKey RECIPE_UNSMITHED_KEY_DOWN_RIGHT = LockSecurity.key("unsmithed_key_down_right");
    public static final NamespacedKey RECIPE_UNSMITHED_KEY_DOWN = LockSecurity.key("unsmithed_key_down");
    public static final NamespacedKey RECIPE_UNSMITHED_KEY_DOWN_LEFT = LockSecurity.key("unsmithed_key_down_left");

    public static final NamespacedKey RECIPE_KEY_MERGE = LockSecurity.key("key_merge");
    public static final NamespacedKey RECIPE_KEY_RESET = LockSecurity.key("key_reset");

    public static final Collection<NamespacedKey> UNSMITHED_KEY_RECIPES = ImmutableSet.of(
            RECIPE_UNSMITHED_KEY_UP_LEFT,
            RECIPE_UNSMITHED_KEY_UP,
            RECIPE_UNSMITHED_KEY_UP_RIGHT,
            RECIPE_UNSMITHED_KEY_LEFT,
            RECIPE_UNSMITHED_KEY_RIGHT,
            RECIPE_UNSMITHED_KEY_DOWN_RIGHT,
            RECIPE_UNSMITHED_KEY_DOWN,
            RECIPE_UNSMITHED_KEY_DOWN_LEFT
    );

    private KeyFactory() { }

    // Utility methods
    public static ItemStack createUnsmithedKey(int amount) {
        return UNSMITHED.builder().build(amount);
    }

    public static ItemStack createUnsmithedKey() {
        return createUnsmithedKey(1);
    }

}
