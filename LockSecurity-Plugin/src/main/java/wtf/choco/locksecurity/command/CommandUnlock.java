package wtf.choco.locksecurity.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Nameable;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.util.LSConstants;

public final class CommandUnlock implements TabExecutor {

    // unlock [<x> <y> <z>] [world]

    private LockSecurity plugin;

    public CommandUnlock(LockSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3 || !NumberUtils.isNumber(args[0]) || !NumberUtils.isNumber(args[1]) || !NumberUtils.isNumber(args[2])) {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "A complete, valid set of " + ChatColor.YELLOW + "coordinates " + ChatColor.GRAY + "must be provided.");
            return true;
        }

        World world = (sender instanceof Entity) ? ((Entity) sender).getWorld() : null;
        if (args.length >= 4) {
            world = Bukkit.getWorld(args[3]);
        }

        if (world == null) {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "Invalid or unknown world name, " + ChatColor.AQUA + args[3]);
            return true;
        }

        int x = NumberUtils.toInt(args[0]);
        int y = NumberUtils.toInt(args[1]);
        int z = NumberUtils.toInt(args[2]);

        LockedBlock lockedBlock = plugin.getLockedBlockManager().getLockedBlock(world.getBlockAt(x, y, z));
        if (lockedBlock == null) {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "The block at " + ChatColor.YELLOW + "(" + x + ", " + y + ", " + z + ")" + ChatColor.GRAY + " in world " + ChatColor.AQUA + world.getName() + ChatColor.GRAY + " is not locked.");
            return true;
        }

        // Check for WorldGuard flags
        if (sender instanceof Player && plugin.getWorldGuardIntegration().testIfPresent(i -> !i.queryFlagBlockUnlocking(lockedBlock.getBlock(), (Player) sender))) {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "You do not have permission to unlock a block here.");
            return true;
        }

        if (!sender.hasPermission(LSConstants.LOCKSECURITY_BLOCK_UNLOCK_OTHER) && sender instanceof Player && !lockedBlock.isOwner((Player) sender)) {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "You do not have permission to unlock a " + ChatColor.YELLOW + lockedBlock.getType().getKey().getKey().toLowerCase().replace("_", " ") + ChatColor.GRAY + " you do not own.");
            return true;
        }

        // Unname the block if it had a nickname
        if (lockedBlock.hasNickname()) {
            BlockState state = lockedBlock.getBlock().getState();
            if (state instanceof Nameable) {
                ((Nameable) state).setCustomName(null);
                state.update(false, false);
            }
        }

        this.plugin.getLockedBlockManager().unregisterLockedBlock(lockedBlock);
        world.playSound(lockedBlock.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1.5F);
        sender.sendMessage(ChatColor.GRAY + "Successfully unlocked block at " + ChatColor.YELLOW + "(" + x + ", " + y + ", " + z + ")" + ChatColor.GRAY + " in world " + ChatColor.AQUA + world.getName() + ChatColor.GRAY + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Target block completion
        if (args.length >= 1 && args.length <= 4 && sender instanceof Player) {
            Player player = (Player) sender;
            Block target = player.getTargetBlockExact(6);

            if (target == null) {
                return Collections.emptyList();
            }

            // Switch statements please :((
            switch (args.length) {
                case 1: return StringUtil.copyPartialMatches(args[0], Arrays.asList(String.valueOf(target.getX())), new ArrayList<>());
                case 2: return StringUtil.copyPartialMatches(args[1], Arrays.asList(String.valueOf(target.getY())), new ArrayList<>());
                case 3: return StringUtil.copyPartialMatches(args[2], Arrays.asList(String.valueOf(target.getZ())), new ArrayList<>());
                case 4: return StringUtil.copyPartialMatches(args[3], Arrays.asList(target.getWorld().getName()), new ArrayList<>());
            }
        }

        return Collections.emptyList();
    }

}
