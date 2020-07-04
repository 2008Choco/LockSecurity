package wtf.choco.locksecurity.api.impl.key;

import org.bukkit.inventory.ItemStack;

import wtf.choco.locksecurity.api.key.IKeyBuilderUnsmithed;
import wtf.choco.locksecurity.key.KeyFactoryUnsmithed.KeyBuilderUnsmithed;

public final class KeyBuilderUnsmithedWrapper implements IKeyBuilderUnsmithed {

    private final KeyBuilderUnsmithed handle;

    public KeyBuilderUnsmithedWrapper(KeyBuilderUnsmithed handle) {
        this.handle = handle;
    }

    @Override
    public ItemStack build(int amount) {
        return handle.build(amount);
    }

}
