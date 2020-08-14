package wtf.choco.locksecurity.integration;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldGuardIntegration {

    private final StateFlag flagBlockLocking;
    private final StateFlag flagBlockUnlocking;

    public WorldGuardIntegration(JavaPlugin plugin) {
        this.flagBlockLocking = new StateFlag("block-locking", true);
        this.flagBlockUnlocking = new StateFlag("block-unlocking", true);

        FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();

        this.registerFlag(plugin, flagRegistry, flagBlockLocking);
        this.registerFlag(plugin, flagRegistry, flagBlockUnlocking);
    }

    public boolean queryFlagBlockLocking(Block block, Player player) {
        return testFlag(block, player, flagBlockLocking);
    }

    public boolean queryFlagBlockUnlocking(Block block, Player player) {
        return testFlag(block, player, flagBlockUnlocking);
    }

    private boolean testFlag(Block block, Player player, StateFlag flag) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(localPlayer.getWorld());
        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(BlockVector3.at(block.getX(), block.getY(), block.getZ()));

        return regionSet.testState(localPlayer, flag);
    }

    private void registerFlag(JavaPlugin plugin, FlagRegistry flagRegistry, StateFlag flag) {
        try {
            flagRegistry.register(flag);
        } catch (FlagConflictException e) {
            plugin.getLogger().warning("A flag with the name \"" + flag.getName() + "\" already exists and could not be registered.");
            e.printStackTrace();
        }
    }

}
