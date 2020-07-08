package wtf.choco.locksecurity.api.impl;

import com.google.common.base.Preconditions;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.api.ILockSecurity;
import wtf.choco.locksecurity.api.block.ILockedBlockManager;
import wtf.choco.locksecurity.api.impl.key.KeyFactoryWrapper;
import wtf.choco.locksecurity.api.key.IKeyBuilder;
import wtf.choco.locksecurity.api.key.IKeyBuilderSmithed;
import wtf.choco.locksecurity.api.key.IKeyBuilderUnsmithed;
import wtf.choco.locksecurity.api.key.IKeyFactory;
import wtf.choco.locksecurity.api.player.ILockSecurityPlayer;
import wtf.choco.locksecurity.key.KeyFactory;

public final class LockSecurityWrapper implements ILockSecurity {

    private final LockSecurity plugin;
    private final IKeyFactory<IKeyBuilderUnsmithed> unsmithedKeyFactory;
    private final IKeyFactory<IKeyBuilderSmithed> smithedKeyFactory;

    public LockSecurityWrapper(LockSecurity plugin) {
        this.plugin = plugin;
        this.unsmithedKeyFactory = new KeyFactoryWrapper<>(KeyFactory.UNSMITHED);
        this.smithedKeyFactory = new KeyFactoryWrapper<>(KeyFactory.SMITHED);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IKeyBuilder> IKeyFactory<T> getKeyFactory(Class<T> type) {
        Preconditions.checkArgument(type != null, "type cannot be null");

        if (type == IKeyBuilderUnsmithed.class) {
            return (IKeyFactory<T>) unsmithedKeyFactory;
        } else if (type == IKeyBuilderSmithed.class) {
            return (IKeyFactory<T>) smithedKeyFactory;
        }

        throw new UnsupportedOperationException("Unknown key factory type for builder: " + type.getName());
    }

    @Override
    public ILockSecurityPlayer getLockSecurityPlayer(OfflinePlayer player) {
        Preconditions.checkArgument(player != null, "player cannot be null");
        return plugin.getPlayer(player).getAPIWrapper();
    }

    @Override
    public ILockedBlockManager getLockedBlockManager() {
        return plugin.getLockedBlockManager().getAPIWrapper();
    }

    @Override
    public boolean isLockable(Material material) {
        Preconditions.checkArgument(material != null, "material cannot be null");
        return plugin.isLockable(material);
    }

}
