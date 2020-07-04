package wtf.choco.locksecurity.api.key;

import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import wtf.choco.locksecurity.api.block.ILockedBlock;

public interface IKeyBuilderSmithed extends IKeyBuilder {

    @NotNull
    public IKeyBuilderSmithed unlocks(@NotNull ItemStack key);

    @NotNull
    public IKeyBuilderSmithed unlocks(@NotNull Iterable<? extends ILockedBlock> blocks);

    @NotNull
    public IKeyBuilderSmithed unlocks(@NotNull ILockedBlock... blocks);

    @NotNull
    public IKeyBuilderSmithed unlocks(@NotNull ILockedBlock block);

    @NotNull
    public IKeyBuilderSmithed withFlags(@NotNull KeyFlag... flags);

    @NotNull
    public IKeyBuilderSmithed withFlag(@NotNull KeyFlag flag, boolean value);

    @NotNull
    public IKeyBuilderSmithed withFlag(@NotNull KeyFlag flag);

    @NotNull
    public IKeyBuilderSmithed preventDuplication();

    @NotNull
    public IKeyBuilderSmithed preventMerging();

    @NotNull
    public IKeyBuilderSmithed preventResetting();

    @NotNull
    public IKeyBuilderSmithed breakOnUse();

    @NotNull
    public IKeyBuilderSmithed hideBlockCoordinates();

    @NotNull
    public IKeyBuilderSmithed hideFlagLore(boolean hide);

    @NotNull
    public IKeyBuilderSmithed hideFlagLore();

}
