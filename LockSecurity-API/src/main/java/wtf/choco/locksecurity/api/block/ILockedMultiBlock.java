package wtf.choco.locksecurity.api.block;

import org.bukkit.block.Block;

import org.jetbrains.annotations.NotNull;

public interface ILockedMultiBlock extends ILockedBlock {

    @NotNull
    public Block getSecondaryBlock();

    public int getXSecondary();

    public int getYSecondary();

    public int getZSecondary();

}
