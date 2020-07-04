package wtf.choco.locksecurity.api.key;

import java.util.Set;

import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import wtf.choco.locksecurity.api.LockSecurityAPI;
import wtf.choco.locksecurity.api.block.ILockedBlock;

public interface IKeyFactory<T extends IKeyBuilder> {

    public static final IKeyFactory<@NotNull IKeyBuilderUnsmithed> UNSMITHED = LockSecurityAPI.getKeyFactory(IKeyBuilderUnsmithed.class);
    public static final IKeyFactory<@NotNull IKeyBuilderSmithed> SMITHED = LockSecurityAPI.getKeyFactory(IKeyBuilderSmithed.class);

    public boolean isKey(@Nullable ItemStack item);

    @NotNull
    public T builder();

    @NotNull
    public T modify(ItemStack item);

    @NotNull
    public ItemStack merge(@Nullable ItemStack firstKey, @Nullable ItemStack secondKey, int amount);

    @NotNull
    public ItemStack merge(@Nullable ItemStack firstKey, @Nullable ItemStack secondKey);

    @NotNull
    public ILockedBlock[] getUnlocks(@NotNull ItemStack item);

    public boolean hasFlag(@NotNull ItemStack item, @NotNull KeyFlag flag);

    @NotNull
    public Set<KeyFlag> getFlags(@NotNull ItemStack item);

    @NotNull
    public ItemStack refresh(@NotNull ItemStack key);

}
